package com.org.coroutineexample2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

/*
*
*
* This was the log cat result which proves that both the job started in parallel. It did not start at the same time because it is sequentially written but execution happened in parallel.
*
* 2020-05-10 15:48:42.346 18667-18704/com.org.coroutineexample2 I/System.out: debug: launching job1 DefaultDispatcher-worker-3
* 2020-05-10 15:48:42.359 18667-18703/com.org.coroutineexample2 I/System.out: debug: launching job2 DefaultDispatcher-worker-2
* 2020-05-10 15:48:43.380 18667-18704/com.org.coroutineexample2 I/System.out: debug: job2 completed in 1021 ms
* 2020-05-10 15:48:43.380 18667-18704/com.org.coroutineexample2 I/System.out: debug: Job2 is completed system time is 1589140123380
* 2020-05-10 15:48:43.380 18667-18706/com.org.coroutineexample2 I/System.out: debug: job1 completed in 1038 ms
* 2020-05-10 15:48:43.382 18667-18706/com.org.coroutineexample2 I/System.out: debug: Job1 is completed system time is 1589140123381
*
*
* when job1.join() is called then both the jobs execute in sequention order instead of executing in parallel
* 2020-05-10 15:46:39.926 18397-18427/com.org.coroutineexample2 I/System.out: debug: launching job1 DefaultDispatcher-worker-3
* 2020-05-10 15:46:40.943 18397-18427/com.org.coroutineexample2 I/System.out: debug: job1 completed in 1018 ms
* 2020-05-10 15:46:40.944 18397-18427/com.org.coroutineexample2 I/System.out: debug: Job1 is completed system time is 1589140000944
* 2020-05-10 15:46:40.946 18397-18426/com.org.coroutineexample2 I/System.out: debug: launching job2 DefaultDispatcher-worker-2
* 2020-05-10 15:46:41.971 18397-18427/com.org.coroutineexample2 I/System.out: debug: job2 completed in 1024 ms
* 2020-05-10 15:46:41.971 18397-18427/com.org.coroutineexample2 I/System.out: debug: Job2 is completed system time is 1589140001971
* */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            fakeAPICall()
        }


    }

    private fun fakeAPICall() {
       CoroutineScope(IO).launch {
           val job1 = launch {
               val time1 = measureTimeMillis {
                   println("debug: launching job1 ${Thread.currentThread().name}")
                   setTextonUI(fakeAPI1())
               }
               println("debug: job1 completed in ${time1} ms")
           }

           job1.invokeOnCompletion {
               println("debug: Job1 is completed system time is ${System.currentTimeMillis()}")
           }

           //job1.join()

           val job2 = launch {
               val time2 = measureTimeMillis {
                   println("debug: launching job2 ${Thread.currentThread().name}")
                   setTextonUI(fakeAPI2())
               }

               println("debug: job2 completed in ${time2} ms")
           }
           job2.invokeOnCompletion {
               println("debug: Job2 is completed system time is ${System.currentTimeMillis()}")
           }


       }
    }


    public suspend fun setTextonUI(text: String) {
        withContext(Main) {
            textView.text = text
        }

    }
    public suspend fun fakeAPI1(): String {
        delay(1000)
        return "RESULT_1"
    }

    public suspend fun fakeAPI2(): String {
        delay (1000)
        return "RESULT_2"
    }
}
