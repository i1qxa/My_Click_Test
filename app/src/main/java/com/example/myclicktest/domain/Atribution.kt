package com.example.myclicktest.domain

const val APPS_CAMPAIGN = "campaign"
data class Attribution(
    val userType:UserType,
    val campaign:String?,
){
    val campaignAsList:List<String>?
        get() = campaign?.split("_")
    val sub1:String?
        get() = campaignAsList?.get(0)
    val sub2:String?
        get() = campaignAsList?.get(1)
    val sub3:String?
        get() = campaignAsList?.get(2)
    val sub4:String?
        get() = campaignAsList?.get(3)
    val sub5:String?
        get() = campaignAsList?.get(4)
    val sub6:String?
        get() = campaignAsList?.get(5)

}
