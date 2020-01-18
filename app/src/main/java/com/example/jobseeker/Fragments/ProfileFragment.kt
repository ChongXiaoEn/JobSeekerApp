package com.example.jobseeker.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.jobseeker.Models.Users
import com.example.jobseeker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    private var mContext: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().getReference("Client")

        setUserInfo()


    }

    private fun setUserInfo() {

        val userImage: ImageView = view!!.findViewById(R.id.userImg)
        val name: TextView = view!!.findViewById(R.id.profileName)
        val email: TextView = view!!.findViewById(R.id.txtEmail)
        val phoneNo: TextView = view!!.findViewById(R.id.txtPhone)

        val expLvl: Spinner = view!!.findViewById(R.id.editExpLvl)
        val expSalary: Spinner = view!!.findViewById(R.id.editSalary)
        val country: Spinner = view!!.findViewById(R.id.editCountry)
        val state: Spinner = view!!.findViewById(R.id.editState)
        val field: Spinner = view!!.findViewById(R.id.editField)

        val engCheck: CheckBox = view!!.findViewById(R.id.engCheck)
        val mandarinCheck: CheckBox = view!!.findViewById(R.id.mandarinCheck)
        val malayCheck: CheckBox = view!!.findViewById(R.id.malayCheck)

        val user = auth.currentUser
        val imageUri = user!!.photoUrl

        email.text = user.email
        Picasso.get().load(imageUri).into(userImage)

        val userListener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(ds: DataSnapshot) {

                for (ds in ds.children) {

                    val uid = ds.child("uid").value.toString()

                    if (uid == user.uid) {

                        phoneNo.text = ds.child("phoneNo").value.toString()
                        name.text = ds.child("fullName").value.toString()
                        expLvl.setSelection(getIndex(expLvl, ds.child("expLevel").value.toString()))

                        var minSalary = ds.child("minSalary").value.toString().toInt()

                        when (minSalary) {

                            1500 -> expSalary.setSelection(0)
                            3000 -> expSalary.setSelection(1)
                            6000 -> expSalary.setSelection(2)
                            9000 -> expSalary.setSelection(3)
                            15000 -> expSalary.setSelection(4)

                        }
                        country.setSelection(
                            getIndex(
                                country,
                                ds.child("country").value.toString()
                            )
                        )
                        state.setSelection(getIndex(state, ds.child("state").value.toString()))
                        field.setSelection(getIndex(field, ds.child("field").value.toString()))

                        val children = ds.child("languages").children

                        children.forEach {

                            when {
                                "English" == it.value.toString() -> {

                                    engCheck.isChecked = true

                                }
                                "Mandarin" == it.value.toString() -> {

                                    mandarinCheck.isChecked = true

                                }
                                "Malay" == it.value.toString() -> {

                                    malayCheck.isChecked = true

                                }
                            }

                        }

                        break

                    }
                }

            }


        }

        database.addValueEventListener(userListener)

        expLvl.isEnabled = false
        expSalary.isEnabled = false
        country.isEnabled = false
        state.isEnabled = false
        field.isEnabled = false

        engCheck.isEnabled = false
        mandarinCheck.isEnabled = false
        malayCheck.isEnabled = false


    }

    fun saveInfo() {

        val user = auth.currentUser
        val uid = user!!.uid

        var minSalary = 0
        var maxSalary = 0

        val expLvlSpinner: Spinner = view!!.findViewById(R.id.editExpLvl)
        val salarySpinner: Spinner = view!!.findViewById(R.id.editSalary)
        val countrySpinner: Spinner = view!!.findViewById(R.id.editCountry)
        val stateSpinner: Spinner = view!!.findViewById(R.id.editState)
        val fieldSpinner: Spinner = view!!.findViewById(R.id.editField)

        val name: String = view!!.findViewById<EditText>(R.id.editName).text.toString()
        val email: String = view!!.findViewById<EditText>(R.id.editEmail).text.toString()
        val phoneNo: String = view!!.findViewById<EditText>(R.id.editPhoneNo).text.toString()

        val expLvl: String = expLvlSpinner.selectedItem.toString()
        val country: String = countrySpinner.selectedItem.toString()
        val state: String = stateSpinner.selectedItem.toString()
        val field: String = fieldSpinner.selectedItem.toString()


        val engCheck: CheckBox = view!!.findViewById(R.id.engCheck)
        val mandarinCheck: CheckBox = view!!.findViewById(R.id.mandarinCheck)
        val malayCheck: CheckBox = view!!.findViewById(R.id.malayCheck)

        var languages = mutableListOf<String>()

        if (engCheck.isChecked) {
            languages.add("English")
        }
        if (mandarinCheck.isChecked) {
            languages.add("Mandarin")
        }
        if (malayCheck.isChecked) {
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

        val updatedUser = Users(
            uid = uid,
            fullName = name,
            email = email,
            phoneNo = phoneNo,
            expLevel = expLvl,
            country = country,
            state = state,
            field = field,
            languages = languages,
            minSalary = minSalary,
            maxSalary = maxSalary
        )

        database.child(uid).setValue(updatedUser)

        disableInfoEdit()

    }

    fun enableInfoEdit() {

        val name: TextView = view!!.findViewById(R.id.profileName)
        val email: TextView = view!!.findViewById(R.id.txtEmail)
        val phoneNo: TextView = view!!.findViewById(R.id.txtPhone)

        val editName: EditText = view!!.findViewById(R.id.editName)
        val editEmail: EditText = view!!.findViewById(R.id.editEmail)
        val editPhoneNo: EditText = view!!.findViewById(R.id.editPhoneNo)

        val expLvl: Spinner = view!!.findViewById(R.id.editExpLvl)
        val expSalary: Spinner = view!!.findViewById(R.id.editSalary)
        val country: Spinner = view!!.findViewById(R.id.editCountry)
        val state: Spinner = view!!.findViewById(R.id.editState)
        val field: Spinner = view!!.findViewById(R.id.editField)

        val engCheck: CheckBox = view!!.findViewById(R.id.engCheck)
        val mandarinCheck: CheckBox = view!!.findViewById(R.id.mandarinCheck)
        val malayCheck: CheckBox = view!!.findViewById(R.id.malayCheck)

        name.visibility = View.INVISIBLE
        email.visibility = View.INVISIBLE
        phoneNo.visibility = View.INVISIBLE

        editName.visibility = View.VISIBLE
        editEmail.visibility = View.VISIBLE
        editPhoneNo.visibility = View.VISIBLE

        expLvl.isEnabled = true
        expSalary.isEnabled = true
        country.isEnabled = true
        state.isEnabled = true
        field.isEnabled = true

        engCheck.isEnabled = true
        mandarinCheck.isEnabled = true
        malayCheck.isEnabled = true

        editName.setText(name.text)
        editEmail.setText(email.text)
        editPhoneNo.setText(phoneNo.text)

    }

    private fun disableInfoEdit() {

        val name: TextView = view!!.findViewById(R.id.profileName)
        val email: TextView = view!!.findViewById(R.id.txtEmail)
        val phoneNo: TextView = view!!.findViewById(R.id.txtPhone)

        val editName: EditText = view!!.findViewById(R.id.editName)
        val editEmail: EditText = view!!.findViewById(R.id.editEmail)
        val editPhoneNo: EditText = view!!.findViewById(R.id.editPhoneNo)

        val expLvl: Spinner = view!!.findViewById(R.id.editExpLvl)
        val expSalary: Spinner = view!!.findViewById(R.id.editSalary)
        val country: Spinner = view!!.findViewById(R.id.editCountry)
        val state: Spinner = view!!.findViewById(R.id.editState)
        val field: Spinner = view!!.findViewById(R.id.editField)

        val engCheck: CheckBox = view!!.findViewById(R.id.engCheck)
        val mandarinCheck: CheckBox = view!!.findViewById(R.id.mandarinCheck)
        val malayCheck: CheckBox = view!!.findViewById(R.id.malayCheck)

        name.visibility = View.VISIBLE
        email.visibility = View.VISIBLE
        phoneNo.visibility = View.VISIBLE

        editName.visibility = View.INVISIBLE
        editEmail.visibility = View.INVISIBLE
        editPhoneNo.visibility = View.INVISIBLE

        expLvl.isEnabled = false
        expSalary.isEnabled = false
        country.isEnabled = false
        state.isEnabled = false
        field.isEnabled = false

        engCheck.isEnabled = false
        mandarinCheck.isEnabled = false
        malayCheck.isEnabled = false

        name.text = editName.text
        email.text = editEmail.text
        phoneNo.text = editPhoneNo.text

    }

    private fun getIndex(spinner: Spinner, value: String): Int {

        for (i in 0..spinner.count) {

            if (spinner.getItemAtPosition(i).toString().compareTo(value) == 0) {

                return i

            }

        }

        return 0

    }

    override fun onAttach(context: Context) {

        mContext = context

        super.onAttach(context)
    }

}