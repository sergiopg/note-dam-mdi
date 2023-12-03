package com.dam.ad.notedam.presentation.home

import android.app.AlertDialog
import android.content.Context
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            Categoria(1, "Trabajo", getCurrentDate(), listOf(Tarea(false, getCurrentDate(), "Reuniones"), Tarea(true, getCurrentDate(), "Proyectos"))),
            Categoria(2, "Personal", getCurrentDate(), listOf(Tarea(false, getCurrentDate(), "Compras"), Tarea(true, getCurrentDate(), "Ejercicio"))),
            Categoria(3, "Estudio", getCurrentDate(), listOf(Tarea(false, getCurrentDate(), "Investigación"), Tarea(true, getCurrentDate(), "Proyectos académicos")))
            // Agrega más categorías con subcategorías según tu necesidad
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona el formato de archivo:")
            .setItems(arrayOf("CSV", "JSON")) { dialogInterface: DialogInterface, which: Int ->
                when (which) {
                    0 -> {
                        // Guardar en formato CSV
                        saveToCSV(categorias, this)
                    }
                    1 -> {
                        // Guardar en formato JSON
                        saveToJSON(categorias, this)
                    }
                }
                dialogInterface.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun saveToCSV(categorias: List<Categoria>, context: Context) {
        val fileName = "categorias.csv"
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(filesDir, fileName)

        // Verificar si el archivo ya existe
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                // Manejar el error según tus necesidades
                return
            }
        }

        writeCategoriesToCSV(file, categorias)
    }

    private fun saveToJSON(categorias: List<Categoria>, context: Context) {
        val fileName = "categorias.json"
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(filesDir, fileName)

        // Verificar si el archivo ya existe
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                // Manejar el error según tus necesidades
                return
            }
        }

        writeCategoriesToJSON(file, categorias)
    }

    private fun writeCategoriesToCSV(file: File, categorias: List<Categoria>) {
        try {
            // Crea un escritor para el archivo CSV en modo de añadir (append)
            val writer = FileWriter(file, true)

            // Escribe cada categoría en una línea separada
            categorias.forEach { categoria ->
                val subcategorias = categoria.subcategorias.joinToString(";") // Separador para las subcategorías
                writer.append("${categoria.id},${categoria.nombre},${categoria.fecha},$subcategorias\n")
            }

            // Cierra el escritor
            writer.close()

            // Notifica al usuario que se guardó localmente
            Toast.makeText(this, "Categorías agregadas al archivo CSV en ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            // Manejar el error según tus necesidades
            Toast.makeText(this, "Error al agregar categorías al archivo CSV", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeCategoriesToJSON(file: File, categorias: List<Categoria>) {
        try {
            // Crea un escritor para el archivo JSON en modo de añadir (append)
            val writer = FileWriter(file, true)

            // Escribe cada categoría como un objeto JSON separado por comas
            categorias.forEachIndexed { index, categoria ->
                val subcategoriasArray = categoria.subcategorias.joinToString("\",\"") // Separador para las subcategorías
                writer.append("{")
                writer.append("\"id\": ${categoria.id},")
                writer.append("\"nombre\": \"${categoria.nombre}\",")
                writer.append("\"fecha\": \"${categoria.fecha}\",")
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
            Toast.makeText(this, "Categorías agregadas al archivo JSON en ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            // Manejar el error según tus necesidades
            Toast.makeText(this, "Error al agregar categorías al archivo JSON", Toast.LENGTH_SHORT).show()
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
                if (parts.size == 4) {
                    val id = parts[0].toInt()
                    val nombre = parts[1]
                    val fecha = parts[2]
                    val subcategorias = parts[3].split(";")
                    categorias.add(Categoria(id, nombre, fecha, subcategorias.map { tarea ->
                        val tareaInfo = tarea.split(":")
                        if (tareaInfo.size == 3) {
                            Tarea(tareaInfo[0].toBoolean(), tareaInfo[1], tareaInfo[2])
                        } else {
                            // Manejar un formato incorrecto o agregar lógica adicional según sea necesario
                            Tarea(false, "", "")
                        }
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

    private fun formatCategories(categorias: List<Categoria>): String {
        val formattedCategories = StringBuilder()
        for (categoria in categorias) {
            formattedCategories.append("Categoria: ${categoria.nombre}, Fecha: ${categoria.fecha}\n")
            for (subcategoria in categoria.subcategorias) {
                formattedCategories.append("  Subcategoria: ${subcategoria.texto}, Completada: ${subcategoria.completada}, Fecha: ${subcategoria.fecha}\n")
            }
        }
        return formattedCategories.toString()
    }

    private fun saveRemotely() {
        // Implementa la lógica para guardar de forma remota
    }

}