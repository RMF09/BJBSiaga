package com.rmf.bjbsiaga.admin.data.security

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.Config

import kotlinx.android.synthetic.main.activity_tambah_data_security.*
import kotlinx.android.synthetic.main.activity_tambah_data_security.edit_password

class TambahDataSecurityActivity : AppCompatActivity() {
    lateinit var db : FirebaseFirestore

    private val TAG = "TambahDataSecurityActiv"
    lateinit var mAuth : FirebaseAuth
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_data_security)

        initFirebaseAuth()
        initDB()

        back.setOnClickListener {
            finish()
        }
        btn_tambah_data.setOnClickListener {
            btn_tambah_data.isEnabled=false
            if(validate()) saveData()
        }
    }

    fun initDB(){
        db = FirebaseFirestore.getInstance()
    }

    fun saveData(){

        val nama = edit_nama.editText?.text.toString()
        val email = edit_email.editText?.text.toString()
        val nik : Long = edit_nik.editText?.text.toString().toLong()
        val unitKerja = edit_unit_kerja.editText?.text.toString()
        val noWA : Long = edit_no_wa.editText?.text.toString().toLong()
        val password = edit_password.editText?.text.toString()

        val dataSecurity =
            DataSecurity(nama, email, nik, noWA, unitKerja,password,Config.USER_LOGIN_USER)

        db.collection(CollectionsFS.SECURITY).document().set(dataSecurity)
            .addOnSuccessListener {
                this.showDialog("Data User berhasil ditambahkan","Berhasil")
            }
            .addOnFailureListener {
                this.showDialog("Data User gagal ditambahkan","Kesalahan")
                Log.e(TAG, "saveData: $it." )
            }
    }

    private fun showDialog(message: String, title: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(message)
            setTitle(title)
            setPositiveButton("OK"
            ) { dialog, _ ->
                dialog.dismiss()
                if(title == "Berhasil")
                    finish()
            }
        }
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun initFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance()
    }
    fun validate() : Boolean{

        edit_nama.error = ""
        edit_nik.error = ""
        edit_email.error = ""
        edit_unit_kerja.error = ""
        edit_no_wa.error = ""
        edit_password.error =""
        val nama = edit_nama.editText?.text.toString()
        val email = edit_email.editText?.text.toString()
        val unitKerja = edit_unit_kerja.editText?.text.toString()
        val password =  edit_password.editText?.text.toString()

        if(nama.isEmpty()){
            edit_nama.requestFocus()
            edit_nama.error ="Nama diperlukan"
            btn_tambah_data.isEnabled=true
            return false
        }
        if(edit_nik.editText?.text.toString().isEmpty()){
            edit_nik.requestFocus()
            edit_nik.error="NIK diperlukan"
            btn_tambah_data.isEnabled=true
            return false
        }
        if(unitKerja.isEmpty()){
            edit_unit_kerja.requestFocus()
            edit_unit_kerja.error="Unit Kerja diperlukan"
            return false
        }
        if(email.isEmpty()){
            edit_email.requestFocus()
            edit_email.error ="Email diperlukan"
            btn_tambah_data.isEnabled=true
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edit_email.requestFocus()
            edit_email.error ="Harap masukan email dengan benar"
            btn_tambah_data.isEnabled=true
            return false
        }

        if(edit_no_wa.editText?.text.toString().isEmpty()){
            edit_no_wa.requestFocus()
            edit_no_wa.error = "Nomor WhatsApp diperlukan"
            btn_tambah_data.isEnabled=true
            return false
        }
        if(password.isEmpty()){
            edit_password.requestFocus()
            edit_password.error = "Password diperlukan"
            btn_tambah_data.isEnabled=true
            return false
        }
        if(edit_password.editText?.length()!! <6){
            edit_password.requestFocus()
            edit_password.error ="Password harus lebih dari 6 karakter"
            btn_tambah_data.isEnabled=true
            return false
        }
        return true


    }
}