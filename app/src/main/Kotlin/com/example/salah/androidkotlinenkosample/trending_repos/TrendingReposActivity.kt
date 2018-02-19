package com.example.salah.androidkotlinenkosample.trending_repos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.setContentView

class TrendingReposActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TrendingReposView().setContentView(this)
    }
}
