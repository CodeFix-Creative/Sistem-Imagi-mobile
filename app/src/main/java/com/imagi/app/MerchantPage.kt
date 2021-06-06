package com.imagi.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MerchantPage : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val myView = inflater.inflate(R.layout.fragment_marchant_page, container, false)

        return myView
    }



    companion object {
        fun newInstance(): MerchantPage = MerchantPage()
    }
}