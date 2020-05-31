package com.kunal.chineaseappuninstaller.model

import android.graphics.drawable.Drawable

class AppModel {
    constructor()
    constructor(name: String?, icon: Drawable?, packages: String?) {
        this.name = name
        this.icon = icon
        this.packages = packages
    }

    var name: String? = null
    var icon: Drawable? = null
    var packages: String? = null
}