package com.example.jobseeker.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.jobseeker.Models.Users
import com.example.jobseeker.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().getReference("Client")

        btnConfirm.setOnClickListener {

            if (newUser()) {

                val intent = Intent(this, UserMainActivity::class.java)

                startActivity(intent)

                finish()

            } else {

                missingInfoDialog()

            }

        }

    }

    private fun newUser(): Boolean {

        var flag = true

        val user = auth.currentUser

        var minSalary = 0
        var maxSalary = 0

        val etFullname: TextView = findViewById(R.id.etName)
        val group: RadioGroup = findViewById(R.id.radioUserStatus)
        val selectedExp: String =
            findViewById<RadioButton>(group.checkedRadioButtonId).text.toString()
        val fieldSpinner: Spinner = findViewById(R.id.fieldSpinner)
        val countrySpinner: Spinner = findViewById(R.id.countrySpinner)
        val stateSpinner: Spinner = findViewById(R.id.stateSpinner)
        val salarySpinner: Spinner = findViewById(R.id.salarySpinner)

        val english: CheckBox = findViewById(R.id.engCB)
        val mandarin: CheckBox = findViewById(R.id.mandarinCB)
        val malay: CheckBox = findViewById(R.id.malayCB)

        var languages = mutableListOf<String>()

        val fullname = etFullname.text.toString()
        val experience = selectedExp
        val field = fieldSpinner.selectedItem.toString()
        val country = countrySpinner.selectedItem.toString()
        val state = stateSpinner.selectedItem.toString()
        val email = user!!.email
        val uid = user!!.uid

        if (english.isChecked) {
            languages.add("English")
        }
        if (mandarin.isChecked) {
            languages.add("Mandarin")
        }
        if (malay.isChecked) {
            languages.add("Malay")
        }

        when (salarySpinner.selectedItemPosition) {
            0 -> {

                minSalary = 1500
                maxSalary = 3000

            }
            1 -> {

                minSalary = 3000
                maxSalary = 6000

            }
            2 -> {

                minSalary = 6000
                maxSalary = 9000

            }
            3 -> {

                minSalary = 9000
                maxSalary = 15000

            }
            4 -> {

                minSalary = 15000
                maxSalary = 50000

            }
        }

        if (fullname == "") {

            flag = false

        }
        if (!english.isChecked && !mandarin.isChecked && !malay.isChecked) {

            flag = false

        }

        val newUser = Users(
            uid = uid,
            fullName = fullname,
            email = email!!,
            country = country,
            state = state,
            field = field,
            expLevel = experience,
            languages = languages,
            minSalary = minSalary,
            maxSalary = maxSalary
        )

        if (flag) {

            database.child(uid).setValue(newUser)

        }

        return flag

    }


    private fun missingInfoDialog() {

        val builder = AlertDialog.Builder(
            this,
            R.style.MyDialogTheme
        )
        builder.apply {
            setTitle("Profile Info")
            setMessage("Please fill up all the info!")
            setPositiveButton(R.string.dialog_ok) { dialog, which ->

            }
        }

        val dialog: AlertDialog = builder.show()
        val positiveButton: Button =
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(Color.WHITE)
        dialog.show()

    }

    override fun onBackPressed() {

        var googleSignInClient: GoogleSignInClient

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInClient.signOut()

        val auth = FirebaseAuth.getInstance()

        auth.signOut()

        super.onBackPressed()

    }


}