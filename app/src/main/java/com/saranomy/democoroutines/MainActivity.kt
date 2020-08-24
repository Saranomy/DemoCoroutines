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
            // On Main/UI thread
            result.text = "Starting..."
            threadName.text = Thread.currentThread().name

            // Set the initial scope to IO to handle network or database work in the background
            CoroutineScope(Dispatchers.IO).launch {
                val nameOfCurrentThread = Thread.currentThread().name

                delay(1000) // Note: Thread.sleep() is for threads, use delay for coroutines

                loadSomethingFromTheInternet()

                updateUI("Processing...")

                // Use Default to do something CPU intensive
                withContext(Dispatchers.Default) {
                    crunchingNumbers()
                }

                // Finally, go back to Main/UI scope to change Views
                updateUI("Done.")

                // Final note: threadName should show that they are almost operating on the same thread, and use different workers within the thread.
            }
        }
    }

    // Use suspend for coroutine function
    private suspend fun updateUI(resultText: String) {
        // Within the same scope started by line 27, we can switch between scopes using withContext
        withContext(Dispatchers.Main) {
            result.text = resultText
            threadName.append("\n${Thread.currentThread().name}")
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