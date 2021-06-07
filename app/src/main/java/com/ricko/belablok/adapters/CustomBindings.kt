package com.ricko.belablok.adapters

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.ricko.belablok.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.reflect.Executable

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

@BindingAdapter("selectedValue")
fun Spinner.setSelectedValue(selectedValue: Int) {
    if (adapter != null) {
        val position = (adapter as ArrayAdapter<Any?>).getPosition(selectedValue)
        setSelection(position, false)
        tag = position
    }
}

@BindingAdapter("selectedValueAttrChanged")
fun Spinner.setInverseBindingListener(listener: InverseBindingListener?) {
    onItemSelectedListener = if (listener == null) null
    else object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            if (tag != position) {
                listener.onChange()
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun Spinner.getSelectedValue() = selectedItem.toString().toInt()