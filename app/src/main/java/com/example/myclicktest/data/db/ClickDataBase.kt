package com.example.myclicktest.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myclicktest.data.db.attribution.AttributionDB
import com.example.myclicktest.data.db.attribution.AttributionDao
import com.example.myclicktest.data.db.link.LinkDB
import com.example.myclicktest.data.db.link.LinkDao
import kotlinx.coroutines.InternalCoroutinesApi

@Database(
    entities = [
        AttributionDB::class,
        LinkDB::class,
    ],
    exportSchema = false,
    version = 1
)
abstract class ClickDataBase: RoomDatabase() {

    abstract fun attributionDao(): AttributionDao
    abstract fun linkDao():LinkDao

    companion object {
        private var INSTANCE: ClickDataBase? = null
        private val LOCK = Any()
        private const val DB_NAME = "click_database_db"

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(application: Application): ClickDataBase {
            INSTANCE?.let {
                return it
            }
            kotlinx.coroutines.internal.synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    application,
                    ClickDataBase::class.java,
                    DB_NAME)
                    .build()
                INSTANCE = db
                return db
            }
        }
    }
}