package com.ricko.belablok.adapters

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import com.ricko.belablok.R

@BindingAdapter("isViewSelected")
fun View.isViewSelected(_isSelected: Boolean) {
    isSelected = _isSelected
}

@BindingAdapter("clearSelection")
fun MaterialButtonToggleGroup.clearSelection(clear: Boolean) {
    if (clear) clearChecked()
}

@BindingAdapter("fillDropDownMenu")
fun AutoCompleteTextView.fillDropDownMenu(context: Context) {
    val mAdapter = ArrayAdapter(context, R.layout.item_drop_down_menu, context.resources.getStringArray(R.array.languages))
    setAdapter(mAdapter)
}