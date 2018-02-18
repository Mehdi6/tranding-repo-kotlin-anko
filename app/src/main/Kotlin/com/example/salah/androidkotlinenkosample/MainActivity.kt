package com.example.salah.androidkotlinenkosample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.salah.androidkotlinenkosample.sign_in.SignInActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

}
