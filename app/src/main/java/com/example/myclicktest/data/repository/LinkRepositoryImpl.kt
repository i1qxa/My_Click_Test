package com.example.myclicktest.data.repository

import android.app.Application
import com.example.myclicktest.data.db.ClickDataBase
import com.example.myclicktest.data.db.link.LinkDB
import com.example.myclicktest.domain.FIREBASE_NON_ORGANIC_LINK
import com.example.myclicktest.domain.FIREBASE_ORGANIC_LINK
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LinkRepositoryImpl(private val application: Application) {

    private val linkDao by lazy { ClickDataBase.getInstance(application).linkDao() }
    private val attributionDao by lazy { ClickDataBase.getInstance(application).attributionDao() }

    fun fetchBaseLink(isNonOrganic: Boolean) {
        val linkType = if (isNonOrganic) FIREBASE_NON_ORGANIC_LINK else FIREBASE_ORGANIC_LINK
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (configUpdate.updatedKeys.contains(linkType)) {
                    remoteConfig.activate()
                    val baseLink = remoteConfig.getString(linkType)
                    if (baseLink.isEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            linkDao.removeLink()
                        }
                    }else{
                        CoroutineScope(Dispatchers.IO).launch {
                            val baseLinkWithParams = baseLink+attributionDao.getAttributionDB().getParamsString()
                            linkDao.saveLink(LinkDB(1,baseLink,baseLinkWithParams,baseLink))
                        }
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {

            }
        })
    }


}