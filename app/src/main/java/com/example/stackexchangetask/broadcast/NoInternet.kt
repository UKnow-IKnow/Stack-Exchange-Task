package com.example.stackexchangetask.broadcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.view.Window
import com.example.stackexchangetask.R

class NoInternet : AppCompatActivity() {

    companion object {
        val TAG = NoInternet::class.simpleName
    }
    var isInternet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Explode()
            exitTransition = Explode()
        }
        setContentView(R.layout.activity_no_internet)
        NetworkError(this@NoInternet).observe(this) {
            isInternet = if (!it) {
                // No internet:
                false
            } else {
                // When internet is connected
                finish()
                true
            }
        }
    }
}