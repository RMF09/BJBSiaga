package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataSecurity (
    val nama: String="",
    val email: String="",
    val nik: Long =0,
    val noWA: Long =0,
    val unitKerja: String="",
    val password: String=""
){
    @get:Exclude
    var documentId: String=""
}
