package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val fileName = intent.getStringExtra(FILE)
        val status = intent.getStringExtra(STATUS)

        fileNameTextView.text = "File: $fileName"
        statusTextView.text = "Status: $status"

        detailMotionLayout.transitionToEnd()

        backButton.setOnClickListener {
            finish()
        }
    }

}
