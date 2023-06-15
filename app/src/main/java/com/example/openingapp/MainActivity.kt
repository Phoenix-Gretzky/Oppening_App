package com.example.openingapp

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.openingapp.databinding.ActivityMain2Binding
import com.example.openingapp.databinding.ActivityMainBinding
import com.example.openingapp.ui.dashboard.DashboardFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        /*   val navView: BottomNavigationView = binding.navView

           val navController = findNavController(R.id.nav_host_fragment_activity_main)
           // Passing each menu ID as a set of Ids because each
           // menu should be considered as top level destinations.
           val appBarConfiguration = AppBarConfiguration(
               setOf(
                   R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
               )
           )
           setupActionBarWithNavController(navController, appBarConfiguration)
           navView.setupWithNavController(navController)*/


        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(
            binding.frameLayout.id, DashboardFragment(binding), "DashboardFragment"
        )
        fragmentTransaction.addToBackStack("DashboardFragment")
        fragmentTransaction.commit()


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}