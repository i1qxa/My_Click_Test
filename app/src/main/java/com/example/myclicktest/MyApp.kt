package com.example.myclicktest

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener

const val APPSFLYER_DEV_KEY = "zyEKUTRENpDpajEPxLbSuF"
const val APPS_LOG_TAG ="APPSF"
class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        val conversionDataListener  = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                Log.d(APPS_LOG_TAG, data.toString())
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

        AppsFlyerLib.getInstance().init(APPSFLYER_DEV_KEY, conversionDataListener, this)
        AppsFlyerLib.getInstance().setDebugLog(true)
        AppsFlyerLib.getInstance().start(this, APPSFLYER_DEV_KEY, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(APPS_LOG_TAG, "Launch sent successfully")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(
                    APPS_LOG_TAG, "Launch failed to be sent:\n" +
                            "Error code: " + p0 + "\n"
                            + "Error description: " + p1)
            }
        }
        )

    }

}