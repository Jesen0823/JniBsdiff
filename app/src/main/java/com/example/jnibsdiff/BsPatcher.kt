package com.example.jnibsdiff

import com.example.jnibsdiff.utils.OLog

class BsPatcher {
    companion object {
        // Used to load the 'jnibsdiff' library on application startup.
//        init {
//            OLog.d("BsPatcher init")
//            System.loadLibrary("jnibsdiff")
//        }

        @JvmStatic
        external fun bsPatch(oldApk: String, patch :String, output: String);
    }


}