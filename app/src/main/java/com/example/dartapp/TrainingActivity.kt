package com.example.dartapp

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.dartapp.databinding.ActivityTrainingBinding
import com.example.dartapp.views.NumberGridDelegate

class TrainingActivity : AppCompatActivity(), NumberGridDelegate {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTrainingBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.numberGrid.delegate = this
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onConfirmPressed(value: Int) {

    }

    override fun numberUpdated(value: Int) {
        binding.labelScore.text = value.toString()
    }
}