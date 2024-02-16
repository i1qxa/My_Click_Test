package com.example.myclicktest.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myclicktest.data.repository.LinkRepositoryImpl
import com.example.myclicktest.databinding.ActivityMainBinding
import com.example.myclicktest.ui.web.MyWebChromeClient
import com.example.myclicktest.ui.web.MyWebViewClient
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private val webView by lazy { WebView(this) }
    private var isWebViewFirstLaunch = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeWebLink()
        observeFileUploadData()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        webView.saveState(outState)
    }

    private fun observeFileUploadData(){
        viewModel.fileUploadDataLD.observe(this){ fileUploadData ->
            fileUploadCallback = fileUploadData.fileUploadCallBack
            fileUploadActivityResultLauncher.launch(fileUploadData.intent)
        }
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



    private fun observeWebLink() {
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

    private fun launchWebView(link:String) {
        setupWebView(link)
        binding.progressLoading.visibility = View.GONE
        binding.constraintMain.addView(webView)
        webView.layoutParams.apply {
            height = ConstraintLayout.LayoutParams.MATCH_PARENT
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
        }
    }

    private fun setupWebView(link:String){
        webView.apply {
            loadUrl(link)
            settings.apply {
                domStorageEnabled = true
                javaScriptEnabled = true
                allowFileAccess = true
                javaScriptCanOpenWindowsAutomatically = true
            }
            webChromeClient = MyWebChromeClient(this@MainActivity)
            webViewClient = MyWebViewClient(LinkRepositoryImpl(application),this@MainActivity)
            onBackPressedDispatcher.addCallback(onBackBehavior)
            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
                startActivity(intent)
            }
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