package com.example.dartapp

import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.dartapp.databinding.ActivityTrainingBinding
import com.example.dartapp.gameModes.GameMode
import com.example.dartapp.gameModes.GameStatus
import com.example.dartapp.gameModes.Mode501
import com.example.dartapp.views.NumberGridDelegate

class TrainingActivity : AppCompatActivity(), NumberGridDelegate {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTrainingBinding

    private lateinit var mode: GameMode
    private lateinit var status: GameStatus

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.numberGrid.delegate = this


        initGameMode()
    }

    private fun initGameMode() {
        val extraString = resources.getString(R.string.extra_string_mode)
        val modeString = intent.getStringExtra(extraString)
        mode = when (modeString) {
            resources.getString(R.string.mode_501_label) -> Mode501()

            // This is the default behaviour
            else -> Mode501()
        }

        status = mode.initGameStatus()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onConfirmPressed(value: Int) {
        if (!mode.isServeValid(value, status))
            invalidInput()
        else
            validInput(value)
    }

    private fun invalidInput() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
        binding.pointsEnteredLabel.startAnimation(animation)
        animation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                binding.numberGrid.number = 0
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }

    private fun validInput(value: Int) {
        status.pointsLeft -= value
        binding.pointsLeftLabel.text = "" + status.pointsLeft

        val animation = AnimationUtils.loadAnimation(this, R.anim.lift_and_fade)
        binding.pointsEnteredLabel.startAnimation(animation)
        animation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                binding.numberGrid.number = 0
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }



    override fun numberUpdated(value: Int) {
        binding.pointsEnteredLabel.text = value.toString()
    }
}