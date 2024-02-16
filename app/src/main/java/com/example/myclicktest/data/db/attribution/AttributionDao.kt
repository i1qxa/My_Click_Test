package com.example.myclicktest.data.db.attribution

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttributionDao {

    @Query("SELECT * FROM attributiondb LIMIT 1")
    fun getAttributionDBFlow():Flow<AttributionDB>

    @Query("SELECT * FROM attributiondb LIMIT 1")
    suspend fun getAttributionDB():AttributionDB

    @Query("SELECT * FROM attributiondb LIMIT 1")
    suspend fun isAttributionNotEmpty(): AttributionDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAttribution(attribution: AttributionDB)

}