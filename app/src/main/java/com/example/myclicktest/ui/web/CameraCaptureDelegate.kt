package com.example.myclicktest.ui.web

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.WebChromeClient
import java.io.File

internal class CameraCaptureDelegate(val context: Context) {
    private var cameraImagePath: String? = null

    fun buildIntent(params: WebChromeClient.FileChooserParams): Intent? {
        if (!params.allowsCameraCapture()) return null

        val file = createEmptyImageFile() ?: return null
        val uri = CustomFileProvider.contentUriForFile(context, file)

        cameraImagePath = file.absolutePath

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
    }

    fun handleResult(onResult: (Array<Uri>?) -> Unit) {
        val results = buildCameraImageResult()

        onResult(results)
        cameraImagePath = null
    }

    private fun buildCameraImageResult(): Array<Uri>? {
        val file = cameraImagePath?.let { File(it) } ?: return null
        val uri = CustomFileProvider.contentUriForFile(context, file)

        return when (file.length()) {
            0L -> null
            else -> arrayOf(uri)
        }
    }

    private fun createEmptyImageFile(): File? {
        return try {
            val directory: File = CustomFileProvider.directory(context)
            return File.createTempFile("Capture_", ".jpg", directory)
        } catch (e: IOException) {
            null
        }
    }

    private fun FileChooserParams.allowsCameraCapture(): Boolean {
        val accept = defaultAcceptType()
        val acceptsAny = accept == "*/*"
        val acceptsImages = accept == "image/*" || accept == "image/jpg"

        return isCaptureEnabled && (acceptsAny || acceptsImages)
    }
}