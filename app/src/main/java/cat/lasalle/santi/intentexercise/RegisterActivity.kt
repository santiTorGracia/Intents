package cat.lasalle.santi.intentexercise

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_register.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 1234
    }

    private var fieldsOk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        addGenderButtons()
        loadFields(intent.extras!!)

        setInitialDate()

        register_birth_date.setOnClickListener {
            register_birth_date.error = null
            val curDate = getCurrentDate()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                setBirthDate(dayOfMonth, month + 1, year)
            }, curDate.year - 18, curDate.monthValue - 1, curDate.dayOfMonth)
                .show()
        }

        register_register.setOnClickListener {
            fieldsOk = true
            checkFields()
            if (fieldsOk) {
                val intent = Intent()
                intent.putExtras(getLoginBundle())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setInitialDate() {
        val date = getCurrentDate()
        setBirthDate(date.dayOfMonth, date.monthValue, date.year)
    }

    private fun setBirthDate(dayOfMonth: Int, month: Int, year: Int) {
        val dayOfMonthAsString: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val monthAsString: String = if (month < 10) "0$month" else month.toString()
        register_birth_date.text =
                getString(R.string.register_birth_date_format, dayOfMonthAsString, monthAsString, year)
    }

    private fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }

    private fun getLoginBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(LoginActivity.EMAIL_EXTRA, register_email.text.toString())
        bundle.putString(LoginActivity.PASSWORD_EXTRA, register_password.text.toString())
        return bundle
    }

    private fun checkFields() {
        checkEmail()
        checkPassword()
        checkRepeatPassword()
        checkGender()
        checkBirthDate()
    }

    private fun checkBirthDate() {
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(register_birth_date.text.toString(), format)
        if (getCurrentDate().minusYears(18).isBefore(date)) {
            fieldsOk = false
            register_birth_date.error = "You must be over 18 to use the app."
        }
    }

    private fun checkGender() {
        if (register_gender.checkedRadioButtonId == -1) {
            register_gender_text.error = getString(R.string.register_gender_not_selected)
            register_gender_text.requestFocus()
            fieldsOk = false
        }
    }

    private fun checkRepeatPassword() {
        if (register_password_repeat.text.toString() != register_password.text.toString()) {
            register_password_repeat.error = getString(R.string.register_repeat_password_error)
            fieldsOk = false
        }
    }

    private fun checkPassword() {
        val password = register_password.text.toString()
        if (password.isEmpty() || password.length < 8) {
            register_password.error = getString(R.string.login_password_length)
            fieldsOk = false
        }
    }

    private fun checkEmail() {
        val email = register_email.text.toString()
        if (!Pattern.compile(".+\\@.+\\..+").matcher(email).matches()) {
            register_email.error = getString(R.string.login_email_invalid)
            fieldsOk = false
        }
    }

    private fun loadFields(extras: Bundle) {
        register_email.setText(extras.getString(LoginActivity.EMAIL_EXTRA))
        register_password.setText(extras.getString(LoginActivity.PASSWORD_EXTRA))
    }

    private fun addGenderButtons() {
        val buttons = Array(Gender.values().size) {
            val button = RadioButton(this)
            button.text = Gender.values()[it].name
            button.setOnClickListener { register_gender_text.error = null }
            button
        }
        for (button in buttons) {
            register_gender.addView(button)
        }
    }
}
