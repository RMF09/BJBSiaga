package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataSiklus (
    val nama: String= "",
    val pukul: String= "",
    val siklusKe: Int= 0,
    val tanggal: String= "",
    val sudahBeres: Boolean=false,
    val idTugasSiaga: String=""
){
    @get:Exclude
    var documentId: String=""
}
