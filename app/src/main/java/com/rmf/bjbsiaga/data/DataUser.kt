package com.rmf.bjbsiaga.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class DataUser (
    var nama: String="",
    val nik: Long =0,
    val password: String="",
    val userType: String="",
    val email: String="",
    val noWA: Long =0,
    val unitKerja: String=""
): Parcelable {
    @get:Exclude
    var documentId: String=""
}