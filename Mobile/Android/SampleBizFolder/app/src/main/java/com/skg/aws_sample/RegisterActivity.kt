package com.skg.aws_sample

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignUpResult
import com.bumptech.glide.util.Util
import com.skg.aws_sample.utils.Utils
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toolbar.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        tvToolBar.text = getString(R.string.register)
        toolBar.navigationIcon = getDrawable(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener {
            finish()
        }
    }

    fun onTapRegisterAccount(v: View) {
        val attribute: Map<String, String> =
            mapOf("email" to edtEmail.text.toString(), "phone_number" to "+84387724625")
        if (edtEmail.text!!.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches() && edtPassword.text!!.isNotEmpty() && edtPassword.text!!.length >= 8) {
            AWSMobileClient.getInstance().signUp(
                edtEmail.text.toString(),
                edtPassword.text.toString(),
                attribute,
                null,
                object : Callback<SignUpResult> {
                    override fun onResult(result: SignUpResult?) {
                        Utils.showDialog(
                            "Sign Up success, please check code had sent in your email",
                            supportFragmentManager
                        )
                    }

                    override fun onError(e: Exception?) {
                        Utils.showDialog("${e?.message}", supportFragmentManager)
                    }
                })
        } else {
            Utils.showDialog("Please input full information", supportFragmentManager)

        }
    }

    fun onTapConfirmCode(v: View) {
        if (edtEmail.text!!.isNotEmpty()) {
            AWSMobileClient.getInstance().confirmSignUp(
                edtEmail.text.toString(),
                edtConfirmCode.text.toString(),
                object : Callback<SignUpResult> {
                    override fun onResult(result: SignUpResult?) {
                        Utils.showDialog("Sign Up success", supportFragmentManager)
                        finish()
                    }

                    override fun onError(e: Exception?) {
                        Utils.showDialog("${e?.message}", supportFragmentManager)
                    }
                })
        } else {
            Utils.showDialog("Please input your email", supportFragmentManager)
        }
    }

    fun onTapResendConfirmCode(v: View) {
        if (edtEmail.text!!.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches() ) {
            AWSMobileClient.getInstance().resendSignUp(edtEmail.text.toString(), object : Callback<SignUpResult> {
                override fun onResult(signUpResult: SignUpResult) {
                    Utils.showDialog(
                        "A verification code has been sent via" +
                                signUpResult.userCodeDeliveryDetails.deliveryMedium
                                + " at " +
                                signUpResult.userCodeDeliveryDetails.destination, supportFragmentManager
                    )
                }

                override fun onError(e: Exception) {
                    Utils.showDialog("${e.message}", supportFragmentManager)
                }
            })
        }
    }

}
