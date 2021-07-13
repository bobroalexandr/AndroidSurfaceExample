package com.example.surfaces

import android.view.Surface

class Surfaces {

    companion object {
        init {
            System.loadLibrary("hello-jni")
        }

        @JvmStatic
        external fun patch(surface: Surface)
    }
}
