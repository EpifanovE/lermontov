package ru.eecode.poems.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.eecode.poems.R

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun Fragment.hideKeyboard() {
    view?.let {
        activity?.hideKeyboard(it)
    }
}

@SuppressLint("Recycle")
fun storeImageFinder(context: Context, sku: String) : Drawable? {
    val resProducts: Array<String> = context.resources.getStringArray(R.array.products)
    val index = resProducts.indexOf(sku)
    return context.resources.obtainTypedArray(R.array.product_images).getDrawable(index)
}