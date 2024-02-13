package com.example.myclicktest.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttributionDao {

    @Query("SELECT * FROM attributiondb LIMIT 1")
    fun getAttributionDB():Flow<AttributionDB>

    @Query("SELECT * FROM attributiondb LIMIT 1")
    suspend fun isAttributionNotEmpty():AttributionDB?

    @Insert
    suspend fun saveAttribution(attribution:AttributionDB)

}