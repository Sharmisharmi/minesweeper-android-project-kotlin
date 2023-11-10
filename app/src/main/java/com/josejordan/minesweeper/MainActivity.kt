package com.josejordan.minesweeper

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.os.Handler
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity(), MineSweeperView.OnScoreChangeListener, MineSweeperView.OnGameEndListener {


    private lateinit var mineSweeperView: MineSweeperView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var highScoreTextView: TextView

    private var elapsedTime = 0
    private var isTimerRunning = false
    private val timerHandler = Handler()
    private var highScore = 0

    private val timerRunnable = object : Runnable {
        override fun run() {
            elapsedTime++
            timerTextView.text = String.format(getString(R.string.time), elapsedTime)
            timerHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        highScoreTextView = findViewById(R.id.tvHighScore)

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        highScore = sharedPrefs.getInt("highScore", 0)


        highScoreTextView.text = String.format(getString(R.string.high_score_text), highScore)
        mineSweeperView = findViewById(R.id.mineSweeperView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        scoreTextView = findViewById(R.id.tvScore)
        scoreTextView.text = getString(R.string.score, 0)
        timerTextView = findViewById(R.id.tvTimer)

        swipeRefreshLayout.setOnRefreshListener {
            resetGame()
            swipeRefreshLayout.isRefreshing = false
        }

        mineSweeperView.setOnScoreChangeListener(this)

        if (!isTimerRunning) {
            timerHandler.post(timerRunnable)
            isTimerRunning = true
        }

        mineSweeperView.setOnGameEndListener(this)
    }

    private fun resetGame() {
        mineSweeperView.resetGame()
        mineSweeperView.invalidate()
        elapsedTime = 0
        timerTextView.text = String.format(getString(R.string.time), elapsedTime)
        if (!isTimerRunning) {
            timerHandler.post(timerRunnable)
            isTimerRunning = true
        }
    }

    override fun onScoreChanged(score: Int) {
        scoreTextView.text = getString(R.string.score, score)

    }



    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacks(timerRunnable)
    }

    override fun onGameEnd() {
        isTimerRunning = false
        timerHandler.removeCallbacks(timerRunnable)

        val score = mineSweeperView.getCurrentScore()
        if (score > highScore) {
            highScore = score
            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putInt("highScore", highScore).apply()


            highScoreTextView.text = String.format(getString(R.string.high_score_text), highScore)

        }else{
           showAlert()
        }
    }
    @SuppressLint("MissingInflatedId")
    fun showAlert() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        val view = LayoutInflater.from(this).inflate(R.layout.dialog,null)
        builder.setView(view)
        builder.setCanceledOnTouchOutside(false)
        builder.show()

        var tryAgain: LinearLayout = view.findViewById(R.id.tryAgain)

        tryAgain.setOnClickListener {
            resetGame()
        }



    }
}

