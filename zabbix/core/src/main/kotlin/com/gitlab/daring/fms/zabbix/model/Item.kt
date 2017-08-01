package com.gitlab.daring.fms.zabbix.model

data class Item(
        val key: String,
        val charsetName: String = "UTF-8"
) {

    val charset = charset(charsetName)
    
}