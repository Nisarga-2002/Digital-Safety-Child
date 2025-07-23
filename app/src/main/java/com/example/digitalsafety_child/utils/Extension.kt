package com.example.digitalsafety_child.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.imageview.ShapeableImageView

fun setWindowInsets(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
        val innerPadding = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        if (view is ShapeableImageView) {
            view.setPadding(innerPadding.left, innerPadding.top, innerPadding.right, 0)
        } else {
            view.setPadding(
                innerPadding.left,
                innerPadding.top,
                innerPadding.right,
                innerPadding.bottom
            )
        }
        insets

    }
}