package com.saranomy.democoroutines

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var result: TextView
    private lateinit var threadName: TextView
    private lateinit var run: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result = findViewById(R.id.result)
        threadName = findViewById(R.id.thread_name)
        run = findViewById(R.id.run)

        run.setOnClickListener {
            // on Main thread
            result.text = "Starting..."
            threadName.text = Thread.currentThread().name

            CoroutineScope(Dispatchers.IO).launch {
                // doing IO things on the background
                var nameOfCurrentThread = Thread.currentThread().name
                delay(1000) // Note: Thread.sleep() is for threads, not routines
                loadSomethingFromTheInternet()

                updateUI("Processing...", nameOfCurrentThread)

                // Use Dispatchers.Default to do something CPU heavy
                withContext(Dispatchers.Default) {
                    crunchingNumbers()
                }

                // Finally go back to Main/UI scope to change Views
                updateUI("Done.")

                // Final note: threadName should show that they are almost operating on the same thread.
            }
        }
    }

    // use suspend for coroutine function
    private suspend fun updateUI(
        resultText: String,
        nameOfCurrentThread: String = Thread.currentThread().name
    ) {
        // Within the same scope, we can switch scope using withContext
        withContext(Dispatchers.Main) {
            result.text = resultText
            threadName.text = "${threadName.text}\n${nameOfCurrentThread}"
        }
    }

    private suspend fun loadSomethingFromTheInternet() {
        // This could be multiple API calls
        for (i in 1..10) {
            updateUI("Downloading...$i/10")

            delay(250)
        }
    }

    private suspend fun crunchingNumbers() {
        delay(1500)
    }
}