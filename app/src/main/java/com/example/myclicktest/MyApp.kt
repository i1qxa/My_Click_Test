package com.example.myclicktest

import android.app.Application
import com.example.myclicktest.data.repository.AttributionRepositoryImpl


class MyApp : Application() {

    private val attributionRepositoryImpl by lazy { AttributionRepositoryImpl(this) }
    override fun onCreate() {
        super.onCreate()
        attributionRepositoryImpl.fetchAttributionData()
    }

}