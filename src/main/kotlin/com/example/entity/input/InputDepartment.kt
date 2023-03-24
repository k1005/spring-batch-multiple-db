package com.example.entity.input

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "department")
data class InputDepartment(
    @Id
    var departmentCode: String,
    var departmentName: String,
    var parentDepartmentCode: String?,
    var departmentDepth: Int,
    var orderSequence: Int?,
    var isDeleted: Boolean,
)
