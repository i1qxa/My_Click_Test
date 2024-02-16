package com.example.myclicktest.ui.web

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.example.myclicktest.ui.MainActivity
import com.example.myclicktest.ui.MainViewModel

private const val FULL_SCREEN_SETTING = View.SYSTEM_UI_FLAG_FULLSCREEN or
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_IMMERSIVE
class MyWebChromeClient(private val activity: MainActivity) : WebChromeClient() {
    private var mCustomView: View? = null
    private var mCustomViewCallback: CustomViewCallback? = null
    private var mOriginalOrientation = 0
    private var mOriginalSystemUiVisibility = 0
    private val mainViewModel by lazy { ViewModelProvider(activity)[MainViewModel::class.java] }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        val intent = fileChooserParams?.createIntent()
        val fileUploadData = FileUploadData(filePathCallback ?: return false, intent)
        mainViewModel.setupFileUploadData(fileUploadData)
        return true
    }

    override fun onHideCustomView() {
        (activity.window.decorView as FrameLayout).removeView(mCustomView)
        mCustomView = null
        activity.window.decorView.setSystemUiVisibility(mOriginalSystemUiVisibility)
        activity.requestedOrientation = mOriginalOrientation
        mCustomViewCallback!!.onCustomViewHidden()
        mCustomViewCallback = null
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
    }

    override fun onShowCustomView(
        paramView: View,
        paramCustomViewCallback: CustomViewCallback
    ) {
        if (mCustomView != null) {
            onHideCustomView()
            return
        }
        mCustomView = paramView
        mOriginalSystemUiVisibility = activity.window.decorView.systemUiVisibility
        mOriginalOrientation = activity.getRequestedOrientation()
        mCustomViewCallback = paramCustomViewCallback
        (activity.getWindow()
            .getDecorView() as FrameLayout)
            .addView(
                mCustomView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        activity.window.decorView.systemUiVisibility = FULL_SCREEN_SETTING
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        mCustomView!!.setOnSystemUiVisibilityChangeListener { visibility: Int -> updateControls() }
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    }

    fun updateControls() {
        val params = mCustomView!!.layoutParams as FrameLayout.LayoutParams
        params.bottomMargin = 0
        params.topMargin = 0
        params.leftMargin = 0
        params.rightMargin = 0
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        mCustomView!!.layoutParams = params
        activity.getWindow().getDecorView()
            .setSystemUiVisibility(FULL_SCREEN_SETTING)
    }
}