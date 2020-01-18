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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*

class AppliedJobsFragment : Fragment() {

    private lateinit var database: DatabaseReference

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val currentUser = auth.currentUser

    private var mContext: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        database = FirebaseDatabase.getInstance().reference

        val layoutManager = LinearLayoutManager(mContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        jobRecycler.layoutManager = layoutManager

        getAppliedJobs()

    }

    override fun onAttach(context: Context) {

        super.onAttach(context)

        mContext = context

    }

    private fun getAppliedJobs() {

        var jobIdList: MutableList<Int>

        var appliedJobs: MutableList<Job>

        val jobListener = object : ValueEventListener {

            override fun onDataChange(ds: DataSnapshot) {

                jobIdList = getJobID(ds)
                appliedJobs = filterById(ds, jobIdList)

                val adapter =
                    JobsAdapter(
                        mContext!!,
                        appliedJobs
                    )

                jobRecycler.adapter = adapter

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        }

        database.addListenerForSingleValueEvent(jobListener)


    }

    private fun getJobID(ds: DataSnapshot): MutableList<Int> {

        var jobIdList = mutableListOf<Int>()

        for (ds in ds.child("Client").child(currentUser!!.uid).child("appliedJobs").children) {

            jobIdList.add(ds.value.toString().toInt())

        }

        return jobIdList

    }

    private fun filterById(ds: DataSnapshot, jobIdList: MutableList<Int>): MutableList<Job> {

        val mutableListIterator = jobIdList.iterator()

        var filteredJobs = mutableListOf<Job>()

        for (i in mutableListIterator) {

            for (ds in ds.child("Add").children) {

                val ID = ds.child("id").value.toString().toInt()

                if (ID == i) {

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

}