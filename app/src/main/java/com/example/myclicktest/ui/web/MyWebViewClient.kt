package com.example.myclicktest.ui.web

import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.myclicktest.data.repository.LinkRepositoryImpl

class MyWebViewClient(private val linkRepo: LinkRepositoryImpl) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (url != null) {
            linkRepo.updateLastLink(url)
        }
    }
}