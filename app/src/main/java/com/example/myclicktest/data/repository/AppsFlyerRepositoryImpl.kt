package com.example.myclicktest.data.repository

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.example.myclicktest.data.db.attribution.AttributionDB
import com.example.myclicktest.data.db.ClickDataBase
import com.example.myclicktest.domain.APPSFLYER_DEV_KEY
import com.example.myclicktest.domain.APPS_CAMPAIGN_PARAM_NAME
import com.example.myclicktest.domain.APPS_LOG_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppsFlyerRepositoryImpl(private val application: Application) {

    val dao = ClickDataBase.getInstance(application).attributionDao()

    fun fetchDataFromApps(){
        val conversionDataListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                Log.d(APPS_LOG_TAG, data.toString())
                if (data != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.saveAttribution(
                            AttributionDB.setupAttribution(
                                data[APPS_CAMPAIGN_PARAM_NAME]?.toString()
                            )
                        )
                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                Log.e(APPS_LOG_TAG, "error onAttributionFailure :  $error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
            }

            override fun onAttributionFailure(error: String?) {
                Log.e(APPS_LOG_TAG, "error onAttributionFailure :  $error")
            }
        }

        AppsFlyerLib.getInstance().init(APPSFLYER_DEV_KEY, conversionDataListener, application)
        AppsFlyerLib.getInstance().setDebugLog(true)
        AppsFlyerLib.getInstance().start(application, APPSFLYER_DEV_KEY, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(APPS_LOG_TAG, "Launch sent successfully")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(
                    APPS_LOG_TAG, "Launch failed to be sent:\n" +
                            "Error code: " + p0 + "\n"
                            + "Error description: " + p1
                )
            }
        }
        )
    }
}