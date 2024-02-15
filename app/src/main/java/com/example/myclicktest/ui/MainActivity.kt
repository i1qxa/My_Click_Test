package com.example.myclicktest.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myclicktest.data.repository.LinkRepositoryImpl
import com.example.myclicktest.databinding.ActivityMainBinding
import com.example.myclicktest.ui.web.MyWebViewClient
import kotlinx.coroutines.launch


private const val REQ_CODE = 666
class MainActivity : AppCompatActivity() {

    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private val webView by lazy { WebView(this) }
    private var isWebViewFirstLaunch = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeViewModel()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        webView.saveState(outState)
    }

    private val fileUploadActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.let { WebChromeClient.FileChooserParams.parseResult(result.resultCode, it) }
            fileUploadCallback?.onReceiveValue(results)
        } else {
            fileUploadCallback?.onReceiveValue(null)
        }
        fileUploadCallback = null
    }



    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.linkFlow.collect() { linkDB ->
                if (linkDB != null) {
                    if (linkDB.lastLink.isNullOrEmpty()){
                        launchWhiteActivity()
                    }else{
                        if (isWebViewFirstLaunch){
                            launchWebView(linkDB.lastLink)
                            isWebViewFirstLaunch = false
                        }
                    }
                }
            }
        }
    }

    val myWebChromeClient = object : WebChromeClient() {

        private var customView: View? = null
        private var customViewCallback: CustomViewCallback? = null
        private var originalOrientation = 0
        private val fullscreenContainer: FrameLayout? = null
        private val activity: Activity? = null

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            fileUploadCallback = filePathCallback ?: return false
            val intent = fileChooserParams?.createIntent()
            fileUploadActivityResultLauncher.launch(intent)
            return true
        }
        override fun onHideCustomView() {
            if (customView == null) {
                return
            }
            fullscreenContainer?.removeView(customView)
            fullscreenContainer?.setVisibility(View.GONE)
            customView = null
            customViewCallback?.onCustomViewHidden()
            this@MainActivity.setRequestedOrientation(originalOrientation)
            this@MainActivity.getWindow()?.getDecorView()?.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
        }

        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            if (customView != null) {
                onHideCustomView()
                return
            }
            customView = view
            originalOrientation = this@MainActivity.getRequestedOrientation()
            customViewCallback = callback
            fullscreenContainer?.addView(
                customView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
            fullscreenContainer?.setVisibility(View.VISIBLE)
            this@MainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            this@MainActivity.getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun launchWebView(link:String) {
        binding.progressLoading.visibility = View.GONE
        webView.apply {
            loadUrl(link)
            settings.apply {
                domStorageEnabled = true
                javaScriptEnabled = true
                allowFileAccess = true
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
            }
            webChromeClient = myWebChromeClient
            webViewClient = MyWebViewClient(LinkRepositoryImpl(application),this@MainActivity)
            onBackPressedDispatcher.addCallback(onBackBehavior)
        }
        binding.constraintMain.addView(webView)
        webView.layoutParams.apply {
            height = ConstraintLayout.LayoutParams.MATCH_PARENT
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
        }
    }



    private val onBackBehavior = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (webView.canGoBack()) webView.goBack()
        }
    }

    private fun launchWhiteActivity() {
        val intent = Intent(this, WhiteActivity::class.java)
        startActivity(intent)
    }

}