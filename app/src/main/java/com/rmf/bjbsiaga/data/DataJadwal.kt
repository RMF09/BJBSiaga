package com.rmf.bjbsiaga.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataJadwal (
    val hari: String= "",
    var shift: String= "",
    var priority: Int= 0
){
    @get:Exclude
    var documentId: String=""
}
