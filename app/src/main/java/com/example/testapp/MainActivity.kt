package com.example.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.os.Handler
/*

class MainActivity : AppCompatActivity(), View.OnClickListener, MineSweeperView.OnScoreChangeListener {

    private lateinit var mineSweeperView: MineSweeperView
    private lateinit var resetButton: Button
    private lateinit var scoreTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mineSweeperView = findViewById(R.id.mineSweeperView)
        resetButton = findViewById(R.id.reset_button)
        scoreTextView = findViewById(R.id.tvScore)

        mineSweeperView.resetGame()
        resetButton.setOnClickListener(this)

        // Establecer el listener en lugar de scoreUpdateListener
        mineSweeperView.setOnScoreChangeListener(this)
    }

    override fun onClick(view: View?) {
        if (view == resetButton) {
            mineSweeperView.resetGame()
        }
    }

    // Implementa el método onScoreChanged() de la interfaz OnScoreChangeListener
    override fun onScoreChanged(score: Int) {
        scoreTextView.text = "Score: $score"
    }
}
*/


class MainActivity : AppCompatActivity(), View.OnClickListener, MineSweeperView.OnScoreChangeListener, MineSweeperView.OnGameEndListener {


    private lateinit var mineSweeperView: MineSweeperView
    private lateinit var resetButton: Button
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private var elapsedTime = 0
    private var isTimerRunning = false

    private val timerHandler = Handler()

    private val timerRunnable = object : Runnable {
        override fun run() {
            elapsedTime++
            timerTextView.text = "Tiempo: $elapsedTime"
            timerHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mineSweeperView = findViewById(R.id.mineSweeperView)
        resetButton = findViewById(R.id.reset_button)
        scoreTextView = findViewById(R.id.tvScore)
        timerTextView = findViewById(R.id.tvTimer)



        mineSweeperView.resetGame()
        resetButton.setOnClickListener(this)

        // Establecer el listener en lugar de scoreUpdateListener
        mineSweeperView.setOnScoreChangeListener(this)

        if (!isTimerRunning) {
            timerHandler.post(timerRunnable)
            isTimerRunning = true
        }

        mineSweeperView.setOnGameEndListener(this)

    }

    override fun onClick(view: View?) {
        if (view == resetButton) {
            mineSweeperView.resetGame()
            elapsedTime = 0
            timerTextView.text = "Tiempo: $elapsedTime"
            if (!isTimerRunning) {
                timerHandler.post(timerRunnable)
                isTimerRunning = true
            }
        }
    }

    // Implementa el método onScoreChanged() de la interfaz OnScoreChangeListener
    override fun onScoreChanged(score: Int) {
        scoreTextView.text = "Puntuación: $score"
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacks(timerRunnable)
    }

    override fun onGameEnd() {
        isTimerRunning = false
        timerHandler.removeCallbacks(timerRunnable)
    }

}

