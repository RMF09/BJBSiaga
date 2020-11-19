package com.rmf.bjbsiaga

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.admin.AdminDashboardActivity
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.dialog_reset_pw_berhasil.*

class ResetPasswordActivity : AppCompatActivity() {

    var pwLama :String? = ""
    var documentId :String? = ""

    lateinit var db : FirebaseFirestore
    lateinit var securityRef: CollectionReference
    private  val TAG = "ResetPasswordActivity"
    lateinit var alertDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        initDB()
        initDialog()

        pwLama = intent.getStringExtra("password")
        documentId  =intent.getStringExtra("document_id")

        btn_reset_password.setOnClickListener {
            if(validate()){

                    documentId?.let { it1 ->
                        securityRef.document(it1)
                            .update("password",edit_password_baru.text.toString()).addOnSuccessListener {
                                Log.d(TAG, "onCreate: Berhasil ")
                                alertDialog.show()
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "onCreate: ${it.toString()}")
                            }
                    }
            }
        }

    }
    fun initDB(){
        db = FirebaseFirestore.getInstance()
        securityRef = db.collection(CollectionsFS.SECURITY)
    }

    fun validate(): Boolean{

        closeKeyboard()

        text_password_lama_warning.visibility=View.GONE
        text_password_baru_warning.visibility=View.GONE
        text_konfirmasi_password_baru_warning.visibility=View.GONE

        val passwordLama: String = edit_password_lama.text.toString()
        val passwordBaru: String = edit_password_baru.text.toString()
        val konfirmasiPassword: String = edit_konfirmasi_password_baru.text.toString()

        if (passwordLama.isEmpty()){
            edit_password_lama.requestFocus()
            text_password_lama_warning.visibility = View.VISIBLE
            text_password_lama_warning.text= "Masukan Password Lama"
            return false
        }
        if (passwordLama!=pwLama){
            edit_password_lama.requestFocus()
            text_password_lama_warning.visibility = View.VISIBLE
            text_password_lama_warning.text= "Password Lama Salah"
            return false
        }
        if (passwordBaru.isEmpty()){
            edit_password_baru.requestFocus()
            text_password_baru_warning.visibility = View.VISIBLE
            text_password_baru_warning.text= "Masukan Password Baru"
            return false
        }
        if (edit_password_baru.length() <8){
            edit_password_baru.requestFocus()
            text_password_baru_warning.visibility = View.VISIBLE
            text_password_baru_warning.text="Password baru harus memiliki 8 karakter atau lebih"
            return false
        }
        if (konfirmasiPassword.isEmpty()){
            edit_konfirmasi_password_baru.requestFocus()
            text_konfirmasi_password_baru_warning.visibility = View.VISIBLE
            text_konfirmasi_password_baru_warning.text="Masukan Konfirmasi Password"
            return false
        }
        if (passwordBaru!=konfirmasiPassword){
            edit_konfirmasi_password_baru.requestFocus()
            text_konfirmasi_password_baru_warning.visibility = View.VISIBLE
            text_konfirmasi_password_baru_warning.text="Konfirmasi Password Baru tidak sesuai dengan Password Baru"
            return false
        }
        return true
    }

    fun closeKeyboard(){
        val view = currentFocus
        if(view!=null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }

    fun initDialog(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_reset_pw_berhasil,null)

        val btn : Button = view.findViewById(R.id.btn_ok_dialog_reset)

        builder.setView(view)
        alertDialog = builder.create()
        alertDialog.setCancelable(false)

        btn.setOnClickListener {
            finish()
        }
    }

}