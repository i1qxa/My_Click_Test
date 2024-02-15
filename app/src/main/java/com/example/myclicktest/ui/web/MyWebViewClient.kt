package com.example.myclicktest.ui.web

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import com.example.myclicktest.data.repository.LinkRepositoryImpl
import java.net.HttpURLConnection
import java.net.URL

class MyWebViewClient(private val linkRepo: LinkRepositoryImpl,private val context: Context) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (url != null) {
//            linkRepo.updateLastLink(url)
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
        if(checkUrl(request?.url.toString())){
            return true
        }else return super.shouldOverrideUrlLoading(view, request)
    }

    private fun checkUrl(url:String):Boolean{
        val intent = if (url.contains("tg://")){
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("org.telegram.messenger")
        }else if (url.contains("viber://")){
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("com.viber.voip")
        }else if (url.contains("whatsapp://")){
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("com.whatsapp")
        }else if (url.contains("mailto")){
            Intent(Intent.ACTION_SENDTO, Uri.parse(url))
        }else if (url.contains("tel://")){
            Intent(Intent.ACTION_DIAL, Uri.parse(url))
        }else{
            null
        }

        if (intent!=null){
            startActivity(context, intent, null)
            return true
        }else{
            return false
        }
    }
}