package com.imagi.app.component

import android.app.Application
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton
import com.imagi.app.di.module.AcitivyModule
import com.imagi.app.di.module.ViewModelModule
import com.imagi.app.network.NetworkModule
import dagger.BindsInstance
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AcitivyModule::class,
    ViewModelModule::class,
    NetworkModule::class
])

interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(instance: DaggerApplication)
}