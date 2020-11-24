package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataDetailSiklus (
    val nama: String= "",
    val pukul: String= "",
    val diCheck: Boolean= false,
    val idRuangan: String= "",
    val idSiklus: String= "",
    val tanggal: String= ""
){
    @get:Exclude
    var documentId: String=""
}
