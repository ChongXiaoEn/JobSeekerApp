package com.example.jobseeker.Activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.jobseeker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_job_details.*

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        database = FirebaseDatabase.getInstance().reference

        val bundle: Bundle? = intent.extras

        bundle?.let {

            val jobID = bundle.getInt("job_id")

            getJobInfo(jobID)

            checkApplied(jobID, this)

            checkBookmarked(jobID, this)

        }
    }

    private fun getJobInfo(jobID: Int) {

        val jobListener = object : ValueEventListener {

            override fun onDataChange(ds: DataSnapshot) {

                setJobInfo(ds, jobID)

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        }

        database.addValueEventListener(jobListener)


    }

    private fun setJobInfo(ds: DataSnapshot, jobID: Int) {


        for (ds in ds.child("Add").children) {

            if (jobID == ds.child("id").value.toString().toInt()) {


                txtPosition.text = ds.child("title").value.toString()
                txtSalary.text = ds.child("salary").value.toString()
                txtLocation.text = ds.child("add").value.toString()
                txtReqDetails.text = ds.child("minReq").value.toString()
                txtScopeDetails.text = ds.child("desc").value.toString()

                break

            }


        }


    }

    private fun applyJob(jobID: Int) {

        val key = database.child("Client").child(currentUser!!.uid).child("appliedJobs").key

        if (key == null) {

            var appliedList = mutableListOf<Int>()

            appliedList.add(jobID)

            database.child("Client").child(currentUser!!.uid).child("appliedJobs").push()
                .setValue(jobID)

            database.child("Add").child(jobID.toString()).child("People").push()
                .setValue(currentUser.uid)

        } else {

            database.child("Client").child(currentUser!!.uid).child("appliedJobs").push()
                .setValue(jobID)

            database.child("Add").child(jobID.toString()).child("People").push()
                .setValue(currentUser.uid)

        }

    }

    private fun saveJob(jobID: Int) {

        val key = database.child("Client").child(currentUser!!.uid).child("savedJobs").key

        if (key == null) {

            var savedList = mutableListOf<Int>()

            savedList.add(jobID)

            database.child("Client").child(currentUser!!.uid).child("savedJobs").push()
                .setValue(savedList)

        } else {

            database.child("Client").child(currentUser!!.uid).child("savedJobs").push()
                .setValue(jobID)

        }


    }

    private fun checkApplied(jobID: Int, context: Context) {

        database.child("Client").child(currentUser!!.uid).child("appliedJobs")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(ds: DataSnapshot) {

                        var applied = contains(ds, jobID)

                        if (!applied) {

                            btnApply.setText(R.string.btnApply_apply)
                            btnApply.setTextColor(resources.getColor(R.color.colorGray))
                            btnApply.setBackgroundResource(R.color.button_default)

                        } else {

                            btnApply.setText(R.string.btnApply_cancel)
                            btnApply.setTextColor(Color.WHITE)
                            btnApply.setBackgroundResource(R.color.button_pressed)

                        }

                        btnApply.setOnClickListener {

                            if (!applied) {

                                btnApply.setText(R.string.btnApply_apply)
                                btnApply.setTextColor(resources.getColor(R.color.colorGray))
                                btnApply.setBackgroundResource(R.color.button_default)

                                val builder = AlertDialog.Builder(
                                    context,
                                    R.style.MyDialogTheme
                                )
                                builder.apply {
                                    setTitle(R.string.btnApply_dialog_title)
                                    setMessage(R.string.btnApply_dialog_message)
                                    setPositiveButton(R.string.dialog_ok) { dialog, which ->

                                        applyJob(jobID)

                                        Toast.makeText(
                                            applicationContext,
                                            R.string.btnApply_dialog_job_applied,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnApply.setText(R.string.btnApply_cancel)
                                        btnApply.setTextColor(Color.WHITE)
                                        btnApply.setBackgroundResource(R.color.button_pressed)
                                    }
                                    setNegativeButton(R.string.dialog_cancel) { dialog, which ->

                                    }
                                }

                                val dialog: AlertDialog = builder.show()
                                val positiveButton: Button =
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                val negativeButton: Button =
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                positiveButton.setTextColor(Color.WHITE)
                                negativeButton.setTextColor(Color.WHITE)
                                dialog.show()

                            } else {

                                btnApply.setText(R.string.btnApply_cancel)
                                btnApply.setTextColor(Color.WHITE)
                                btnApply.setBackgroundResource(R.color.button_pressed)

                                val builder = AlertDialog.Builder(
                                    context,
                                    R.style.MyDialogTheme
                                )
                                builder.apply {
                                    setTitle(R.string.btnApply_dialog_title_cancel)
                                    setMessage(R.string.btnApply_dialog_message_cancel)
                                    setPositiveButton(R.string.dialog_ok) { dialog, which ->

                                        removeAppliedJob(jobID, ds)

                                        Toast.makeText(
                                            applicationContext,
                                            R.string.btnApply_dialog_job_cancel,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnApply.setText(R.string.btnApply_apply)
                                        btnApply.setTextColor(resources.getColor(R.color.colorGray))
                                        btnApply.setBackgroundResource(R.color.button_default)
                                    }
                                    setNegativeButton(R.string.dialog_cancel) { dialog, which ->

                                    }

                                }
                                val dialog: AlertDialog = builder.show()
                                val positiveButton: Button =
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                val negativeButton: Button =
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                positiveButton.setTextColor(Color.WHITE)
                                negativeButton.setTextColor(Color.WHITE)
                                dialog.show()
                            }
                        }

                    }
                })


    }

    private fun contains(ds: DataSnapshot, jobID: Int): Boolean {

        for (ds in ds.children) {

            if (ds.value.toString().toInt() == jobID) {

                return true

            }

        }

        return false

    }

    private fun checkBookmarked(jobID: Int, context: Context) {

        database.child("Client").child(currentUser!!.uid).child("savedJobs").addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(ds: DataSnapshot) {

                    var bookmarked = contains(ds, jobID)

                    if (!bookmarked) {

                        btnBookmark.setText(R.string.btnBookmark_bookmark)
                        btnBookmark.setTextColor(resources.getColor(R.color.colorGray))


                    } else {

                        btnBookmark.setText(R.string.btnBookmark_cancel)
                        btnBookmark.setTextColor(resources.getColor(R.color.colorBlack))

                    }

                    btnBookmark.setOnClickListener {

                        if (!bookmarked) {

                            btnBookmark.setText(R.string.btnBookmark_bookmark)
                            btnBookmark.setTextColor(resources.getColor(R.color.colorGray))

                            val builder = AlertDialog.Builder(
                                context,
                                R.style.MyDialogTheme
                            )
                            builder.apply {
                                setTitle(R.string.btnBookmark_dialog_bk_title)
                                setMessage(R.string.btnBookmark_dialog_bk_message)
                                setPositiveButton(R.string.dialog_ok) { dialog, which ->

                                    saveJob(jobID)

                                    Toast.makeText(
                                        applicationContext,
                                        R.string.btnBookmark_dialog_bookmark,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    finish()

                                    btnBookmark.setText(R.string.btnBookmark_cancel)
                                    btnBookmark.setTextColor(resources.getColor(R.color.colorBlack))

                                }
                                setNegativeButton(R.string.dialog_cancel) { dialog, which ->

                                }
                            }

                            val dialog: AlertDialog = builder.show()
                            val positiveButton: Button =
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            val negativeButton: Button =
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            positiveButton.setTextColor(Color.WHITE)
                            negativeButton.setTextColor(Color.WHITE)
                            dialog.show()

                        } else {

                            btnBookmark.setText(R.string.btnBookmark_cancel)
                            btnBookmark.setTextColor(resources.getColor(R.color.colorBlack))

                            val builder = AlertDialog.Builder(
                                context,
                                R.style.MyDialogTheme
                            )
                            builder.apply {
                                setTitle(R.string.btnBookmark_dialog_bk_title_cancel)
                                setMessage(R.string.btnApply_dialog_message_cancel)
                                setPositiveButton(R.string.dialog_ok) { dialog, which ->

                                    removeBookmarked(jobID, ds)

                                    Toast.makeText(
                                        applicationContext,
                                        R.string.btnBookmark_dialog_bookmark_cancel,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    finish()

                                    btnBookmark.setText(R.string.btnBookmark_bookmark)
                                    btnBookmark.setTextColor(resources.getColor(R.color.colorGray))
                                }
                                setNegativeButton(R.string.dialog_cancel) { dialog, which ->

                                }

                            }
                            val dialog: AlertDialog = builder.show()
                            val positiveButton: Button =
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            val negativeButton: Button =
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            positiveButton.setTextColor(Color.WHITE)
                            negativeButton.setTextColor(Color.WHITE)
                            dialog.show()
                        }
                    }

                }
            })


    }

    private fun removeAppliedJob(jobID: Int, ds: DataSnapshot) {

        for (item in ds.children) {

            val id = item.value.toString().toInt()

            if (jobID == id) {

                val key = item.key!!

                database.child("Client").child(currentUser!!.uid).child("appliedJobs").child(key)
                    .removeValue()

                break

            }


        }


    }

    private fun removeBookmarked(jobID: Int, ds: DataSnapshot) {

        for (item in ds.children) {

            val id = item.value.toString().toInt()

            if (jobID == id) {

                val key = item.key!!

                database.child("Client").child(currentUser!!.uid).child("savedJobs").child(key)
                    .removeValue()

                break

            }

        }

    }

}