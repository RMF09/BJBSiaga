package com.rmf.bjbsiaga.admin.data.supervisor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataUser
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_edit_supervisor.*

class EditSupervisorActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userRef: CollectionReference
    private lateinit var alertDialog: AlertDialog

    private lateinit var id: String
    private lateinit var dataUser: DataUser

    companion object{
        const val TAG ="EditData"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_supervisor)
        initDB()

        id = intent.getStringExtra("id")!!
        dataUser = intent.getParcelableExtra("data")!!

        dataUser.apply {
            edit_nama.editText?.setText(nama)
            edit_nik.editText?.setText(nik.toString())
            edit_password.editText?.setText(password)
        }

        Log.d(TAG, "onCreate: id: $id")


        btn_edit_data.setOnClickListener {
            if(validate()) saveData()
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

        dataUser = DataUser(nama,nik,password, CollectionsFS.SUPERVISOR)
        userRef.document(id).set(dataUser)
            .addOnSuccessListener {
                this.showDialog("Data User Supervisor berhasil","Berhasil")
            }
            .addOnFailureListener {
                this.showDialog("Data User Supervisor gagal","Kesalahan")
                Log.e(TambahDataSupervisorActivity.TAG, "saveData: $it." )
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
            btn_edit_data.isEnabled=true
            return false
        }
        if(edit_nik.editText?.text.toString().isEmpty()){
            edit_nik.requestFocus()
            edit_nik.error="NIK diperlukan"
            btn_edit_data.isEnabled=true
            return false
        }
        if(password.isEmpty()){
            edit_password.requestFocus()
            edit_password.error = "Password diperlukan"
            btn_edit_data.isEnabled=true
            return false
        }
        if(edit_password.editText?.length()!! <6){
            edit_password.requestFocus()
            edit_password.error ="Password harus lebih dari 6 karakter"
            btn_edit_data.isEnabled=true
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
                if(title == "Berhasil"){
                    Intent().apply {
                        putExtra("data",dataUser)
                        setResult(DetailSupervisor.REQUEST_CODE,this)
                        finish()
                    }
                }

            }
        }
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }
}