package com.example.customviewapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.customviewapp.databinding.ActivityMainBinding
import com.example.customviewlib.ui.CustomAvatarImageView
import com.example.customviewlib.ui.CustomIosShimmerLoaderView


class MainActivity : AppCompatActivity() {

    private lateinit var loaderShimmer: CustomIosShimmerLoaderView
    private lateinit var customAvatarImageView: CustomAvatarImageView

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

//    private val imgViewId = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding = DataBindingUtil
            .setContentView<ActivityMainBinding?>(this, R.layout.activity_main)
            .also { it.mainViewModel = viewModel }

        loaderShimmer = binding.shimmerLoader
        customAvatarImageView = binding.aiv


//        loaderShimmer.setShimmerVisibility(true)
//        customAvatarImageView.visibility = View.GONE


//
//        val view = CustomAvatarImageView(this).apply {
//            id = imgViewId
//            layoutParams = ConstraintLayout.LayoutParams(120,120)
//            setImageResource(com.example.customviewlib.R.drawable.homm_vivern)
//
//        }
//        container.addView(view)
    }

    override fun onResume() {
        super.onResume()

    }
}