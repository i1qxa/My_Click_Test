package com.example.myclicktest.ui.web

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback

data class FileUploadData(
    val fileUploadCallBack: ValueCallback<Array<Uri>>?,
    val intent:Intent?,
)