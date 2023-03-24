package com.example.entity.main

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "department")
data class MainDepartment(
    @Id
    var deptCode: String,
    var deptName: String,
    var parentCode: String?,
    var lvl: Int,
    var sortNumber: Int?,
    var isEnable: Boolean
)
