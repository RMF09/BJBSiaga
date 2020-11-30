package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataDetailSiklus (
    val nama: String= "",
    var pukul: String= "",
    var diCheck: Boolean= false,
    val idRuangan: String= "",
    val idSiklus: String= "",
    val tanggal: String= "",
    val foto: String=""
){
    @get:Exclude
    var documentId: String=""
}
