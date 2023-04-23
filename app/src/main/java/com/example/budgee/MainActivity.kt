package com.example.budgee

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        bottomNav = findViewById(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.home

        //TODO: Remove
        loadFragment(AssetsFragment())

        loadFragment(DashboardFragment())
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(DashboardFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.assets -> {
                    loadFragment(AssetsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.transactions -> {
                    loadFragment(TransactionsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.history -> {
                    loadFragment(HistoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.budget -> {
                    loadFragment(BudgetFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}