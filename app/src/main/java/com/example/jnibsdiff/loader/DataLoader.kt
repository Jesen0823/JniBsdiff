package com.example.jnibsdiff.loader

import android.content.Context
import android.os.Environment
import androidx.loader.content.AsyncTaskLoader
import com.example.jnibsdiff.AppGlobal
import com.example.jnibsdiff.BsPatcher
import com.example.jnibsdiff.utils.OLog
import java.io.File
import java.io.IOException

class DataLoader(context: Context):AsyncTaskLoader<File>(context) {
    var parentPath:File? = null
    lateinit var output:String

    override fun onStartLoading() {
        super.onStartLoading()
        OLog.d("onStartLoading, thread:${Thread.currentThread().name}")
        parentPath = AppGlobal.application?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        OLog.d("DataLoader, onStartLoading")
        OLog.d("parentPath:${parentPath?.absolutePath}")
        //forceLoad()
    }

    override fun loadInBackground(): File? {
        OLog.d("loadInBackground, thread:${Thread.currentThread().name}")
        val patch = File(parentPath,"patch").absolutePath
        val oldApk = File(parentPath,"old.apk").absolutePath
        output = createNewApk().absolutePath
        return if (!File(patch).exists()){
            OLog.d("loadInBackground, patch not exists")
            null
        } else {
            BsPatcher.bsPatch(oldApk, patch, output)
            File(output)
        }
    }

    private fun createNewApk(): File {
        OLog.d("createNewApk, thread:${Thread.currentThread().name}")
        val newApk = File(parentPath,"bsdiff.apk")
        OLog.d("createNewApk, newApk:${newApk.absolutePath}")
        if (!newApk.exists()){
            try {
                newApk.createNewFile()
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
        return newApk
    }


    override fun deliverResult(data: File?) {
        super.deliverResult(data)
        OLog.d("DataLoader, deliverResult: ${data?.absolutePath}")

    }
}