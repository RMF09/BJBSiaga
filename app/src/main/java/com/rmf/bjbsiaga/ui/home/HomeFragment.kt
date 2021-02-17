package com.rmf.bjbsiaga.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.util.SharedPref

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val cardInput: CardView = root.findViewById(R.id.cardInput)
        val cardJadwal: CardView = root.findViewById(R.id.cardJadwal)
        val cardRuangan: CardView = root.findViewById(R.id.cardRuangan)
        val cardKeluar: CardView = root.findViewById(R.id.cardKeluar)

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController


        cardInput.setOnClickListener {
            navController.navigate(R.id.action_nav_home_to_nav_data_user)
        }

        cardJadwal.setOnClickListener {
            navController.navigate(R.id.action_nav_home_to_nav_input_jadwal)
        }
        cardRuangan.setOnClickListener {
            navController.navigate(R.id.action_nav_home_to_nav_ruangan)
        }

        cardKeluar.setOnClickListener {
            showLogout(root.context)
        }

        return root
    }

    private fun showLogout(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle("Keluar")
            setMessage("Anda yakin ingin keluar?")
            setPositiveButton("Ya"){dialog,  _->
                dialog.dismiss()
                SharedPref.getInstance(context)!!.logout()
                activity?.finish()
            }
            setNegativeButton("Batal"){ dialog, _ ->
                dialog.dismiss()
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

}