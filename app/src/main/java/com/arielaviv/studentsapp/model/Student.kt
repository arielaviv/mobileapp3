package com.arielaviv.studentsapp.model

import com.arielaviv.studentsapp.R

data class Student(
    var id: String,
    var name: String,
    var phone: String,
    var address: String,
    var isChecked: Boolean = false,
    var birthDate: String = "",  // dd/MM/yyyy
    var birthTime: String = "",
    val avatarResId: Int = R.drawable.ic_student_avatar
)
