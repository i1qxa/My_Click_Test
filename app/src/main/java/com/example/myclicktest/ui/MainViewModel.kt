package com.example.myclicktest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myclicktest.data.db.ClickDataBase

class MainViewModel(application: Application):AndroidViewModel(application) {

    private val linkDao = ClickDataBase.getInstance(application).linkDao()

    val linkFlow = linkDao.getLinkFlow()

}