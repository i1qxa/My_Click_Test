package com.example.myclicktest.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
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

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private val webView by lazy { WebView(this) }
    private var isWebViewFirstLaunch = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeViewModel()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQ_CODE) {
            val mFilePathCallbackCurr = mFilePathCallback
            if (mFilePathCallbackCurr == null) {
                return
            } else {
                mFilePathCallbackCurr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                mFilePathCallbackCurr = null
            }
        }
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

    private fun launchWebView(link:String) {
        binding.progressLoading.visibility = View.GONE
        webView.apply {
            loadUrl(link)
            settings.apply {
                domStorageEnabled = true
                javaScriptEnabled = true
                allowFileAccess = true
                allowFileAccess = true
            }
            webViewClient = MyWebViewClient(LinkRepositoryImpl(application))
            webChromeClient
            onBackPressedDispatcher.addCallback(onBackBehavior)
        }
        binding.constraintMain.addView(webView)
        webView.layoutParams.apply {
            height = ConstraintLayout.LayoutParams.MATCH_PARENT
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
        }
    }

    val MywebChromeClient = object : WebChromeClient() {

        //        override fun onShowFileChooser(
//            webView: WebView?,
//            filePathCallback: ValueCallback<Array<Uri>>?,
//            fileChooserParams: FileChooserParams?
//        ): Boolean {
//            if(fileChooserParams!=null){
//                filePathCallback
//                val intent = fileChooserParams.createIntent()
//                startActivityForResult(intent, REQ_CODE)
//            }
//            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
//        }
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
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