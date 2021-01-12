package com.rmf.bjbsiaga.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
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
            if(validate(it)) saveData()
        }
    }

    fun initDB(){
        db = FirebaseFirestore.getInstance()
    }

    fun saveData(){

        val nama = edit_nama.text.toString()
        val email = edit_email.text.toString()
        val nik : Long = edit_nik.text.toString().toLong()
        val unitKerja = edit_unit_kerja.text.toString()
        val noWA : Long = edit_no_wa.text.toString().toLong()
        val password = edit_password.text.toString()

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
//    fun registerNewUser(view:View, email: String, password: String){
//        mAuth.createUserWithEmailAndPassword(email,password)
//            .addOnCompleteListener {
//                if(it.isSuccessful){
//                    Log.d(TAG, "registerNewUser: success")
//                    val user = mAuth.currentUser
//                    saveData(view,user!!.uid)
//
//                }else{
//                    if(it.exception.toString() == ConstantValue.EMAIL_SUDAH_DIGUNAKAN){
//                        Snackbar.make(view,"Alamat Email sudah digunakan oleh akun lain",Snackbar.LENGTH_LONG).show()
//                    }
//                    Log.w(TAG, "registerNewUser: failure ${it.exception.toString()}" )
//                }
//            }
//    }
    fun validate(view: View) : Boolean{
        val nama = edit_nama.text.toString()
        val email = edit_email.text.toString()
        val unitKerja = edit_unit_kerja.text.toString()
        val password =  edit_password.text.toString()

        if(nama.isEmpty()){
            edit_nama.requestFocus()
            Snackbar.make(view,"Nama diperlukan",Snackbar.LENGTH_LONG).show()
            return false
        }
        if(edit_nik.text.toString().isEmpty()){
            edit_nik.requestFocus()
            Snackbar.make(view,"NIK diperlukan",Snackbar.LENGTH_LONG).show()
            return false
        }
        if(unitKerja.isEmpty()){
            edit_unit_kerja.requestFocus()
            Snackbar.make(view,"Unit Kerja diperlukan",Snackbar.LENGTH_LONG).show()
            return false
        }
        if(email.isEmpty()){
            edit_email.requestFocus()
            Snackbar.make(view,"Email diperlukan",Snackbar.LENGTH_LONG).show()
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edit_email.requestFocus()
            Snackbar.make(view,"Harap masukan email dengan benar",Snackbar.LENGTH_LONG).show()
            return false
        }

        if(edit_no_wa.text.toString().isEmpty()){
            edit_no_wa.requestFocus()
            Snackbar.make(view,"Nomor WhatsApp diperlukan",Snackbar.LENGTH_LONG).show()
            return false
        }
        if(password.isEmpty()){
            edit_password.requestFocus()
            Snackbar.make(view,"Password diperlukan",Snackbar.LENGTH_LONG).show()
            return false
        }
        if(edit_password.length()<6){
            edit_password.requestFocus()
            Snackbar.make(view,"Password harus lebih dari 6 karakter",Snackbar.LENGTH_LONG).show()
            return false
        }
        return true


    }
}