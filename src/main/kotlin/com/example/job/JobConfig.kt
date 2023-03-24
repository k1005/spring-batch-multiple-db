package com.example.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class JobConfig(
    private val listener: JobCompletionNotificationListener,
    @Qualifier("departmentStep")
    private val departmentStep: Step
) {

    @Bean
    fun importUserInfoJob(
        jobRepository: JobRepository
    ): Job {
        return JobBuilder("ImportDepartmentJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .flow(departmentStep)
            .end()
            .build()
    }

}
