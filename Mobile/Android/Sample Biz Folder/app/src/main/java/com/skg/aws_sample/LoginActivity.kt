package com.skg.aws_sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.*
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState
import com.skg.aws_sample.net_work.NetworkClient
import com.skg.aws_sample.net_work.onSuccess
import com.skg.aws_sample.utils.Utils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tvToolBar.text = getString(R.string.login)
    }

    fun onTapRegister(v: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun onTapLoginEmail(v: View) {
        progressBar.visibility = View.VISIBLE
        if (edtEmail.text!!.isNotEmpty() && edtPassword.text!!.isNotEmpty()) {
            AWSMobileClient.getInstance()
                .signIn(edtEmail.text.toString(), edtPassword.text.toString(), null, object : Callback<SignInResult> {
                    override fun onResult(result: SignInResult?) {
                        when (result?.signInState) {
                            SignInState.DONE -> {
                                AWSMobileClient.getInstance().getUserAttributes(object : Callback<Map<String, String>> {
                                    override fun onResult(result: Map<String, String>) {
                                        this@LoginActivity.runOnUiThread {

                                            val sharedPreferences = getSharedPreferences(getString(R.string.save_info), Context.MODE_PRIVATE)
                                            with(sharedPreferences.edit()) {
                                                this.putString(getString(R.string.email), result["email"])
                                                this.putString(getString(R.string.sub), result["sub"])
                                                this.apply()
                                            }

                                            registerToken(result["email"], result["sub"])
                                            startActivity(Intent(this@LoginActivity, HomePage::class.java))
                                            finish()
                                        }
                                    }

                                    override fun onError(e: Exception?) {
                                        this@LoginActivity.runOnUiThread {
                                            Toast.makeText(this@LoginActivity, e?.message, Toast.LENGTH_LONG).show()
                                            progressBar.visibility = View.GONE
                                        }
                                    }

                                })
                            }
                            else -> print(result?.signInState)
                        }
                    }

                    override fun onError(e: Exception?) {
                        this@LoginActivity.runOnUiThread {
                            Toast.makeText(this@LoginActivity, e?.message, Toast.LENGTH_LONG).show()
                            progressBar.visibility = View.GONE
                        }
                    }
                })


        } else {
            Utils.showDialog("Please input full information", supportFragmentManager)
        }
    }

    fun onTapLoginFacebook(v: View) {
        progressBar.visibility = View.VISIBLE

        val hostedUi = HostedUIOptions.builder()
            .scopes(
                "phone",
                "email",
                "openid",
                "profile",
                "aws.cognito.signin.user.admin"
            )
            .identityProvider("Facebook")
            .build()

        val signInUiOptions = SignInUIOptions.builder()
            .hostedUIOptions(hostedUi)
            .build()

        AWSMobileClient.getInstance().showSignIn(this, signInUiOptions, object : Callback<UserStateDetails> {
            override fun onResult(result: UserStateDetails?) {
                when (result?.userState) {
                    UserState.SIGNED_IN -> {
                        registerToken(
                            AWSMobileClient.getInstance().tokens.idToken.getClaim("email"),
                            AWSMobileClient.getInstance().tokens.idToken.getClaim("sub")
                        )
                        this@LoginActivity.runOnUiThread {
                            startActivity(Intent(this@LoginActivity, HomePage::class.java))
                            finish()
                        }
                    }
                    else -> this@LoginActivity.runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Please handle more", Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE
                    }
                }
            }

            override fun onError(e: Exception?) {
                this@LoginActivity.runOnUiThread {
                    Toast.makeText(this@LoginActivity, e?.message, Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    fun onTapLoginGoogle(v: View) {
        progressBar.visibility = View.VISIBLE

        val hostedUiOptions = HostedUIOptions.builder()
            .scopes(
                "phone",
                "email",
                "openid",
                "profile",
                "aws.cognito.signin.user.admin"
            )
            .identityProvider("Google")
            .build()

        val signInUiOptions = SignInUIOptions.builder()
            .hostedUIOptions(hostedUiOptions)
            .build()

        AWSMobileClient.getInstance().showSignIn(this, signInUiOptions, object : Callback<UserStateDetails> {
            override fun onResult(result: UserStateDetails?) {
                when (result?.userState) {
                    UserState.SIGNED_IN -> {
                        registerToken(
                            AWSMobileClient.getInstance().tokens.idToken.getClaim("email"),
                            AWSMobileClient.getInstance().tokens.idToken.getClaim("sub")
                        )
                        this@LoginActivity.runOnUiThread {
                            startActivity(Intent(this@LoginActivity, HomePage::class.java))
                            finish()
                        }
                    }

                    else -> this@LoginActivity.runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Please handle more", Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE
                    }
                }
            }

            override fun onError(e: Exception?) {
                this@LoginActivity.runOnUiThread {
                    Toast.makeText(this@LoginActivity, e?.message, Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    fun registerToken(email: String?, id: String?) {
        val sharePref = getSharedPreferences(getString(R.string.token_devices), Context.MODE_PRIVATE) ?: return
        val token = sharePref.getString(getString(R.string.save_token), "") ?: return
        val map1 = mapOf(
            "id" to id,
            "email" to email
        )
        val map2 = mapOf(
            "token" to token,
            "platform" to "FCM",
            "userInfo" to map1
        )

        NetworkClient.instance.service.registerToken(
            map2
        ).onSuccess {
            //save token registered
            val sharePreferenceEndPoint = getSharedPreferences(getString(R.string.endpoint_arn), Context.MODE_PRIVATE)
            val endpointArn = sharePreferenceEndPoint.getString(getString(R.string.endpoint_arn), "")
            val sharePreferenceSubscription =
                getSharedPreferences(getString(R.string.subscription_arn), Context.MODE_PRIVATE)
            val subscriptionArn = sharePreferenceSubscription.getString(getString(R.string.subscription_arn), "")

            if (endpointArn!!.isNotEmpty() && subscriptionArn!!.isNotEmpty()) {
                if (endpointArn != it?.endpointArn) {
                    with(sharePreferenceEndPoint.edit()) {
                        this.putString(getString(R.string.endpoint_arn), it?.endpointArn)
                        apply()
                    }
                } else {
                }
                if (subscriptionArn != it?.subscriptionArn) {
                    with(sharePreferenceSubscription.edit()) {
                        this.putString(getString(R.string.subscription_arn), it?.subscriptionArn)
                        apply()
                    }
                }
            }
            print(it)

        }
    }

    override fun onResume() {
        super.onResume()
        val activityIntent = intent
        if (activityIntent.data != null && "myapp" == activityIntent.data!!.scheme) {
            AWSMobileClient.getInstance().handleAuthResponse(activityIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressBar.visibility = View.GONE
    }
}
