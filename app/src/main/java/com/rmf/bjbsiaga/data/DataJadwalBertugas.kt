package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataJadwalBertugas (

    val idJadwal: String= "",
    val nikPetugas: Long= 0,
    val nama: String= "",
    val shift: String="",
    val hari: String="",
    val unitKerja: String=""
){
    @get:Exclude
    var documentId: String=""
}
