package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class DataTugasSiaga (
    val idJadwalBertugas: String= "",
    val statusSudahBeres: Boolean=false,
    val tanggal: Date? = null
){
    @get:Exclude
    var documentId: String=""
}