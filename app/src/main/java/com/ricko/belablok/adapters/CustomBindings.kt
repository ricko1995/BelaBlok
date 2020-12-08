package com.ricko.belablok.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButtonToggleGroup

@BindingAdapter("isViewSelected")
fun View.isViewSelected(_isSelected: Boolean) {
    isSelected = _isSelected
}

@BindingAdapter("clearSelection")
fun MaterialButtonToggleGroup.clearSelection(clear: Boolean) {
    if (clear) clearChecked()
}