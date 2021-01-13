package com.rmf.bjbsiaga.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.Config
import kotlinx.android.synthetic.main.activity_edit_user.*
import kotlinx.android.synthetic.main.activity_edit_user.edit_email
import kotlinx.android.synthetic.main.activity_edit_user.edit_nama
import kotlinx.android.synthetic.main.activity_edit_user.edit_nik
import kotlinx.android.synthetic.main.activity_edit_user.edit_no_wa
import kotlinx.android.synthetic.main.activity_edit_user.edit_password
import kotlinx.android.synthetic.main.activity_edit_user.edit_unit_kerja

class EditUser : AppCompatActivity() {
    lateinit var db : FirebaseFirestore

    private val TAG = "EditDataSecurityActiv"
    private lateinit var alertDialog: AlertDialog
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        documentId = intent.getStringExtra("id").toString()
        intent.getParcelableExtra<DataSecurity>("data").apply {
            edit_nama.setText(this?.nama)
            edit_nik.setText(this?.nik.toString())
            edit_email.setText(this?.email)
            edit_no_wa.setText(this?.noWA.toString())
            edit_unit_kerja.setText(this?.unitKerja)
            edit_password.setText(this?.password)
        }
        Log.d(TAG, "onCreate: documentID = $documentId")
        initDB()

        btn_edit_data.setOnClickListener {
            if(validate(it))editData()
        }
        back.setOnClickListener { finish() }
    }

    fun initDB(){
        db = FirebaseFirestore.getInstance()
    }

    fun editData(){

        val nama = edit_nama.text.toString()
        val email = edit_email.text.toString()
        val nik : Long = edit_nik.text.toString().toLong()
        val unitKerja = edit_unit_kerja.text.toString()
        val noWA : Long = edit_no_wa.text.toString().toLong()
        val password = edit_password.text.toString()

        val dataSecurity =
            DataSecurity(nama, email, nik, noWA, unitKerja,password, Config.USER_LOGIN_USER)

        db.collection(CollectionsFS.SECURITY).document(documentId).set(dataSecurity)
            .addOnSuccessListener {
                this.showDialog("Edit Data User berhasil ditambahkan","Berhasil")
            }
            .addOnFailureListener {
                this.showDialog("Edit Data User gagal ditambahkan","Kesalahan")
                Log.e(TAG, "saveData: $it." )
            }
    }

    fun validate(view: View) : Boolean{
        val nama = edit_nama.text.toString()
        val email = edit_email.text.toString()
        val unitKerja = edit_unit_kerja.text.toString()
        val password =  edit_password.text.toString()

        if(nama.isEmpty()){
            edit_nama.requestFocus()
            Snackbar.make(view,"Nama diperlukan", Snackbar.LENGTH_LONG).show()
            return false
        }
        if(edit_nik.text.toString().isEmpty()){
            edit_nik.requestFocus()
            Snackbar.make(view,"NIK diperlukan", Snackbar.LENGTH_LONG).show()
            return false
        }
        if(unitKerja.isEmpty()){
            edit_unit_kerja.requestFocus()
            Snackbar.make(view,"Unit Kerja diperlukan", Snackbar.LENGTH_LONG).show()
            return false
        }
        if(email.isEmpty()){
            edit_email.requestFocus()
            Snackbar.make(view,"Email diperlukan", Snackbar.LENGTH_LONG).show()
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edit_email.requestFocus()
            Snackbar.make(view,"Harap masukan email dengan benar", Snackbar.LENGTH_LONG).show()
            return false
        }

        if(edit_no_wa.text.toString().isEmpty()){
            edit_no_wa.requestFocus()
            Snackbar.make(view,"Nomor WhatsApp diperlukan", Snackbar.LENGTH_LONG).show()
            return false
        }
        if(password.isEmpty()){
            edit_password.requestFocus()
            Snackbar.make(view,"Password diperlukan", Snackbar.LENGTH_LONG).show()
            return false
        }
        if(edit_password.length()<6){
            edit_password.requestFocus()
            Snackbar.make(view,"Password harus lebih dari 6 karakter", Snackbar.LENGTH_LONG).show()
            return false
        }
        return true


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

}