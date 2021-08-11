package com.example.jnibsdiff.loader

import android.content.Context
import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.example.jnibsdiff.utils.OLog
import java.io.File

class LoaderCallback(context: Context, resultCallback:ResultCallback<File>):
LoaderManager.LoaderCallbacks<File>{

    private val mContext = context
    private val mResultCallback = resultCallback

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<File> {
        return DataLoader(mContext)
    }

    override fun onLoadFinished(loader: Loader<File>, data: File?) {
        mResultCallback.onResult(data)
    }

    override fun onLoaderReset(loader: Loader<File>) {
        OLog.d("LoaderCallback, onLoaderReset")
    }
}

interface ResultCallback<T>{
    fun onResult(data: T?)
}