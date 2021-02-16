package com.rmf.bjbsiaga.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataUser
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_tambah_data_supervisor.btn_tambah_data
import kotlinx.android.synthetic.main.activity_tambah_data_supervisor.edit_nama
import kotlinx.android.synthetic.main.activity_tambah_data_supervisor.edit_nik
import kotlinx.android.synthetic.main.activity_tambah_data_supervisor.edit_password


class TambahDataSupervisorActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userRef: CollectionReference
    private lateinit var alertDialog: AlertDialog

    companion object{
        const val TAG ="TambahData  "
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_data_supervisor)
        initDB()

        btn_tambah_data.setOnClickListener {
            if (validate()) saveData()
        }
    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        userRef = db.collection(CollectionsFS.USER)
    }

    private fun saveData(){
        val nama = edit_nama.editText?.text.toString()
        val nik : Long = edit_nik.editText?.text.toString().toLong()
        val password = edit_password.editText?.text.toString()

        val dataUser = DataUser(nama,nik,password,CollectionsFS.SUPERVISOR)
        userRef.document().set(dataUser)
            .addOnSuccessListener {
                this.showDialog("Data User berhasil ditambahkan","Berhasil")
            }
            .addOnFailureListener {
                this.showDialog("Data User gagal ditambahkan","Kesalahan")
                Log.e(TAG, "saveData: $it." )
            }
    }
    private fun validate():Boolean{
        edit_nama.error = ""
        edit_nik.error = ""
        edit_password.error=""

        val nama = edit_nama.editText?.text.toString()
        val password = edit_password.editText?.text.toString()

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