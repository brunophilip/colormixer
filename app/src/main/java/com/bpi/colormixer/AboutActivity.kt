package com.bpi.colormixer

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val versionTextView: TextView = findViewById(R.id.textViewVersion)
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        versionTextView.text = "Version : $versionName"
    }
}