package com.example.jobseeker.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobseeker.Adapters.JobsAdapter
import com.example.jobseeker.Models.Job
import com.example.jobseeker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
//import com.example.jobseeker.Database.Supplier
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var mContext: Context? = null

    private lateinit var database: DatabaseReference

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        database = FirebaseDatabase.getInstance().reference

        auth = FirebaseAuth.getInstance()

        val layoutManager = LinearLayoutManager(mContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        jobRecycler.layoutManager = layoutManager

        getFilteredJobs()


    }

    private fun getFilteredJobs(): List<Job> {

        val user = auth.currentUser

        var filteredJobs = mutableListOf<Job>()

        var minSalary: Int
        var maxSalary: Int
        var field: String

        val jobListener = object : ValueEventListener {

            override fun onDataChange(ds: DataSnapshot) {

                minSalary = getMinSalaryRange(ds, user!!)
                maxSalary = getMaxSalaryRange(ds, user)
                field = getField(ds, user)
                filteredJobs = filterBySalary(ds, minSalary, maxSalary, field)

                val adapter =
                    JobsAdapter(
                        mContext!!,
                        filteredJobs
                    )

                jobRecycler.adapter = adapter

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        }

        database.addListenerForSingleValueEvent(jobListener)

        return filteredJobs

    }

    private fun getMinSalaryRange(ds: DataSnapshot, currentUser: FirebaseUser): Int {

        var minSalary = 0

        for (ds in ds.child("Client").children) {

            val uid = ds.child("uid").value.toString()

            if (uid == currentUser.uid) {

                minSalary = ds.child("minSalary").value.toString().toInt()

                break

            }

        }

        return minSalary

    }

    private fun getMaxSalaryRange(ds: DataSnapshot, currentUser: FirebaseUser): Int {

        var maxSalary = 0

        for (ds in ds.child("Client").children) {

            val uid = ds.child("uid").value.toString()

            if (uid == currentUser.uid) {

                maxSalary = ds.child("maxSalary").value.toString().toInt()

                break

            }

        }

        return maxSalary

    }

    private fun getField(ds: DataSnapshot, currentUser: FirebaseUser): String {

        var field = ""

        for (ds in ds.child("Client").children) {

            val uid = ds.child("uid").value.toString()

            if (uid == currentUser.uid) {

                field = ds.child("field").value.toString()

                break

            }

        }

        return field

    }

    private fun filterBySalary(
        ds: DataSnapshot,
        minSalary: Int,
        maxSalary: Int,
        field: String
    ): MutableList<Job> {

        var filteredJobs = mutableListOf<Job>()


        for (ds in ds.child("Add").children) {

            val salary = ds.child("salary").value.toString().toInt()
            val jobField = ds.child("Field").value.toString()

            if (field == jobField) {

                if (salary in minSalary..maxSalary) {

                    val jobID = ds.child("id").value.toString().toInt()
                    val address = ds.child("add").value.toString()
                    val desc = ds.child("desc").value.toString()
                    val minReq = ds.child("minReq").value.toString()
                    val salary = ds.child("salary").value.toString().toInt()
                    val title = ds.child("title").value.toString()
                    val wday = ds.child("wday").value.toString()
                    val whour = ds.child("whour").value.toString()
                    val companyName = ds.child("Company Name").value.toString()

                    filteredJobs.add(Job(jobID, address, desc, minReq, salary, title, wday, whour, companyName))

                }
            }


        }

        return filteredJobs

    }

    override fun onAttach(context: Context) {

        super.onAttach(context)

        mContext = context

    }

}