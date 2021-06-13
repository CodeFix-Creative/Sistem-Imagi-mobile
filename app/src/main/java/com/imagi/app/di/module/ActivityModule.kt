package com.imagi.app.di.module

import com.imagi.app.ui.login.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributeLoginActivity(): LoginActivity
}