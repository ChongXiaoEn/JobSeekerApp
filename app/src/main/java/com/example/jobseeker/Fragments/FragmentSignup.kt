package com.example.jobseeker.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.jobseeker.Models.Users
import com.example.jobseeker.R
import com.example.jobseeker.Activities.UserMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_signup.*


class FragmentSignup : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_signup, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().getReference("Client")

        btnConfirm.setOnClickListener {

            if (newUser(view)) {

                val intent = Intent(mContext, UserMainActivity::class.java)

                startActivity(intent)

            } else {

                showDialog()

            }

        }


    }

    private fun newUser(view: View): Boolean {

        var flag = true

        val user = auth.currentUser

        var minSalary = 0
        var maxSalary = 0

        val etFullname: TextView = view.findViewById(R.id.etName)
        val group: RadioGroup = view.findViewById(R.id.radioUserStatus)
        val selectedExp: String =
            view.findViewById<RadioButton>(group.checkedRadioButtonId).text.toString()
        val fieldSpinner: Spinner = view.findViewById(R.id.fieldSpinner)
        val countrySpinner: Spinner = view.findViewById(R.id.countrySpinner)
        val stateSpinner: Spinner = view.findViewById(R.id.stateSpinner)
        val salarySpinner: Spinner = view.findViewById(R.id.salarySpinner)

        val english: CheckBox = view.findViewById(R.id.engCB)
        val mandarin: CheckBox = view.findViewById(R.id.mandarinCB)
        val malay: CheckBox = view.findViewById(R.id.malayCB)

        var languages = mutableListOf<String>()

        val fullname = etFullname.text.toString()
        val experience = selectedExp
        val field = fieldSpinner.selectedItem.toString()
        val country = countrySpinner.selectedItem.toString()
        val state = stateSpinner.selectedItem.toString()
        val email = user!!.email
        val uid = user.uid

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

    override fun onAttach(context: Context) {

        mContext = context

        super.onAttach(context)
    }

    private fun showDialog() {

        val builder = AlertDialog.Builder(
            context!!,
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

}