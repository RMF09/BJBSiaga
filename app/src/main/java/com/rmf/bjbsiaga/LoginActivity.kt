package com.rmf.bjbsiaga

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.admin.AdminDashboardActivity
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.InfoLogin
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    lateinit var mAuth : FirebaseAuth
    private  val TAG = "LoginActivity"

    lateinit var db : FirebaseFirestore
    lateinit var securityRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        initFirebaseAuth()
        initDB()

        btn_login.setOnClickListener {
            hideInfoLogin()
            btn_login.isEnabled=false
            if (validateLogin()){
                signIn(edit_username.text.toString().toLong(),edit_password.text.toString())
            }else{
                btn_login.isEnabled=true
            }
        }
    }

    fun initDB(){
        db = FirebaseFirestore.getInstance()
        securityRef = db.collection(CollectionsFS.SECURITY)
    }

    fun signIn(nik: Long,password: String){

        var passwordDB = ""
        var nikDB : Long = 0
        var documentId=""

        showInfoLogin(InfoLogin.LOADING)

        securityRef.whereEqualTo("nik",nik).get()
            .addOnSuccessListener {

                btn_login.isEnabled=true
                if (!it.isEmpty){

                    for (document in it){
                        val dataSecurity : DataSecurity = document.toObject(DataSecurity::class.java)
                        dataSecurity.documentId = document.id
                        passwordDB = dataSecurity.password
                        nikDB = dataSecurity.nik
                        documentId =  dataSecurity.documentId


                    }
                    if(!passwordDB.equals(password)){

                        showInfoLogin(InfoLogin.PASSWORD_SALAH)
                    }
                    else{
                        hideInfoLogin()
                        clearForm()
                        if(checkKesamaanUsernamePassword(nikDB.toString(),password)){
                            val intent = Intent(this,ResetPasswordActivity::class.java)
                            intent.putExtra("document_id",documentId)
                            intent.putExtra("password",passwordDB )
                            startActivity(intent)

                        }else{
                            startActivity(Intent(this,AdminDashboardActivity::class.java))
                        }
                    }
                }
                else{

                    showInfoLogin(InfoLogin.USERNAME_SALAH)
                }
            }.addOnFailureListener {
                hideInfoLogin()
                btn_login.isEnabled=true
                Log.e(TAG, "signIn: ${it.toString()}")

            }
    }
    fun checkKesamaanUsernamePassword(usename :String, password: String): Boolean{
        if(usename.equals(password)){
            return true
        }
        return false
    }

    fun hideInfoLogin(){
        linear_info.visibility= View.GONE
    }
    fun showInfoLogin(info: Int){

        text_username_warning.visibility= View.GONE
        text_password_warning.visibility= View.GONE

        when(info){
            InfoLogin.LOADING ->{
                linear_info.visibility =View.VISIBLE
                progress.visibility= View.VISIBLE
                keterangan.text = "Harap tunggu..."
                keterangan.setTextColor(Color.parseColor("#F4F4F4"))
            }
            InfoLogin.USERNAME_SALAH -> {
                hideInfoLogin()
                text_username_warning.visibility= View.VISIBLE
                text_username_warning.text="Username Salah"
            }
            InfoLogin.PASSWORD_SALAH -> {
                hideInfoLogin()
                text_password_warning.visibility= View.VISIBLE
                text_password_warning.text="Password Salah"
            }
        }
    }

    fun validateLogin() : Boolean{
        val username = edit_username.text.toString()
        val password = edit_password.text.toString()

        text_username_warning.visibility= View.GONE
        text_password_warning.visibility= View.GONE

        if(username.isEmpty()){
            edit_username.requestFocus()

            text_username_warning.visibility= View.VISIBLE
            text_username_warning.text="Username tidak boleh kosong"
            return false
        }

        if(password.isEmpty()){
            edit_password.requestFocus()
            text_password_warning.visibility= View.VISIBLE
            text_password_warning.text="Password tidak boleh kosong"
            return false
        }
        return true
    }

    fun clearForm(){
        edit_username.setText("")
        edit_password.setText("")
    }

}