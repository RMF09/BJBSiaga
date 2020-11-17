package com.rmf.bjbsiaga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.rmf.bjbsiaga.admin.AdminDashboardActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    lateinit var mAuth : FirebaseAuth
    private  val TAG = "LoginActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initFirebaseAuth()

        btn_login.setOnClickListener {
//            if (validateLogin(it))
//                signIn()
            startActivity(Intent(this,
                AdminDashboardActivity::class.java))
        }
    }
    fun signIn(){

        mAuth.signInWithEmailAndPassword("reksamfitrananda@gmail.com","12345678")
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d(TAG, "signIn: success")
                }
                else{
                    Log.d(TAG, "signIn: failure",it.exception)
                }
            }
    }

    fun initFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance()
    }
    fun registerNewUser(){
        mAuth.createUserWithEmailAndPassword("reksamfitrananda@gmail.com","12345678")
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d(TAG, "registerNewUser: success")
                }else{
                    Log.w(TAG, "registerNewUser: failure ${it.exception}" )
                }
            }
    }
    fun validateLogin(view: View) : Boolean{
        val email = edit_username.text.toString()
        val password = edit_password.text.toString()

        if(email.isEmpty()){
            edit_username.requestFocus()
            Snackbar.make(view,"Email diperlukan",Snackbar.LENGTH_LONG).show()
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edit_username.requestFocus()
            Snackbar.make(view,"Harap masukan email dengan benar",Snackbar.LENGTH_LONG).show()
            return false
        }
        if(password.isEmpty()){
            edit_password.requestFocus()
            Snackbar.make(view,"Password diperlukan",Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

}