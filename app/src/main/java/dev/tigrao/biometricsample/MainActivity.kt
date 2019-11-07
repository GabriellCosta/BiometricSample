package dev.tigrao.biometricsample

import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private val authButton: Button by lazy { findViewById<Button>(R.id.btn_auth) }
    private val authButtonCustom: Button by lazy { findViewById<Button>(R.id.btn_auth_custom) }
    private val mainViewModel by lazy { MainViewModel(applicationContext as Application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareObserver()
    }

    private fun prepareObserver() {
        mainViewModel.viewState.observe(this, Observer {
            authButton.isEnabled = it.buttonEnabled
            authButtonCustom.isEnabled = it.buttonEnabled

            authButton.setOnClickListener(it.clickAction)
        })

        mainViewModel.resultState.observe(this, Observer {
            toastMessage(it)
        })
    }

    override fun onResume() {
        super.onResume()

        mainViewModel.fetchButton()
    }

    private fun toastMessage(message: String) {
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                message, Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}
