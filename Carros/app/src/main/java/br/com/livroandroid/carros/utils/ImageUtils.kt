package br.com.livroandroid.carros.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

object ImageUtils {
    // Faz o resize da imagem
    fun resize(file: File, reqWidth: Int, reqHeight: Int): Bitmap {
        // first decode with inJustDecodeBounds = true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        // Calculate inSampleSize
        options.inSampleSize = calculeteinSampleSize(options, reqWidth, reqHeight)
        // Decode Bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    // Calcula o fator escala
    fun calculeteinSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height> reqHeight || width > reqWidth){
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps a both
            // height and width larger than request height and width
            while
                    (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth){
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}