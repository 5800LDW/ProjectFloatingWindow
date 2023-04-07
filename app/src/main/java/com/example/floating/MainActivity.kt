package com.example.floating

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.projectfloatingwindow.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btOpen.setOnClickListener { _ ->

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    openOverlay()
                } else {
                    startFloatService()
                }
            } else {
                startFloatService()
            }

            //activity退到后台
            moveTaskToBack(true)

        }

    }

    private fun startFloatService() {
        val service = Intent()
        service.setClass(this, FloatService::class.java)
        startService(service)
    }

    private fun openOverlay() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


}