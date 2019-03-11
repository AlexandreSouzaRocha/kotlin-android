package br.com.livroandroid.carros.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class CameraHelper {

    companion object {
        private val TAG = "cameraHelper"
    }

    // Arquivo pode ser nulo
    var file: File? = null
        private set
    // Se girou a tela recupera o estado
    fun init(savedInstance: Bundle?) {
        if (savedInstance != null) {
            file = savedInstance.getSerializable("file") as File
        }
    }

    // Salva o estado
    fun OnSaveInstanceState(outState: Bundle) {
        if (file != null) {
            outState.putSerializable("file", file)
        }
    }

    // Intent para abrir a camera
    fun open(context: Context, fileName: String): Intent {
        file = getSdCardFile(context, fileName)
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = FileProvider.getUriForFile(context,
                context.applicationContext.packageName + ".provider", file!!)
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        return i
    }

    // Cria o arquivo da foto no diretório privado do app
    fun getSdCardFile(context: Context, fileName: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return File(dir, fileName)
    }

    // Lê o bitmap no tamanho desejado
    fun getBitmap(width: Int, height: Int): Bitmap? {
        file?.apply {
            if (exists()) {
                Log.d(TAG, absolutePath)
                // Resize
                val bitmap = ImageUtils.resize(this, width, height)
                Log.d(TAG, "getBitmap w/h: " + bitmap.width + "/" + bitmap.height)
                return bitmap
            }
        }
        return null
    }

    // Salva o bitmap em tamanho reduzido para upload
    fun save(bitmap: Bitmap) {
        file?.apply {
            val out = FileOutputStream(this)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            Log.d(TAG, "Foto salva: " + absolutePath)
        }
    }

}