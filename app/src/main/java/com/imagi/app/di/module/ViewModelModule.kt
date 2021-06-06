package com.imagi.app.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imagi.app.di.scope.AppViewModelFactory
import com.imagi.app.di.scope.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.imagi.app.ui.base.CoreViewModel


@Suppress("unused")
@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CoreViewModel::class)
    abstract fun bindCoreViewModel(coreViewModel: CoreViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

}