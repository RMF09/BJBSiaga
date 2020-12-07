package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataJadwalBertugas (

    val idJadwal: String= "",
    val nikPetugas: Long= 0,
    val shift: String=""
){
    @get:Exclude
    var documentId: String=""
}
