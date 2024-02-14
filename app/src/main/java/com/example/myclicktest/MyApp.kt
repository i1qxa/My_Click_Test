package com.example.myclicktest

import android.app.Application
import com.example.myclicktest.data.db.ClickDataBase
import com.example.myclicktest.data.repository.AttributionRepositoryImpl
import com.example.myclicktest.data.repository.LinkRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyApp : Application() {

    private val attributionRepositoryImpl by lazy { AttributionRepositoryImpl(this) }
    private val attributionDao by lazy { ClickDataBase.getInstance(this).attributionDao() }
    private val linkDao by lazy { ClickDataBase.getInstance(this).linkDao() }
    private val linkRepositoryImpl by lazy { LinkRepositoryImpl(this) }
    override fun onCreate() {
        super.onCreate()
        attributionRepositoryImpl.fetchAttributionData()
        fetchBaseLink()
    }

    private fun fetchBaseLink() {
        CoroutineScope(Dispatchers.IO).launch {
            attributionDao.getAttributionDBFlow().collect() {
                if (it != null) {
                    linkRepositoryImpl.fetchBaseLink(it.isNonOrganic)
                }
            }
        }
    }

}