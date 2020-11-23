package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataRuangan(
    var namaRuangan: String="",
    var lat: Double= 0.0,
    var lng: Double= 0.0
){
    @get:Exclude
    var documentId: String=""
}