package com.example.myclicktest.data.repository

import android.app.Application
import com.example.myclicktest.data.db.attribution.AttributionDB
import com.example.myclicktest.data.db.ClickDataBase
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MetaRepositoryImpl(private val application: Application) {

    private val dao by lazy { ClickDataBase.getInstance(application).attributionDao() }
    fun fetchDataFromMeta() {
        AppLinkData.fetchDeferredAppLinkData(
            application,
            AppLinkData.CompletionHandler { appLinkData ->
                if (appLinkData != null) {
                    val regex =
                        Regex("""([a-zA-Z0-9]+_[a-zA-Z0-9]+_[a-zA-Z0-9]+_[a-zA-Z0-9]+_[a-zA-Z0-9]+_[a-zA-Z0-9]+)""")
                    val campaign =
                        regex.find(
                            appLinkData.appLinkData.toString()
                        )?.value
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.saveAttribution(
                            AttributionDB.setupAttribution(campaign)
                        )
                    }
                }
            })
    }

}