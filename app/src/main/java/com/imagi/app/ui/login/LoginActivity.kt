package com.imagi.app.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.imagi.app.MainActivity
import com.imagi.app.R
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        try{
            val sharedPreferences : SharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
            if(sharedPreferences.contains("user")){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        }catch (e : Exception){}

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val image = findViewById<ImageView>(R.id.background_login)

        if(loadUser()){
            goToMenuPage()
        }


        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        fun saveUser(@NonNull email : String) {
            val sharedPreferences : SharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor? = sharedPreferences.edit()
            editor?.apply { putString("email", username.text.toString()) }?.apply()

//            val result = sharedPreferences.getString("email", null)
//            Toast.makeText(
//                applicationContext,
//                " user saat ini ${sharedPreferences.contains("email")}",
//                Toast.LENGTH_LONG
//            ).show()
        }

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {

            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                saveUser("${username?.text}")
                updateUiWithUser(loginResult.success)
//                finish()
            }
            setResult(Activity.RESULT_OK)
            //Complete and destroy login activity once successful
            goToMenuPage()


        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                Timber.d("TESTING")
                Timber.i("TESTING")
                print(username.text.toString())

                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun loadUser(): Boolean {
        val sharedPreferences : SharedPreferences =  getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPreferences.contains("email")
    }

    private fun goToMenuPage(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
//        Toast.makeText(
//            applicationContext,
//            "$welcome $displayName",
//            Toast.LENGTH_LONG
//        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}