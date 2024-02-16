package com.example.myclicktest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myclicktest.data.db.ClickDataBase
import com.example.myclicktest.ui.web.FileUploadData

class MainViewModel(application: Application):AndroidViewModel(application) {

    private val linkDao = ClickDataBase.getInstance(application).linkDao()

    val linkFlow = linkDao.getLinkFlow()

    private val _fileUploadDataLD = MutableLiveData<FileUploadData>()

    val fileUploadDataLD : LiveData<FileUploadData>
        get() = _fileUploadDataLD

    fun setupFileUploadData(data:FileUploadData){
        _fileUploadDataLD.value = data
    }

}