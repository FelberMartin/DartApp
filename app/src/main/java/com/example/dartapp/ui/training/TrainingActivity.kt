package com.example.dartapp.ui.training

import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.dartapp.R
import com.example.dartapp.databinding.ActivityTrainingBinding
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.ui.dialogs.CheckoutDialog
import com.example.dartapp.ui.dialogs.DoubleAttemptsDialog
import com.example.dartapp.ui.dialogs.ExitTrainingDialog
import com.example.dartapp.ui.dialogs.LegFinishedDialog
import com.example.dartapp.ui.training.viewmodels.GameViewModel
import com.example.dartapp.views.numbergrid.NumberGridDelegate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrainingActivity : AppCompatActivity(), NumberGridDelegate {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTrainingBinding

    private lateinit var viewModel: GameViewModel

    @Inject
    lateinit var viewModelFactory: GameViewModel.GameViewModelFactory

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.numberGrid.delegate = this

        initViewModel()

        binding.gameViewModel = viewModel
        binding.lifecycleOwner = this

        binding.buttonUndo.setOnClickListener { viewModel.undo() }
    }

    private fun initViewModel() {
        val args: TrainingActivityArgs by navArgs()
        val gameMode = GameMode.fromTypeId(args.gameModeId)

        val _viewModel: GameViewModel by viewModels {
            GameViewModel.providesFactory(viewModelFactory, gameMode)
        }
        viewModel = _viewModel
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val exitDialog = ExitTrainingDialog(this)
        exitDialog.show()

        exitDialog.setOnExitClickedListener { super.onBackPressed() }
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

    private fun validServeFinished(value: Int, dartCount: Int = 3) {
        viewModel.processServe(value, dartCount)

        animatePointsEnteredLabel()
        checkLegFinished()
    }

    private fun animatePointsEnteredLabel() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.lift_and_fade)
        animation.setAnimationEndListener { binding.numberGrid.number = 0 }
        binding.pointsEnteredLabel.startAnimation(animation)
    }

    private fun checkLegFinished() {
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

            if (viewModel.wouldBeFinished(serveValue))
                showCheckoutDialog(serveValue)
            else
                validServeFinished(serveValue)
        }
    }

    private fun showCheckoutDialog(serveValue: Int){
        val dialog = CheckoutDialog(this)
        dialog.show()

        dialog.setClickListener {
            dialog.dismiss()
            validServeFinished(serveValue, dartCount = it)
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
