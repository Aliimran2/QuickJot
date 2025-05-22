package com.miassolutions.quickjot.utils

enum class SortOrder(val value: String) {
    TITLE_ASC("Title A-Z"),
    TITLE_DESC("Title Z-A"),
    TIME_ASC("Time New-Old"),
    TIME_DESC("Time Old-New");

    override fun toString(): String = value
}