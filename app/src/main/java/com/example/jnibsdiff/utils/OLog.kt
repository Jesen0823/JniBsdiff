package com.example.jnibsdiff.utils

import android.util.Log

class OLog {
    companion object {

        @JvmStatic
        fun i(msg: String) {
            Log.i("JniBsdiff", msg)
        }

        @JvmStatic
        fun d(msg: String) {
            Log.d("JniBsdiff", msg)
        }

    }
}