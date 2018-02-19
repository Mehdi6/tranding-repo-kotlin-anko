package com.example.salah.androidkotlinenkosample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.salah.androidkotlinenkosample.trending_repos.TrendingReposActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, TrendingReposActivity::class.java))
        finish()
    }

}
