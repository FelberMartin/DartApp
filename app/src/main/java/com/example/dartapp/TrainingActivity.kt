package com.example.dartapp

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
import com.example.dartapp.database.LegDatabase
import com.example.dartapp.databinding.ActivityTrainingBinding
import com.example.dartapp.dialogs.LegFinishedDialog
import com.example.dartapp.util.App
import com.example.dartapp.util.Strings
import com.example.dartapp.viewmodels.GameViewModel
import com.example.dartapp.viewmodels.GameViewModelFactory
import com.example.dartapp.views.NumberGridDelegate


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
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onConfirmPressed(value: Int) {
        val valid = viewModel.processServe(value)
        var animationId = R.anim.lift_and_fade

        if (!valid)
            animationId = R.anim.shake

        val animation = AnimationUtils.loadAnimation(this, animationId)
        animation.setAnimationEndListener { binding.numberGrid.number = 0 }
        binding.pointsEnteredLabel.startAnimation(animation)

        // Check for game over
        if (viewModel.isFinished())
            legFinished()
    }

    private fun legFinished() {
        val dialog = LegFinishedDialog(this)
        dialog.show()

        dialog.setOnBackClickedListener { this.finish() }
        dialog.setOnRestartClickedListener { viewModel.restart() }

        Thread {
            viewModel.saveGameToDatabase()
        }.start()
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
