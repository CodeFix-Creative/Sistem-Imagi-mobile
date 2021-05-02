package com.imagi.app.model

class Product {
    var name: String ? = "-"
    var image: String ? = ""
    var price: Int? = 0

    constructor(name: String, image:String, price:Int) {
        this.name = name
        this.image = image
        this.price = price
    }

}