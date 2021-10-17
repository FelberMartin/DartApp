package com.example.dartapp.ui.training

import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.dartapp.R
import com.example.dartapp.databinding.ActivityTrainingBinding
import com.example.dartapp.ui.dialogs.DoubleAttemptsDialog
import com.example.dartapp.ui.dialogs.LegFinishedDialog
import com.example.dartapp.util.Strings
import com.example.dartapp.ui.training.viewmodels.GameViewModel
import com.example.dartapp.ui.training.viewmodels.GameViewModelFactory
import com.example.dartapp.views.numbergrid.NumberGridDelegate


class TrainingActivity : AppCompatActivity(), NumberGridDelegate {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTrainingBinding

    private lateinit var viewModel: GameViewModel
    private lateinit var viewModelFactory: GameViewModelFactory

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.numberGrid.delegate = this

        val extraString = Strings.get(R.string.extra_string_mode)
        val modeString = intent.getStringExtra(extraString)
        viewModelFactory = GameViewModelFactory(modeString)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GameViewModel::class.java)

        binding.gameViewModel = viewModel
        binding.lifecycleOwner = this

        binding.buttonUndo.setOnClickListener { viewModel.undo() }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onConfirmPressed(value: Int) {
        if (!viewModel.isServeValid(value)) {
            invalidServe()
            return
        }

        if (viewModel.askForDoubleAttempts())
            showDoubleAttemptsDialog(value)
        else
            validServeFinished(value)
    }

    private fun invalidServe() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
        animation.setAnimationEndListener { binding.numberGrid.number = 0 }
        binding.pointsEnteredLabel.startAnimation(animation)
    }

    private fun validServeFinished(value: Int) {
        viewModel.processServe(value)

        val animation = AnimationUtils.loadAnimation(this, R.anim.lift_and_fade)
        animation.setAnimationEndListener { binding.numberGrid.number = 0 }
        binding.pointsEnteredLabel.startAnimation(animation)

        // Check for game over
        if (viewModel.isFinished())
            legFinished()
    }

    private fun legFinished() {
        showLegFinishedDialog()

        Thread {
            viewModel.saveGameToDatabase()
        }.start()
    }

    private fun showLegFinishedDialog() {
        val dialog = LegFinishedDialog(this)
        dialog.show()

        dialog.setOnBackClickedListener { this.finish() }
        dialog.setOnRestartClickedListener { viewModel.restart() }
    }

    private fun showDoubleAttemptsDialog(serveValue: Int){
        val dialog = DoubleAttemptsDialog(this)
        dialog.show()

        dialog.setClickListener {
            viewModel.addDoubleAttempts(it)
            dialog.dismiss()
            validServeFinished(serveValue)
        }
    }


    override fun numberUpdated(value: Int) {
        binding.pointsEnteredLabel.text = value.toString()
    }


}

// Extension of Animation
fun Animation.setAnimationEndListener(listener: (Animation?) -> Unit) {
    setAnimationListener(object: Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            listener.invoke(p0)
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }
    })
}
