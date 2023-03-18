package com.example.recorder.ui

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.recorder.R

class AuthActivity: AppCompatActivity(R.layout.auth_main) {

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to leave the app?")
            .setCancelable(false)
            .setPositiveButton("Yes"
            ) { _, _ -> finish() }
            .setNegativeButton("No"
            ) { dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}