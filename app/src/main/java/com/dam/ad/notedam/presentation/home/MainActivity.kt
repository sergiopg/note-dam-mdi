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
                        readAndShowCategories()
                    }
                    1 -> saveRemotely()
                }
                dialogInterface.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }
    private fun saveLocally() {
        val categorias = listOf(
            Categoria(1, "Trabajo", listOf("Reuniones", "Proyectos")),
            Categoria(2, "Personal", listOf("Compras", "Ejercicio")),
            Categoria(3, "Estudio", listOf("Investigación", "Proyectos académicos"))
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona el formato de archivo:")
            .setItems(arrayOf("CSV", "JSON")) { dialogInterface: DialogInterface, which: Int ->
                when (which) {
                    0 -> {
                        saveToCSV(categorias)
                    }
                    1 -> {
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
            val writer = FileWriter(file)
            writer.append("id,nombre,subcategorias\n")
            categorias.forEach { categoria ->
                val subcategorias = categoria.subcategorias.joinToString(";") // Separador para las subcategorías
                writer.append("${categoria.id},${categoria.nombre},$subcategorias\n")
            }
            writer.close()
            Toast.makeText(this,"Archivo CSV guardado localmente en ${file.absolutePath}",Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,"Error al guardar el archivo CSV localmente",Toast.LENGTH_SHORT).show()
        }
    }
    private fun writeCategoriesToJSON(file: File, categorias: List<Categoria>) {
        try {
            val writer = FileWriter(file)
            writer.append("[")
            categorias.forEachIndexed { index, categoria ->
                val subcategoriasArray = categoria.subcategorias.joinToString("\",\"") // Separador para las subcategorías
                writer.append("{")
                writer.append("\"id\": ${categoria.id},")
                writer.append("\"nombre\": \"${categoria.nombre}\",")
                writer.append("\"subcategorias\": [\"$subcategoriasArray\"]")
                writer.append("}")
                if (index < categorias.size - 1) {
                    writer.append(",")
                }
            }
            writer.append("]")
            writer.close()
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
            val categorias = readCategoriesFromCSV(file)
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
    }
}