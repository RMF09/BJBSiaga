package com.rmf.bjbsiaga

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.admin.NavAdmin
import com.rmf.bjbsiaga.data.DataUser
import com.rmf.bjbsiaga.security.SecurityDashboardActivity
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.Config
import com.rmf.bjbsiaga.util.InfoLogin
import com.rmf.bjbsiaga.util.SharedPref
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "LoginActivity"
    }

    lateinit var db : FirebaseFirestore
    private lateinit var userRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Check Login
        if(SharedPref.getInstance(this)!!.isLoggedIn()){
            when(SharedPref.getInstance(this)!!.loggedInRole()){
                Config.USER_LOGIN_ADMIN ->{
                    startActivity(Intent(this, NavAdmin::class.java))
                    finish()
                }
                Config.USER_LOGIN_SECURITY ->{
                    startActivity(Intent(this,SecurityDashboardActivity::class.java))
                    finish()
                }
                else -> {

                }
            }
        }

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

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        userRef = db.collection(CollectionsFS.USER)
    }

    private fun signIn(nik: Long, password: String){

        var passwordDB = ""
        var nikDB : Long = 0
        var nama=""
        var documentId=""
        var userType=""

        showInfoLogin(InfoLogin.LOADING)

        userRef.whereEqualTo("nik",nik).get()
            .addOnSuccessListener {

                btn_login.isEnabled=true
                if (!it.isEmpty){

                    for (document in it){
                        val dataUser : DataUser = document.toObject(DataUser::class.java)
                        dataUser.documentId = document.id
                        passwordDB = dataUser.password
                        nikDB = dataUser.nik
                        documentId =  dataUser.documentId
                        nama = dataUser.nama
                        userType = dataUser.userType
                    }
                    if(passwordDB != password){
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
                            SharedPref.getInstance(this)!!.apply {
                                storeID(documentId)
                                storeRole(userType)
                            }
                            val intent: Intent = when(userType){
                                Config.USER_LOGIN_SECURITY ->
                                    pergiKe(SecurityDashboardActivity::class.java)

                                Config.USER_LOGIN_ADMIN ->
                                    pergiKe(NavAdmin::class.java)

                                else -> { pergiKe(NavAdmin::class.java) }

                            }
                            intent.putExtra("nama",nama)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                else{

                    showInfoLogin(InfoLogin.USERNAME_SALAH)
                }
            }.addOnFailureListener {
                hideInfoLogin()
                btn_login.isEnabled=true
                Log.e(TAG, "signIn: $it")

            }
    }

    private fun pergiKe(cls: Class<*>): Intent{
        return Intent(this,cls)
    }

    private fun checkKesamaanUsernamePassword(usename :String, password: String): Boolean{
        if(usename == password){
            return true
        }
        return false
    }

    private fun hideInfoLogin(){
        linear_info.visibility= View.GONE
    }
    @SuppressLint("SetTextI18n")
    private fun showInfoLogin(info: Int){

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

    private fun validateLogin() : Boolean{
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

    private fun clearForm(){
        text_input_username.editText?.text= null
        text_input_password.editText?.text= null

    }

}