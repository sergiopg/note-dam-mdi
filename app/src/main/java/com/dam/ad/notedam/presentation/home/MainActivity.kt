package com.dam.ad.notedam.presentation.home

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dam.ad.notedam.R
import com.dam.ad.notedam.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
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
                    0 -> saveLocally()
                    1 -> saveRemotely()
                }
                dialogInterface.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun saveLocally() {
        // Supongamos que tienes una lista de categorías
        val categorias = listOf(
            Categoria(1, "Trabajo"),
            Categoria(2, "Personal"),
            Categoria(3, "Estudio")
            // Agrega más categorías según tu necesidad
        )

        // Nombre del archivo CSV
        val fileName = "categorias.csv"

        // Ruta completa del archivo en el almacenamiento interno
        val file = File(filesDir, fileName)

        // Escribe las categorías en el archivo CSV
        writeCategoriesToCSV(file, categorias)
    }

    private fun writeCategoriesToCSV(file: File, categorias: List<Categoria>) {
        try {
            // Crea un escritor para el archivo CSV
            val writer = FileWriter(file)

            // Escribe la línea de encabezado
            writer.append("id,nombre\n")

            // Escribe cada categoría en una línea separada
            categorias.forEach { categoria ->
                writer.append("${categoria.id},${categoria.nombre}\n")
            }

            // Cierra el escritor
            writer.close()

            // Notifica al usuario que se guardó localmente
            Toast.makeText(this,"Archivo CSV guardado localmente en ${file.absolutePath}",Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,"Error al guardar el archivo CSV localmente",Toast.LENGTH_LONG).show()
        }
    }

    private fun saveRemotely() {
        // Implementa la lógica para guardar de forma remota
    }
}