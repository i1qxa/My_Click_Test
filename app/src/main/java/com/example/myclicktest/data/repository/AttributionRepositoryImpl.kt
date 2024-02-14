package com.example.myclicktest.data.repository

import android.app.Application
import com.example.myclicktest.data.db.attribution.AttributionDB
import com.example.myclicktest.data.db.ClickDataBase
import com.example.myclicktest.domain.WAITING_TIME_IN_MILS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AttributionRepositoryImpl(private val application: Application) {

    val dao = ClickDataBase.getInstance(application).attributionDao()
    private val appsFlyerRepositoryImpl by lazy { AppsFlyerRepositoryImpl(application) }
    private val metaRepositoryImpl by lazy { MetaRepositoryImpl(application) }

    fun waitDataFromApps(){
        appsFlyerRepositoryImpl.fetchDataFromApps()
        CoroutineScope(Dispatchers.IO).launch {
            delay(WAITING_TIME_IN_MILS)
            if (dao.isAttributionNotEmpty()==null) waitDataFromMeta()
        }
    }

    fun waitDataFromMeta(){
        metaRepositoryImpl.fetchDataFromMeta()
        CoroutineScope(Dispatchers.IO).launch {
            delay(WAITING_TIME_IN_MILS)
            if (dao.isAttributionNotEmpty()==null) dao.saveAttribution(
                AttributionDB(
                1,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
            )
            )
        }
    }
    fun fetchAttributionData() {
        CoroutineScope(Dispatchers.IO).launch {
            if (dao.isAttributionNotEmpty()==null) waitDataFromApps()
        }
    }

}