package com.example.myclicktest.data.db.attribution

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.StringBuilder

@Entity
data class AttributionDB(
    @PrimaryKey
    val id:Int,
    val isNonOrganic:Boolean,
    val sub1:String?,
    val sub2:String?,
    val sub3:String?,
    val sub4:String?,
    val sub5:String?,
    val sub6:String?,
){
    fun getParamsString():String{
        val paramsSB = StringBuilder()
        paramsSB.apply {
            append("?")
            append("sub1=${sub1?:""}")
            append("&sub2=${sub2?:""}")
            append("&sub3=${sub3?:""}")
            append("&sub4=${sub4?:""}")
            append("&sub5=${sub5?:""}")
            append("&sub6=${sub6?:""}")

        }
        return paramsSB.toString()
    }
    companion object{
        fun setupAttribution(campaign:String?): AttributionDB {
            if(campaign?.isNotEmpty()?:false){
                val campaignAsList = campaign?.split("_")
                return AttributionDB(
                    1,
                    true,
                    campaignAsList?.get(0),
                    campaignAsList?.get(1),
                    campaignAsList?.get(2),
                    campaignAsList?.get(3),
                    campaignAsList?.get(4),
                    campaignAsList?.get(5),
                )
            }
            else{
                return AttributionDB(
                    1,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                )
            }
        }
    }
}
