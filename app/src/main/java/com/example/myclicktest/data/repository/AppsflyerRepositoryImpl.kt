package com.example.myclicktest.data.repository

import com.example.myclicktest.domain.APPS_CAMPAIGN
import com.example.myclicktest.domain.Attribution
import com.example.myclicktest.domain.UserType

class AppsflyerRepositoryImpl() {

    fun getAttributionFromApps(appsData:Map<String, Any>):Attribution{
        return if (appsData.containsKey(APPS_CAMPAIGN)) Attribution(UserType.NON_ORGANIC, appsData.get(
            APPS_CAMPAIGN).toString())
        else Attribution(UserType.ORGANIC, null)
    }

}