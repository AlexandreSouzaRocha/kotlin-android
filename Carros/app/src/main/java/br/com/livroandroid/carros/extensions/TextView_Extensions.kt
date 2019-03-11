package br.com.livroandroid.carros.extensions

import android.widget.TextView
// Cria uma extensão para converter os dados para string
var TextView.string: String
    get() = text.toString()
    set(value) {
        text = value
    }
// Valida os dados do TextView
fun TextView.isEmpty() = text.trim().isEmpty()