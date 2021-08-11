package com.example.jnibsdiff

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.loader.app.LoaderManager
import com.example.jnibsdiff.databinding.ActivityMainBinding
import com.example.jnibsdiff.loader.DataLoader
import com.example.jnibsdiff.loader.LoaderCallback
import com.example.jnibsdiff.loader.ResultCallback
import com.example.jnibsdiff.utils.OLog
import java.io.File

class MainActivity : AppCompatActivity(),ResultCallback<File> {

    private val LOADER_ID = 0
    private val callback = LoaderCallback(this, this)


    private lateinit var binding: ActivityMainBinding

    companion object {
        // Used to load the 'jnibsdiff' library on application startup.
        init {
            OLog.d("MainActivity init")
            System.loadLibrary("jnibsdiff")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sampleText.text = "当前应用版本： ${BuildConfig.VERSION_NAME}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 1000)
            }
        }

        // 初始化Loader
        LoaderManager.getInstance(this).initLoader(LOADER_ID, Bundle.EMPTY, callback)

        binding.updateApk.setOnClickListener {
            updateApk()
        }
    }

    private fun updateApk(){
        // 启动Loader
        val loader = LoaderManager.getInstance(this).getLoader<DataLoader>(LOADER_ID)
        loader?.forceLoad()
    }

    override fun onResult(file: File?) {
        OLog.d("onResult, thread:${Thread.currentThread().name}")
        file?.let {
            if (!file.exists()) return

            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES
                    .M){
                val fileUri = FileProvider.getUriForFile(this@MainActivity, this@MainActivity.applicationInfo.packageName+".fileprovider",
                file
                )
                intent.setDataAndType(fileUri,"application/vnd.android.package-archive")
            }else{
                intent.setDataAndType(
                    Uri.fromFile(file),
                    "application/vnd.android.package-archive"
                )
            }
            this@MainActivity.startActivity(intent)
        };{
            Toast.makeText(this@MainActivity, "差分包未找到",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LoaderManager.getInstance(this).destroyLoader(LOADER_ID)
    }
}