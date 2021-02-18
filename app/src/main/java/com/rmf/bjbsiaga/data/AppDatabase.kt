package com.rmf.bjbsiaga.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DataUserLogin::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dataUserLoginDao(): DataUserLoginDAO
}