package com.rmf.bjbsiaga.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataUserLoginDAO {
    @Query("SELECT * FROM datauserlogin WHERE nik IN (:userNIK)")
    fun getDataByNIK(userNIK: Long): List<DataUserLogin>

    @Insert
    fun insertAll(vararg dataUserLogin: DataUserLogin)

    @Delete
    fun delete(dataUserLogin: DataUserLogin)
    @Query("DELETE FROM datauserlogin")
    fun deleteAll()

}