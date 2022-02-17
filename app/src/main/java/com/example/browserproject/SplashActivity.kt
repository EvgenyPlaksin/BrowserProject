package com.example.browserproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var progressStatus = 0
        var handler = Handler()


            Thread(Runnable {
                if(progressStatus >= 100){
                    val randomIntent = Intent(this, MainActivity::class.java)
                    startActivity(randomIntent)
                }
                while (progressStatus <= 100) {
                    // update progress status
                    progressStatus += 1
                    if(progressStatus == 100){
                        val randomIntent = Intent(this, MainActivity::class.java)
                        startActivity(randomIntent)
                    }
                    // sleep the thread for 100 milliseconds
                    Thread.sleep(30)

                    // update the progress bar
                    handler.post {
                        progressBar.progress = progressStatus
                        textView.text = "$progressStatus"
                    }
                }
            }).start()

        }

    }