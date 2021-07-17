package com.imagi.app.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.gson.Gson
import com.imagi.app.MainActivity
import com.imagi.app.MenuMerchant
import com.imagi.app.R
import com.imagi.app.model.UserLogin
import com.imagi.app.model.UserResponse
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.util.AppUtils
import com.imagi.app.util.Constant
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), HasSupportFragmentInjector {

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        dbServices = DbServices(this)
        dbServices.mContext = this
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        viewModel.isShowLoader.value = false
        username.setText("pedagang@gmail.com")
        password.setText("Pedagang123")

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        try{
            val sharedPreferences : SharedPreferences = getSharedPreferences(
                Constant.SP_TOKEN_USER,
                Context.MODE_PRIVATE
            )
            if(sharedPreferences.contains(Constant.SP_TOKEN) ){
                if(dbServices.user.role == "Pedagang"){
                    goToMenuPageMerchant()
                }else {
                    goToMenuPage()
                }
            }
        }catch (e: Exception){}


        btn_login.setOnClickListener {
//            loading.visibility = View.VISIBLE
            Log.d("TESTING", "s")
            Timber.i("TESTING")
            print(username.text.toString())

            if(validationForm(this, username.text.toString(), password.text.toString())){
                var data : UserLogin = UserLogin(username.text.toString(), password.text.toString())
                viewModel.postLogin(data)
            }
        }

        loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
//            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })


        loginResult.observe(this@LoginActivity, Observer {

            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
//            if (loginResult.success != null) {
//                goToMenuPage()
////                finish()
//            }
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
        }

        observeViewModel()
    }

    private fun validationForm(ctx: Context, email: String, password: String): Boolean {
        Log.d("Check user", "UWU")
        return if(TextUtils.isEmpty(email)){
            AppUtils.showAlert(ctx, "Mohon mengisi email anda")
            false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            AppUtils.showAlert(ctx, "Mohon mengisi dengan email yang valid")
            false
        }else if(TextUtils.isEmpty(password)){
            AppUtils.showAlert(ctx, "Mohon mengisi password anda")
            false
        }else{
            Log.d("Check user", "VALID")
            true
        }
    }

    private fun loginDataChanged(username: String, password: String) {
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

    private fun goToMenuPage(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun goToMenuPageMerchant(){
        val intent = Intent(this, MenuMerchant::class.java)
        startActivity(intent)
    }

    private fun observeViewModel(){
        viewModel.token.observe(this, {
            getSharedPreferences(Constant.SP_TOKEN_USER, MODE_PRIVATE).edit()
                .putString(Constant.SP_TOKEN, it)
                .apply()

            viewModel.userLiveData.observe(this, {
                getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE).edit()
                    .putString(Constant.SP_USER, Gson().toJson(it))
                    .apply()
                Log.d("DATA_USER", "${Gson().toJson(it)}")
            })
            if(dbServices.user.role == "Pedagang"){
                goToMenuPageMerchant()
            }else {
                goToMenuPage()
            }
            finish()
        })

        viewModel.errorMessage.observe(this, {
//            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            AppUtils.showAlert(this, it)
        })

        viewModel.isShowLoader.observe(this, {
            if (it) {
                loading?.visibility = View.VISIBLE
            } else {
                loading?.visibility = View.GONE
            }
        })
    }

//    private fun showLoginFailed(@StringRes errorString: Int) {
//        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
//    }

    private fun showLoginFailed(errorString: String) {
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