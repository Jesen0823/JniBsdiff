# JniBsdiff
使用bsdiff-v4.3差分异apk更新应用

# 增量更新

> 在目前的大部分热门应用中（QQ、微信、抖音等）都包含了一个名称类似 libbspatch.so的动态库，而且通过：
>
> ```nm -D xx.so```()
>命令对应：\ndk\xx.xx.xx\toolchains\arm-linux-androideabi-4.9\prebuilt\windows-x86_64\bin\arm-linux-androideabi-nm.exe
>
> 查看这些库中的符号内容都差不多，因此它们肯定是实现了同一件事情，也就是 ”增量更新“



## 好处

​	增量更新相较于全量更新的好处不言而喻，利用差分算法获得1.0版本到2.0版本的差分包，这样在安装了1.0的设备上只要下载这个差分包就能够完成由1.0-2.0的更新。比如：
存在一个1.0版本的apk
	
![apk1](https://user-images.githubusercontent.com/36436771/129043841-37765a70-b879-4cc9-abeb-ef667bc327da.png)

然后需要升级到2.0版本，而2.0版本的apk为
	
![apk2](https://user-images.githubusercontent.com/36436771/129043900-a37e4387-7230-44fd-b238-fd60ca2f5927.png)

这样如果进行全量更新则需要下载完整的76.6M大小的apk文件，进行安装。而如果使用增量更新则只需要下载如下 50.7M的差分包。
	
![patch](https://user-images.githubusercontent.com/36436771/129044854-dfe087dc-b803-49db-9d77-1219bbaf48b5.png)


下载数据减少了26M。这样做的好处不仅仅在于对于流量的节省。对于用户来说现在流量可能并不值钱，或者使用wifi再进行更新，但是从下载时间能够得到一个良好的优化，同时也减小了服务器的压力。

## 实现

​	需要实现增量更新，现在有各种开源的制作与合并差分包的开源库，比如：bsdiff、hdiff等等。因此我们只需要获得源码来使用即可。

> bsdiff 下载地址：
>
> http://www.daemonology.net/bsdiff/
>
> bsdiff 依赖bzip2(zip压缩库)
>
> https://nchc.dl.sourceforge.net/project/gnuwin32/bzip2/1.0.5/bzip2-1.0.5-src.zip

下载完成后解压：

```
解压：
tar zxvf  bsdiff-4.3.tar.gz
```
![bsdiff源码](https://user-images.githubusercontent.com/36436771/129045003-6db0ad33-460c-4be6-9f69-4c112fb40ca6.png)



源码中  `Makefile`

bsdiff: 比较两个文件的二进制数据，生成差分包

bspatch：合并旧的文件与差分包，生成新文件

很显然，bspatch我们需要在Android环境下来执行，而bsdiff 一般会在你的存储服务器当中执行即电脑环境下执行(win或linux)

### bsdiff

​	对于windows，可以直接从 https://github.com/cnSchwarzer/bsdiff-win/releases 下载。

​	而Linux/Mac则可以自行编译：

在Linux中的解压目录直接执行：`make` 会产生错误。需要修改：

```makefile
install:
	${INSTALL_PROGRAM} bsdiff bspatch ${PREFIX}/bin
.ifndef WITHOUT_MAN
	${INSTALL_MAN} bsdiff.1 bspatch.1 ${PREFIX}/man/man1
.endif
#上面这段makefile片段显然有问题(指令必须以tab开头)
#因此需要修改为：
install:
	${INSTALL_PROGRAM} bsdiff bspatch ${PREFIX}/bin
	.ifndef WITHOUT_MAN
	${INSTALL_MAN} bsdiff.1 bspatch.1 ${PREFIX}/man/man1
	.endif
#也就是在 `.if` 和 `.endif` 前加一个 tab
```

> 修改后再来执行`make ` 如果出现找不到bzip2 `no file found bzlib.h`之类的错误，则需要先安装bzip2：
>
> Ubuntu:
>
>  ```apt install libbz2-dev ```
>
> Centos:
>
> ```yum -y install bzip2-devel.x86_64  ```
>
> Mac:
>
> ```brew install bzip2```
>
> <!--install 不知道填什么，可以使用包管理器进行搜索： -->
>
> <!-- apt/yum/brew search bzip2 -->

如果执行make出现

```shell
bsdiff.c:(.text.startup+0x2aa): undefined reference to `BZ2_bzWriteOpen'
bsdiff.c:(.text.startup+0xcfa): undefined reference to `BZ2_bzWrite'
bsdiff.c:(.text.startup+0xe37): undefined reference to `BZ2_bzWrite'
bsdiff.c:(.text.startup+0xf80): undefined reference to `BZ2_bzWrite'
bsdiff.c:(.text.startup+0xfe1): undefined reference to `BZ2_bzWriteClose'
bsdiff.c:(.text.startup+0x1034): undefined reference to `BZ2_bzWriteOpen'
bsdiff.c:(.text.startup+0x105c): undefined reference to `BZ2_bzWrite'
bsdiff.c:(.text.startup+0x1082): undefined reference to `BZ2_bzWriteClose'
bsdiff.c:(.text.startup+0x10d5): undefined reference to `BZ2_bzWriteOpen'
bsdiff.c:(.text.startup+0x1100): undefined reference to `BZ2_bzWrite'
bsdiff.c:(.text.startup+0x1126): undefined reference to `BZ2_bzWriteClose'
```

则修改Makefile为：

```makefile
CFLAGS          +=      -O3 -lbz2

PREFIX          ?=      /usr/local
INSTALL_PROGRAM ?=      ${INSTALL} -c -s -m 555
INSTALL_MAN     ?=      ${INSTALL} -c -m 444

all:            bsdiff bspatch
bsdiff:         bsdiff.c
        cc bsdiff.c ${CFLAGS} -o bsdiff  #增加
bspatch:        bspatch.c
        cc bspatch.c ${CFLAGS} -o bspatch #增加

install:
        ${INSTALL_PROGRAM} bsdiff bspatch ${PREFIX}/bin
        .ifndef WITHOUT_MAN
        ${INSTALL_MAN} bsdiff.1 bspatch.1 ${PREFIX}/man/man1
        .endif
```



而bspatch则需要在Android工程中使用NDK来进行编译使用





