package com.example.myclicktest.ui.web

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class FileChooserDelegate(val context: Context) :
    CoroutineScope {
    private var uploadCallback: ValueCallback<Array<Uri>>? = null
    private val browseFilesDelegate = BrowseFilesDelegate(context)
    private val cameraCaptureDelegate = CameraCaptureDelegate(context)

    override val coroutineContext: CoroutineContext
        get() = dispatcherProvider.io + Job()

    fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>, params: WebChromeClient.FileChooserParams
    ): Boolean {
        uploadCallback = filePathCallback
        openChooser(params)
        return true;
    }

    private fun openChooser(params: WebChromeClient.FileChooserParams) {
        val cameraIntent = cameraCaptureDelegate.buildIntent(params)
        val chooserIntent = browseFilesDelegate.buildIntent(params)
        val extraIntents = listOfNotNull(cameraIntent).toTypedArray()

        val intent = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, chooserIntent)
            putExtra(Intent.EXTRA_TITLE, params.title())
            putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents)
        }
        (context as Activity).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

}