package com.lzf.quickcheck.screens.home

import android.content.res.AssetManager
import android.graphics.Bitmap

class YoloV5Ncnn {
    external fun Init(mgr: AssetManager?): Boolean

    inner class Obj {
        var x: Float = 0f
        var y: Float = 0f
        var w: Float = 0f
        var h: Float = 0f
        var label: String? = null
        var prob: Float = 0f
    }

    external fun Detect(bitmap: Bitmap?, use_gpu: Boolean): Array<Obj?>?

    companion object {
        init {
            System.loadLibrary("yolov5ncnn")
        }
    }
}
