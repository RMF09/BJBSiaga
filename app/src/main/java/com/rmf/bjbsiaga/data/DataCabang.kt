package com.rmf.bjbsiaga.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class DataCabang(
    var namaCabang: String="",
    val jumlahRuangan: Int=0
) : Parcelable{
    @get:Exclude
    var documentId: String=""
}