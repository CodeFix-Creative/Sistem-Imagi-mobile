package com.imagi.app.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.imagi.app.MainActivity
import com.imagi.app.R
import com.imagi.app.data.LoginDataSource
import com.imagi.app.data.LoginRepository
import com.imagi.app.model.UserLogin
import com.imagi.app.model.UserResponse
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

//    private lateinit var loginViewModel: LoginViewModel

    val loginRepository: LoginRepository = LoginRepository(LoginDataSource())

    val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    var currentUser : UserResponse? = null

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

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
            if(sharedPreferences.contains("authorization")){
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

//
//        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
//                .get(LoginViewModel::class.java)

        loginFormState.observe(this@LoginActivity, Observer {
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


        loginResult.observe(this@LoginActivity, Observer {

            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                goToMenuPage()
//                finish()
            }
            setResult(Activity.RESULT_OK)
            //Complete and destroy login activity once successful


        })

        username.afterTextChanged {
            loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginUser(
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

                loginUser(username.text.toString(), password.text.toString())
            }
        }
    }

    fun loginUser(username: String, password: String){
        // can be launched in a separate asynchronous job
        val user = UserLogin(username, password)
        Log.d("tes", "UWU 01")
        loginRepository.login(user) {
            if (it?.code == 200) {
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = username))
                saveUser(it)
            } else {
                _loginResult.value = LoginResult(error = it?.message.toString())
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {

            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            return false
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun saveUser(@NonNull user : UserResponse) {

//        Log.d("USER_RESPONSE" , user.token)
//        Log.d("USER_RESPONSE" , user.data.alamat)
        val sharedPreferences : SharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor? = sharedPreferences.edit()
        Log.d("USER_DATA", Gson().toJson(user.data))
        try{
            editor?.apply {
                putString("currentUser", Gson().toJson(user.data))
                putString("authorization", user.token)
            }?.apply()
        }catch (e: Exception){
            Log.d("EXCEPTION", "${e.message}")
        }

//        Log.d("USER_LOCAL" , sharedPreferences.contains("authorization").toString())

    }

    private fun loadUser(): Boolean {
        val sharedPreferences : SharedPreferences =  getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPreferences.contains("user")
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

//    private fun showLoginFailed(@StringRes errorString: Int) {
//        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
//    }

    private fun showLoginFailed( errorString: String) {
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