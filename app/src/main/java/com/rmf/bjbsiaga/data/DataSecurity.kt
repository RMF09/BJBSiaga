package com.rmf.bjbsiaga.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class DataSecurity (
    var nama: String="",
    val email: String="",
    val nik: Long =0,
    val noWA: Long =0,
    val unitKerja: String="",
    val password: String="",
    val role: String=""
):Parcelable{
    @get:Exclude
    var documentId: String=""
    @get:Exclude
    var terpilih: Boolean=false
}
