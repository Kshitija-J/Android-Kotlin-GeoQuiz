package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

import androidx.lifecycle.ViewModelProvider

import com.example.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"
private const val REQUEST_CODE_CHEAT = 0
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var cheatButton: Button


    private val questionBank = listOf(
        Question(R.string.question_australia, true,false),
        Question(R.string.question_oceans, true,false),
        Question(R.string.question_mideast, false,false),
        Question(R.string.question_africa, false,false),
        Question(R.string.question_americas, true,false),
        Question(R.string.question_asia, true,false),
        Question(R.string.question_india, true,false),
    )

    private var currentIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cheatButton = findViewById(R.id.cheat_button)

        if(quizViewModel.cCount > 0) {
            binding.cheatNumber.text = "You have ${quizViewModel.cCount} attempts left"
        }
        val questionResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionResId)

        binding.trueButton.setOnClickListener { _ ->
            checkAnswer(true)
        }


        binding.falseButton.setOnClickListener { _ ->
            checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            updateQuestion()
        }

//        binding.previousButton.setOnClickListener {
//            updateQuestion()
//        }
        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = questionBank[currentIndex].answer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }


    }
    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.cCount -= 1
            if(quizViewModel.cCount > 0) {
                binding.cheatNumber.text = "You have ${quizViewModel.cCount} attempts left"
            } else {
                binding.cheatNumber.text = "You have no more attempts left"
                binding.cheatButton.isEnabled = false
            }
            questionBank[currentIndex].isCheatUsed =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        currentIndex = (currentIndex + 1) % questionBank.size
        val questionResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionResId)
    }

    private fun prevQuestion() {
        currentIndex = (currentIndex - 1) % questionBank.size
        val questionResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer =  questionBank[currentIndex].answer


        val messageResId = when {
            questionBank[currentIndex].isCheatUsed -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Snackbar.make(binding.root, messageResId , Snackbar.LENGTH_SHORT).show()
    }
}