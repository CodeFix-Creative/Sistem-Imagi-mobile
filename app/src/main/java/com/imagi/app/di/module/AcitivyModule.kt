package com.imagi.app.di.module

import com.imagi.app.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AcitivyModule {

    @ContributesAndroidInjector
    internal abstract fun contributeHomeActivity() : HomeFragment
}