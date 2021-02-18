package com.rmf.bjbsiaga.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.SharedPref

class ProfileFragment: Fragment() {

    private lateinit var textNama: TextView
    private lateinit var btnLogout: AppCompatButton

    private lateinit var alertDialog: AlertDialog
    private lateinit var mContext: Context

    private lateinit var db: FirebaseFirestore
    private lateinit var securityRef: CollectionReference

    companion object{
        const val TAG= "ProfileFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile,container,false)
        root.apply {
            btnLogout = findViewById(R.id.btn_logout)
            textNama = findViewById(R.id.text_nama)
            mContext = context
        }
        initDB()
        initDialog()
        loadData()

        btnLogout.setOnClickListener {
            alertDialog.show()
        }

        return root
    }

    private fun loadData() {
        if(SharedPref.getInstance(mContext)!!.isLoggedIn()){
            SharedPref.getInstance(mContext)!!.loggedInUser()?.let { it ->
                Log.d(TAG, "loadData: $it")
                securityRef.whereEqualTo("nik",it).get()
                    .addOnSuccessListener { document ->
                        if(!document.isEmpty){
                            for (data in document){
                                val dataSecurity = data.toObject(DataSecurity::class.java)
                                textNama.text = dataSecurity.nama
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "loadData: $it" )
                    }
            }
        }
    }

    private fun initDialog(){
        val builder = AlertDialog.Builder(mContext)
        builder.apply {
            setTitle("Logout")
            setMessage("Anda yakin ingin logout?")
            setPositiveButton("Ya"){ dialog, _ ->
                dialog.dismiss()
                SharedPref.getInstance(mContext)!!.logout()
                activity?.finish()
            }
            setNegativeButton("Batal"){dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialog = builder.create()
    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        securityRef = db.collection(CollectionsFS.SECURITY)
    }
}