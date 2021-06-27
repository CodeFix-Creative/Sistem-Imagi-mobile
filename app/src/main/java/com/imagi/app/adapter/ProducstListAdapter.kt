package com.imagi.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.imagi.app.R
import com.imagi.app.model.Products


class ProducstListAdapter : BaseAdapter {

    var productList = ArrayList<Products>()
    var context: Context? = null

    constructor(context: Context, productsList: ArrayList<Products>) : super() {
        this.context = context
        this.productList = productsList
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