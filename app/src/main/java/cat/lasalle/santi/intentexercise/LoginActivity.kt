package cat.lasalle.santi.intentexercise

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    companion object {
        const val EMAIL_EXTRA = "email"
        const val PASSWORD_EXTRA = "password"
    }

    private var fieldsOk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_login.setOnClickListener {
            fieldsOk = true
            checkEmail(login_email)
            checkPassword(login_password)
            if (fieldsOk) {
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_LONG).show()
            }
        }

        login_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtras(getRegisterBundle())
            startActivityForResult(intent, RegisterActivity.REQUEST_CODE)
        }
    }

    private fun checkPassword(editText: EditText) {
        val password = login_password.text.toString()
        if (password.isEmpty() || password.length < 8) {
            editText.error = getString(R.string.login_password_length)
            fieldsOk = false
        }
    }

    private fun checkEmail(editText: EditText) {
        val email = editText.text.toString()
        if (!Pattern.compile(".+\\@.+\\..+").matcher(email).matches()) {
            editText.error = getString(R.string.login_email_invalid)
            fieldsOk = false
        }
    }

    private fun getRegisterBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(EMAIL_EXTRA, login_email.text.toString())
        bundle.putString(PASSWORD_EXTRA, login_password.text.toString())
        return bundle
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RegisterActivity.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) updateViews(data!!)
            }
        }
    }

    private fun updateViews(data: Intent) {
        login_email.setText(data.getStringExtra(EMAIL_EXTRA))
        login_password.setText(data.getStringExtra(PASSWORD_EXTRA))
    }
}
