package com.example.jobseeker.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobseeker.Activities.JobDetailsActivity
import com.example.jobseeker.Models.Job
import com.example.jobseeker.R
import kotlinx.android.synthetic.main.list_job.view.*

class JobsAdapter(private val context: Context, private val fullJobs: List<Job>) :
    RecyclerView.Adapter<JobsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.list_job, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {

        return fullJobs.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val job = fullJobs[position]
        holder.setData(job, position)


    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var currentJob: Job? = null
        var currentPosition: Int = 0

        fun setData(job: Job?, position: Int) {

            job?.let {

                itemView.txtPosition.text = job.title
                itemView.txtLocation.text = job.address
                itemView.txtSalary.text = job.salary.toString()
                itemView.txtCompany.text = job.companyName

            }

            this.currentJob = job
            this.currentPosition = position

        }

        init {

            itemView.setOnClickListener{

                val intent = Intent(context, JobDetailsActivity::class.java)

                intent.putExtra("job_id", currentJob!!.jobID)

                context.startActivity(intent)

            }

        }

    }
}