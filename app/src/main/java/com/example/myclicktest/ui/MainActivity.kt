package com.example.myclicktest.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myclicktest.R
import com.example.myclicktest.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private val webView by lazy { WebView(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.linkFlow.collect() { linkDB ->
                if (linkDB != null) {
                    if (linkDB.lastLink.isNullOrEmpty()){
                        launchWhiteActivity()
                    }else{
                        launchWebView(linkDB.lastLink)
                    }
                }
            }
        }
    }

    private fun launchWebView(link:String) {
        webView.layoutParams.apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        webView.loadUrl(link)
    }

    private fun launchWhiteActivity() {
        val intent = Intent(this, WhiteActivity::class.java)
        startActivity(intent)
    }

}