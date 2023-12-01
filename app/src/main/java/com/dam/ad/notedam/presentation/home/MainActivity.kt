package com.dam.ad.notedam.presentation.home

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dam.ad.notedam.R
import com.dam.ad.notedam.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavMenu()
        showSaveFileDialog()
    }

    private fun initNavMenu() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavView.setupWithNavController(navController)
    }


    private fun showSaveFileDialog() {
        val options = arrayOf("Local", "Remoto")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Guardar fichero:")
            .setItems(options) { dialogInterface: DialogInterface, which: Int ->
                when (which) {
                    0 -> {
                        saveLocally()
                        readAndShowCategories() // Leer y mostrar categorías después de guardar localmente
                    }
                    1 -> saveRemotely()
                }
                dialogInterface.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun saveLocally() {
        // Supongamos que tienes una lista de categorías con subcategorías
        val categorias = listOf(
            Categoria(1, "Trabajo", listOf("Reuniones", "Proyectos")),
            Categoria(2, "Personal", listOf("Compras", "Ejercicio")),
            Categoria(3, "Estudio", listOf("Investigación", "Proyectos académicos"))
            // Agrega más categorías con subcategorías según tu necesidad
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona el formato de archivo:")
            .setItems(arrayOf("CSV", "JSON")) { dialogInterface: DialogInterface, which: Int ->
                when (which) {
                    0 -> {
                        // Guardar en formato CSV
                        saveToCSV(categorias)
                    }
                    1 -> {
                        // Guardar en formato JSON
                        saveToJSON(categorias)
                    }
                }
                dialogInterface.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun saveToCSV(categorias: List<Categoria>) {
        val fileName = "categorias.csv"
        val filesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(filesDir, fileName)
        writeCategoriesToCSV(file, categorias)
    }

    private fun saveToJSON(categorias: List<Categoria>) {
        val fileName = "categorias.json"
        val filesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(filesDir, fileName)
        writeCategoriesToJSON(file, categorias)
    }

    private fun writeCategoriesToCSV(file: File, categorias: List<Categoria>) {
        try {
            // Crea un escritor para el archivo CSV
            val writer = FileWriter(file)

            // Escribe la línea de encabezado
            writer.append("id,nombre,subcategorias\n")

            // Escribe cada categoría en una línea separada
            categorias.forEach { categoria ->
                val subcategorias = categoria.subcategorias.joinToString(";") // Separador para las subcategorías
                writer.append("${categoria.id},${categoria.nombre},$subcategorias\n")
            }

            // Cierra el escritor
            writer.close()

            // Notifica al usuario que se guardó localmente
            Toast.makeText(this,"Archivo CSV guardado localmente en ${file.absolutePath}",Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,"Error al guardar el archivo CSV localmente",Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeCategoriesToJSON(file: File, categorias: List<Categoria>) {
        try {
            // Crea un escritor para el archivo JSON
            val writer = FileWriter(file)

            // Inicia un objeto JSON
            writer.append("[")

            // Escribe cada categoría como un objeto JSON separado por comas
            categorias.forEachIndexed { index, categoria ->
                val subcategoriasArray = categoria.subcategorias.joinToString("\",\"") // Separador para las subcategorías
                writer.append("{")
                writer.append("\"id\": ${categoria.id},")
                writer.append("\"nombre\": \"${categoria.nombre}\",")
                writer.append("\"subcategorias\": [\"$subcategoriasArray\"]")
                writer.append("}")

                // Agrega una coma si no es la última categoría
                if (index < categorias.size - 1) {
                    writer.append(",")
                }
            }

            // Cierra el array JSON
            writer.append("]")

            // Cierra el escritor
            writer.close()

            // Notifica al usuario que se guardó localmente
            Toast.makeText(this, "Archivo JSON guardado localmente en ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar el archivo JSON localmente", Toast.LENGTH_SHORT).show()
        }
    }


    private fun readAndShowCategories() {
        try {
            val fileName = "categorias.csv"
            val file = File(filesDir, fileName)

            // Lee las categorías desde el archivo CSV
            val categorias = readCategoriesFromCSV(file)

            // Muestra las categorías en un Toast
            Toast.makeText(this,"Categorías leídas:\n${formatCategories(categorias)}",Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,"Error al leer las categorías desde el archivo CSV",Toast.LENGTH_SHORT).show()
        }
    }

    private fun readCategoriesFromCSV(file: File): List<Categoria> {
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

    private fun formatCategories(categorias: List<Categoria>): String {
        val formattedCategories = StringBuilder()
        for (categoria in categorias) {
            formattedCategories.append("Categoria: ${categoria.nombre}\n")
            for (subcategoria in categoria.subcategorias) {
                formattedCategories.append("  Subcategoria: $subcategoria\n")
            }
        }
        return formattedCategories.toString()
    }

    private fun saveRemotely() {
        // Implementa la lógica para guardar de forma remota
    }
}