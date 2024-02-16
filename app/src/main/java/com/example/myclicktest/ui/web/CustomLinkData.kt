package com.example.myclicktest.ui.web

import android.content.Intent
import android.net.Uri

data class CustomLinkData(
    val keyWord:String,
    val intentKey:String,
    val packName:String?,
){
    fun getIntent(url:String):Intent{
        val uri = Uri.parse(url)
        val intent = Intent(intentKey,uri)
        if (packName!=null) intent.setPackage(packName)
        return intent
    }
}
