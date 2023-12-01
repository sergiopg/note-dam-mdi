package com.dam.ad.notedam.presentation.home

data class Categoria(
    val id: Int,
    val nombre: String,
    val subcategorias: List<String>
)
