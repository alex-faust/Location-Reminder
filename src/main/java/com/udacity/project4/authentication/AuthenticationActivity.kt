package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)


        authenticationState.observe(this) {

            when (it) {
                AuthenticationState.AUTHENTICATED ->
                    startActivity(Intent(this, RemindersActivity::class.java))
                    else ->
                        Log.e(TAG, "Authentication state that doesn't require any UI change " +
                                "$authenticationState")

            }

        }
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            launchSignInFlow()
            //registerForSignInResult()
        }


        // TODO: Implement the create account and sign in using FirebaseUI,
        //  use sign in using email and sign in using Google

        // TODO: If the user was authenticated, send him to RemindersActivity

        // TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }

    private fun launchSignInFlow()  {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val layout = AuthMethodPickerLayout.Builder(R.layout.auth_layout)
            .setGoogleButtonId(R.id.googleButton)
            .setEmailButtonId(R.id.emailButton)
            .build()

        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAuthMethodPickerLayout(layout)
            .setAvailableProviders(providers)
            .build(), SIGN_IN_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //registerForActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //val response = IdpResponse.fromResultIntent(resultCode)

            //Listen to the result of the sign in process
            if (requestCode == Activity.RESULT_OK) {
                Log.i(
                    TAG,
                    "User ${FirebaseAuth.getInstance().currentUser?.displayName} has signed in."
                )
                //startActivity(Intent(this, RemindersActivity::class.java))

            } else {
                Log.i(TAG, "Sign in unsuccessful: $resultCode")
            }
        //}
    }



    companion object {
        const val TAG = "Auth"
        const val SIGN_IN_RESULT_CODE = 1001
    }
}