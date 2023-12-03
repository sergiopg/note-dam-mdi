package com.dam.ad.notedam.presentation.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import com.dam.ad.notedam.R
import com.dam.ad.notedam.databinding.FragmentCategoriesBinding
import com.dam.ad.notedam.presentation.home.Categoria
import com.dam.ad.notedam.presentation.home.Tarea
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        // Lógica para leer categorías y mostrar CheckBox
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

            // Omite la línea de encabezado
            reader.readLine()

            // Lee cada línea y agrega la categoría a la lista
            var line: String? = reader.readLine()
            while (line != null) {
                val parts = line.split(",")
                if (parts.size == 3) {
                    val id = parts[0].toInt()
                    val nombre = parts[1]
                    val tareas = parseTareas(parts[2])
                    val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        Date()
                    )

                    // Actualiza la fecha de la categoría y de cada tarea con la fecha actual
                    categorias.add(Categoria(id, nombre, fechaActual, tareas.map { tarea ->
                        Tarea(tarea.completada, fechaActual, tarea.texto)
                    }))
                }
                line = reader.readLine()
            }

            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return categorias
    }

    private fun parseTareas(tareasString: String): List<Tarea> {
        val tareas = mutableListOf<Tarea>()
        val tareaParts = tareasString.split(";")
        for (tareaPart in tareaParts) {
            val tareaInfo = tareaPart.split(":")
            if (tareaInfo.size == 3) {
                val completada = tareaInfo[0].toBoolean()
                val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val texto = tareaInfo[2]
                tareas.add(Tarea(completada, fechaActual, texto))
            }
        }
        return tareas
    }

    private fun showCategoriesWithCheckBoxes(view: View, categorias: List<Categoria>) {
        val container = view.findViewById<ViewGroup>(R.id.checkboxContainer)

        for (categoria in categorias) {
            val checkBox = CheckBox(requireContext())
            checkBox.text = "${categoria.nombre} (${categoria.fecha})"
            container.addView(checkBox)

            for (tarea in categoria.subcategorias) {
                val subCheckBox = CheckBox(requireContext())
                subCheckBox.text = tarea.texto
                subCheckBox.isChecked = tarea.completada
                container.addView(subCheckBox)
            }
        }
    }
}