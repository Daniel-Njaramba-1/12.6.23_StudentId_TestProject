package com.example.studentid.lecturermodule

import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.content.Intent

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Intent.parcelableArray(key: String): Array<T>? = when {
    SDK_INT >= 33 -> {
        getParcelableArrayExtra(key, T::class.java)?.filterIsInstance<T>()?.toTypedArray()
    }
    else -> null
}




