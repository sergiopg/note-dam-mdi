package com.dam.ad.notedam.presentation.home

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dam.ad.notedam.R
import com.dam.ad.notedam.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

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
        // Aquí puedes implementar la lógica para guardar localmente
        // por ejemplo, escribir en un archivo en el almacenamiento interno
        val file = File(filesDir, "nombre_del_archivo.csv")
        // Realiza la operación de escritura en el archivo local
    }

    private fun saveRemotely() {
        // Aquí puedes implementar la lógica para guardar de forma remota
        // por ejemplo, enviar los datos a un servidor
    }
}