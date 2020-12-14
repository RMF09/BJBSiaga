package com.rmf.bjbsiaga.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapUtils {
    companion object{
        fun getMarkerIcon(drawable : Drawable) : BitmapDescriptor {
            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888)
            canvas.setBitmap(bitmap)
            drawable.setBounds(0,0,drawable.intrinsicHeight,drawable.intrinsicHeight)
            drawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}