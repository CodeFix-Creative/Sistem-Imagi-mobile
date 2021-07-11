package com.imagi.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.ui.home.HomeFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MenuMerchant : AppCompatActivity(), HasSupportFragmentInjector {

    lateinit var dbServices: DbServices


    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        dbServices = DbServices(this)
        dbServices.mContext = this
        setContentView(R.layout.activity_menu_merchant)

        setContentView(R.layout.activity_menu_merchant)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationMerchant)
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val fragment = HomeFragment()
                    addFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    val fragment = ProfilePage.newInstance()
                    addFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
//            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
            .addToBackStack(null)
            .commit()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

}