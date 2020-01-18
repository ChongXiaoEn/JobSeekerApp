package com.example.jobseeker.Models

data class Users(

    var uid : String,

    var email : String,

    var fullName : String = "",

    var expLevel: String = "",

    var field: String = "",

    var country : String = "",

    var state : String = "",

    var phoneNo : String = "",

    var languages : MutableList<String>,

    var minSalary : Int,

    var maxSalary : Int,

    var appliedJobs : MutableList<Int> = mutableListOf(),

    var savedJobs : MutableList<Int> = mutableListOf()

)