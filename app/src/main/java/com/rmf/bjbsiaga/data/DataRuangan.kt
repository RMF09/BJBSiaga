package com.rmf.bjbsiaga.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@IgnoreExtraProperties
data class DataRuangan(
    var namaRuangan: String="",
    var lat: Double= 0.0,
    var lng: Double= 0.0,
    var idCabang: String=""
) : Parcelable{
    @get:Exclude
    var documentId: String=""
}