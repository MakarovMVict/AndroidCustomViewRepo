package com.example.customviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customviewlib.ui.CustomAvatarImageView

class MainActivity : AppCompatActivity() {

//    private lateinit var container:LinearLayout
//    private val imgViewId = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        container = findViewById(R.id.container)
//
//        val view = CustomAvatarImageView(this).apply {
//            id = imgViewId
//            layoutParams = ConstraintLayout.LayoutParams(120,120)
//            setImageResource(com.example.customviewlib.R.drawable.homm_vivern)
//
//        }
//        container.addView(view)
    }
}