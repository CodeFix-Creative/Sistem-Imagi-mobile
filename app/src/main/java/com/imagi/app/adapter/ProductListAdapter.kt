package com.imagi.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.imagi.app.HomePage
import com.imagi.app.R
import com.imagi.app.model.Product


class ProductListAdapter : BaseAdapter {

    var productList = ArrayList<Product>()
    var context: Context? = null

    constructor(context: Context, productList: ArrayList<Product>) : super() {
        this.context = context
        this.productList = productList
    }

    override fun getCount(): Int {
        return productList.count()
    }

    override fun getItem(position: Int): Any {
        return productList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val product = this.productList[position]

        var v = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var productView = v.inflate(R.layout.product_item, null)
        var name = productView.findViewById<TextView>(R.id.productName)
        name.text = product.name
        return productView
    }


}