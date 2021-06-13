package com.imagi.app.di.module

import com.imagi.app.HomeFragment
import com.imagi.app.ui.login.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AcitivyModule {

    @ContributesAndroidInjector
    internal abstract fun contributeHomeActivity() : LoginActivity
}