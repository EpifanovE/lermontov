package ru.eecode.lermontov.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.lermontov.R
import ru.eecode.lermontov.domain.StartActivityViewModel

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    private val viewModel: StartActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val intent = Intent(this, MainActivity::class.java)
        viewModel.isReady.observe(this, {
            if (it) {
                startActivity(intent)
            }
        })
    }
}