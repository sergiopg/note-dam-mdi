package com.dam.ad.notedam.presentation.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.dam.ad.notedam.R
import com.dam.ad.notedam.presentation.home.Categoria
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        val categorias = readCategoriesFromCSV()
        showCategoriesWithCheckBoxes(view, categorias)
        return view
    }
    private fun readCategoriesFromCSV(): List<Categoria> {
        val fileName = "categorias.csv"
        val file = File(requireActivity().filesDir, fileName)
        val categorias = mutableListOf<Categoria>()
        try {
            val reader = BufferedReader(FileReader(file))
            reader.readLine()
            var line: String? = reader.readLine()
            while (line != null) {
                val parts = line.split(",")
                if (parts.size == 3) {
                    val id = parts[0].toInt()
                    val nombre = parts[1]
                    val subcategorias = parts[2].split(";")
                    categorias.add(Categoria(id, nombre, subcategorias))
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return categorias
    }
    private fun showCategoriesWithCheckBoxes(view: View, categorias: List<Categoria>) {
        val container = view.findViewById<ViewGroup>(R.id.checkboxContainer)
        for (categoria in categorias) {
            val checkBox = CheckBox(requireContext())
            checkBox.text = categoria.nombre
            container.addView(checkBox)
        }
    }
}