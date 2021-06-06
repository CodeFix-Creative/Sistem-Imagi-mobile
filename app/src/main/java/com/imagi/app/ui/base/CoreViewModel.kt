package com.imagi.app.ui.base

import androidx.lifecycle.ViewModel
import com.imagi.app.network.DataManager
import javax.inject.Inject

class CoreViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {


}