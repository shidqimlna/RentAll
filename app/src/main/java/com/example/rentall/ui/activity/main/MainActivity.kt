package com.example.rentall.ui.activity.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.rentall.R
import com.example.rentall.ui.fragment.ChatFragment
import com.example.rentall.ui.fragment.HistoryFragment
import com.example.rentall.ui.fragment.HomeFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val homeFragment: HomeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            activity_main_bottomnavigationbar.setItemSelected(R.id.bottom_menu_home, true)
            homeFragment.let {
                fragmentManager.beginTransaction().replace(
                    R.id.activity_main_fragmentcontainer,
                    it
                )
                    .commit()
            }
        }
        activity_main_bottomnavigationbar.setOnItemSelectedListener(object :
            ChipNavigationBar.OnItemSelectedListener {
            override fun onItemSelected(id: Int) {
                var fragment: Fragment? = null
                when (id) {
                    R.id.bottom_menu_home -> fragment = HomeFragment()
                    R.id.bottom_menu_chat -> fragment = ChatFragment()
                    R.id.bottom_menu_history -> fragment = HistoryFragment()
                }
                if (fragment != null) {
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.let {
                        it.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        it.replace(R.id.activity_main_fragmentcontainer, fragment)
                        it.commit()
                    }
                }
            }
        })
    }
}