package com.example.login_app.api.service

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun rawJSON(){
    NetworkService!!.getInstance()!!.getJSONApi()!!.getStudents()!!
        .enqueue(object : Callback<List<Student>?> {
            override fun onResponse(
                    call: Call<List<Student>?>,
                    response: Response<List<Student>?>
            ) {
                val list: List<Student>? = response.body()
                if (list != null) {
                    for (student in list) {
                        Log.d("Pretty Printed JSON :", student.getName().toString())
                    }
                } else {
                    Log.d("Pretty Printed JSON :", "list is null")
                }
            }

            override fun onFailure(call: Call<List<Student>?>, t: Throwable) {
                Log.e("RETROFIT_ERROR", "error")
                t.printStackTrace()
            }
        })
}



fun reqlogIn(id: String, lastname: String, firstname: String, callback:(Student?) -> Unit){//настроить возврат

    val req:JSONObject = JSONObject()
    req.put("Id",id)
    req.put("LastName",lastname)
    req.put("FirstName", firstname)
    var student = Student()


    NetworkService.getInstance()!!.getJSONApi()!!.postLogIn(req.toString())!!.enqueue(object : Callback<Student?> {

         override fun onResponse(call: Call<Student?>, response: Response<Student?>) {

            val student_temp = response.body()
            Log.d("Pretty Printed JSON :", "Student from response  : Get student from server ")
            student = student_temp!! // возвращает либо message либо groupid
            callback(student)
        }

        override fun onFailure(call: Call<Student?>, t: Throwable) {
            Log.e("RETROFIT_ERROR", "error")
            callback(null)
            t.printStackTrace()
        }
    })
    }


fun reqGetSubject(groupId: Int, callback: (MySubject?) -> Unit){

    val req:JSONObject = JSONObject()
    req.put("Id", groupId)
    var subject :MySubject? = MySubject()

    NetworkService.getInstance()!!.getJSONApi()!!.postGetSubject(req.toString())?.enqueue(object : Callback<MySubject?> {

        override fun onResponse(call: Call<MySubject?>, response: Response<MySubject?>) {
            val mySubject_temp = response.body()
            Log.d("Pretty Printed JSON :", "Subject from response  : Get subject from server ")
            subject = mySubject_temp //вернет либо null либо subject
           callback(subject)
        }

        override fun onFailure(call: Call<MySubject?>, t: Throwable) {
            Log.e("RETROFIT_ERROR", "error")
            callback(null)
            t.printStackTrace()
        }
    })
}

fun reqGetTopics (subjectId: Int, studentId: String ,callback:(List<Topic>?) -> Unit){

    val req:JSONObject = JSONObject()
    req.put("SubjId", subjectId)
    req.put("StudentId", studentId)

    NetworkService.getInstance()!!.getJSONApi()!!.getTopics(req.toString())?.enqueue(object : Callback<List<Topic>?> {
        override fun onResponse(
                call: Call<List<Topic>?>,
                response: Response<List<Topic>?>
        ) {
            val list: List<Topic>? = response.body()
            callback(list)
        }

        override fun onFailure(call: Call<List<Topic>?>, t: Throwable) {
            Log.e("RETROFIT_ERROR", "error")
            callback(null)
            t.printStackTrace()
        }
    })
}

fun reqGetTest(topicId: Int, callback:(List<Task>?) -> Unit){
    val req:JSONObject = JSONObject()
    req.put("Id", topicId)

    NetworkService.getInstance()!!.getJSONApi()!!.postGetTest(req.toString())
            ?.enqueue(object : Callback<List<Task>?> {

                override fun onResponse(call: Call<List<Task>?>, response: Response<List<Task>?>) {
                    val list: List<Task>? = response.body()
                    if (list != null) {
                        Log.d("Pretty Printed JSON :", "Тест с вопросами пришел успешно" )
                        callback(list);
                    } else {
                        Log.d("Pretty Printed JSON :", "list task is null")
                        callback(null);
                    }
                }

                override fun onFailure(call: Call<List<Task>?>, t: Throwable) {
                    Log.e("RETROFIT_ERROR", "error")
                    t.printStackTrace()
                }
            })

}

fun reqPostResult(result: Result , groupId: Int, subjectId: Int, topicId: Int,callback:(Int) -> Unit){
    val req:JSONObject = JSONObject()
    req.put("StudentId", result.studentId)
    req.put("GroupId", groupId )
    req.put("SubjectId", subjectId)
    req.put("TopicId", topicId)
    req.put("QuNum", result.quNum)
    req.put("RightAnsws", result.rightAnswNum)
    req.put("Mark", result.mark)

    NetworkService.getInstance()!!.getJSONApi()!!.postResult(req.toString())
            ?.enqueue(object : Callback<Int> {

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    val list: Int? = response.body()
                    if (list != 0) {
                        Log.d("Pretty Printed JSON :", "Результат сохранился на сервера" )
                        callback(list!!);
                    } else {
                        Log.d("Pretty Printed JSON :", "Результат НЕ сохранился на сервера")
                        callback(0);
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.e("RETROFIT_ERROR", "error")
                    callback(0);
                    t.printStackTrace()
                }
            })
}
