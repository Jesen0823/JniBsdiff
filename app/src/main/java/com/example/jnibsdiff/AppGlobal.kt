package com.example.jnibsdiff

import android.app.Application
import com.example.jnibsdiff.AppGlobal
import java.lang.reflect.InvocationTargetException

object AppGlobal {
    private var mApplication: Application? = null
    val application: Application?
        get() {
            if (mApplication == null) {
                try {
                    val method = Class.forName("android.app.ActivityThread")
                        .getDeclaredMethod("currentApplication")
                    mApplication = method.invoke(null, *arrayOf()) as Application
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
            return mApplication
        }
}