package br.com.livroandroid.carros.extensions

// Utilizar onClick ao ivés de setOnClickListener
fun android.view.View.onClick(l: (v: android.view.View?) -> Unit) {
    setOnClickListener(l)
}