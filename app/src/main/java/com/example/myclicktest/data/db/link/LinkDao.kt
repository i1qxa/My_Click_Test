package com.example.myclicktest.data.db.link

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {

    @Query("SELECT * FROM LinkDB LIMIT 1")
    fun getLinkFlow():Flow<LinkDB>

    @Query("SELECT * FROM LinkDB LIMIT 1")
    suspend fun getLink():LinkDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLink(link:LinkDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setEmptyLink(link:LinkDB)

}