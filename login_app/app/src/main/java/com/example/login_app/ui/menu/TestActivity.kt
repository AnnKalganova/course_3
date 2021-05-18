package com.example.login_app.ui.menu

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.login_app.BuildConfig
import com.example.login_app.R
import com.example.login_app.api.service.Result
import com.example.login_app.api.service.Task
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.login_app.ui.login.TaskViewModelFactory
import com.example.login_app.ui.login.TestViewModelFactory
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.util.*

class TestActivity : AppCompatActivity() {
    var numOfQuestion: TextView? = null
    var question: TextView? = null

    var listcheckBoxes: List<CheckBox>? = null
    var listanswerTexts: List<TextView>? = null

    var viewTest: LinearLayout? = null
    var buttonNext: Button? = null

    var listOfTasks: List<Task>? = null

    var countOfQ: Int = 0
    var qCounter: Int = 1
    var countOfRightAnswers: Int = 0

    var topicId:Int?=null
    var loadTest: ProgressBar? = null

    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topicId = intent.getIntExtra(("topicId"), 0)

        setContentView(R.layout.run_test_activity2)

        numOfQuestion = findViewById(R.id.numOfQuestion)
        question = findViewById(R.id.question)
        viewTest = findViewById(R.id.viewBoxText)
        buttonNext = findViewById(R.id.buttonNext)
        loadTest = findViewById(R.id.loadingTest)

        listcheckBoxes = listOf(
                findViewById(R.id.checkbox_1),
                findViewById(R.id.checkbox_2),
                findViewById(R.id.checkbox_3),
                findViewById(R.id.checkbox_4)
        )

        listanswerTexts = listOf(
                findViewById(R.id.answer_1),
                findViewById(R.id.answer_2),
                findViewById(R.id.answer_3),
                findViewById(R.id.answer_4)
        )


        taskViewModel = ViewModelProvider(this, TaskViewModelFactory())
                .get(TaskViewModel::class.java)
        //Todo: тут запросы
        taskViewModel.getTasks(topicId!!)
        taskViewModel.tasksResult.observe(this@TestActivity, Observer {
            val tasksResult = it ?: return@Observer

            loadTest?.visibility = View.GONE
            if (tasksResult.error != null) {
                //TODO кнопка для возврата на прошлую активити обратотка null
                Toast.makeText(applicationContext, "tasksResult.error", Toast.LENGTH_LONG).show()
                endtest()
            }
            if (tasksResult.success != null) {
                listOfTasks = tasksResult.success.listTasks
                viewTest?.visibility = View.VISIBLE
                buttonNext?.visibility = View.VISIBLE
                countOfQ = listOfTasks?.size!!

                qCounter = 1
                countOfRightAnswers = 0
                val dialog = AlertDialog.Builder(
                        this@TestActivity
                )
                        .setMessage("После нажатия на кнопку 'Подтвердить', будут показаны результаты по этому вопросу")
                        .setPositiveButton(
                                "Ok"
                        ) { _, _ -> setView() }.create()
                dialog.show()
            }
        })

    }



    private fun setView() {
        val task = listOfTasks!![qCounter-1]

        val answers = task.getAnswers()
        val keys = answers?.keys?.toList()

        numOfQuestion!!.text = "Вопрос: " + qCounter.toString() + " из " + countOfQ.toString()
        question?.text = task.getQuestion()
        for (i in 0..3) {
            listanswerTexts?.get(i)!!.setTextColor(Color.GRAY)
            listanswerTexts?.get(i)?.text = answers!![keys?.get(i)]

            listcheckBoxes?.get(i)!!.isChecked = false
        }

    }

    private fun endtest() {
        var result = Result()
        result.quNum = countOfQ
        result.rightAnswNum = countOfRightAnswers
        result.mark = countOfRightAnswers * 10 / countOfQ
        result.topicId = topicId!!
    }

    fun goNextQ(view: View?) {
        var allAnsRight = true

        val answerids = listOfTasks!![qCounter-1].getRightIds()
        val keys = listOfTasks!![qCounter-1].getAnswers()?.keys?.toList()

        when (buttonNext!!.text.toString()) {
            "Подтвердить" -> {
                for (i in 0..3) {
                    if (answerids != null) {
                        if (answerids.contains(keys?.get(i))) {
                            listanswerTexts?.get(i)!!.setTextColor(Color.GREEN)
                            if (!listcheckBoxes?.get(i)!!.isChecked) {
                                allAnsRight = false

                            }
                        } else {
                            if (listcheckBoxes?.get(i)!!.isChecked) {
                                allAnsRight = false

                            }
                        }
                    }
                }
                if (allAnsRight) {
                    countOfRightAnswers++
                }


                if (qCounter < countOfQ) {
                    buttonNext!!.text = "Далее"
                } else {
                    buttonNext!!.text = "Завершить тест"
                }
            }
            "Далее" -> {

                qCounter++
                setView()
                // меняем текст кнопки
                buttonNext!!.text = "Подтвердить"
            }
            "Завершить тест" -> {
                endtest()
            }
            else -> {
            }
        }
    }

}