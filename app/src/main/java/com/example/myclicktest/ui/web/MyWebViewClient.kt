package com.example.myclicktest.ui.web

import android.content.Context
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import com.example.myclicktest.data.repository.LinkRepositoryImpl
import java.net.HttpURLConnection
import java.net.URL

class MyWebViewClient(private val linkRepo: LinkRepositoryImpl,private val context: Context) : WebViewClient() {

    private val listOfCustomLinkData by lazy {
        listOf(
            CustomLinkData("tg://", Intent.ACTION_VIEW, "org.telegram.messenger"),
            CustomLinkData("tg:resolve", "", null),
            CustomLinkData("viber://", Intent.ACTION_VIEW, "com.viber.voip"),
            CustomLinkData("whatsapp://", Intent.ACTION_VIEW, "com.whatsapp"),
            CustomLinkData("mailto", Intent.ACTION_SENDTO, null),
            CustomLinkData("tel://", Intent.ACTION_DIAL, null),
            )
    }
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (url != null) {
            linkRepo.updateLastLink(url)
        }
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        val url = request?.url.toString()
        if (url?.endsWith(".jpg") == true || url?.endsWith(".png") == true || url?.endsWith(".gif") == true) {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connect()
            val inputStream = connection.inputStream
            return WebResourceResponse("image/*", "UTF-8", inputStream)
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return if(checkUrl(request?.url.toString())){
            true
        }else
            super.shouldOverrideUrlLoading(view, request)
    }


    private fun checkUrl(url:String):Boolean{

        var intent:Intent? = null
        listOfCustomLinkData.map {
            if (url.contains(it.keyWord)) {
                if (it.intentKey.isEmpty()) return true
                intent = it.getIntent(url)
            }
        }
        return if (intent!=null){
            startActivity(context, intent!!, null)
            true
        }else{
            false
        }
    }
}