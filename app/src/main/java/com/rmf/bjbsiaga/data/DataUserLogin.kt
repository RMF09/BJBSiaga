package com.rmf.bjbsiaga.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "datauserlogin")
data class DataUserLogin(
    @PrimaryKey val nik: Long,
    @ColumnInfo(name = "nama") var nama: String="",
    @ColumnInfo(name = "unit_kerja") var unit_kerja: String?="",
    @ColumnInfo(name = "no_wa") var no_wa: Long=0
)