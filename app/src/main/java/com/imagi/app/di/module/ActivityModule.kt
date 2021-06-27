package com.imagi.app.di.module

import com.imagi.app.MainActivity
import com.imagi.app.MarketFragment
import com.imagi.app.ProfilePage
import com.imagi.app.ui.login.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributeLoginActivity(): LoginActivity


    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeFragmentProfile(): ProfilePage

    @ContributesAndroidInjector
    internal abstract fun contributeFragmentMarket(): MarketFragment
}