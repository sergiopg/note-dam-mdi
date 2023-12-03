package com.dam.ad.notedam.presentation.home

data class Categoria(
    val id: Int,
    val nombre: String,
    val fecha: String,
    val subcategorias: List<Tarea>
)
