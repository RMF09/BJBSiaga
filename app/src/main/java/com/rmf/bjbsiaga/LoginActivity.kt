package com.rmf.bjbsiaga

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.admin.AdminDashboardActivity
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.security.SecurityDashboardActivity
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
                signIn(text_input_username.editText?.text.toString().toLong(),text_input_password.editText?.text.toString())
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
                            startActivity(Intent(this,SecurityDashboardActivity::class.java))
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

        text_input_username.error= ""
        text_input_password.error= ""

        when(info){
            InfoLogin.LOADING ->{
                linear_info.visibility =View.VISIBLE
                progress.visibility= View.VISIBLE
                keterangan.text = "Harap tunggu..."
                keterangan.setTextColor(Color.parseColor("#F4F4F4"))
            }
            InfoLogin.USERNAME_SALAH -> {
                hideInfoLogin()
                text_input_username.requestFocus()
                text_input_username.error="Username Salah"
            }
            InfoLogin.PASSWORD_SALAH -> {
                hideInfoLogin()
                text_input_password.requestFocus()
                text_input_password.error="Password Salah"
            }
        }
    }

    fun validateLogin() : Boolean{
        val username = text_input_username.editText?.text.toString()
        val password = text_input_password.editText?.text.toString()

        text_input_username.error= ""
        text_input_password.error= ""

        if(username.isEmpty()){
            text_input_username.requestFocus()
            text_input_username.error="Username tidak boleh kosong"
            return false
        }

        if(password.isEmpty()){
            text_input_password.requestFocus()
            text_input_password.error="Password tidak boleh kosong"
            return false
        }
        return true
    }

    fun clearForm(){
        text_input_username.editText?.text= null
        text_input_password.editText?.text= null

    }

}