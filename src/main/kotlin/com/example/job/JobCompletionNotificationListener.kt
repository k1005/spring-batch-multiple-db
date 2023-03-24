package com.example.job

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.stereotype.Component


@Component
class JobCompletionNotificationListener : JobExecutionListener {

    private val log: Logger = LoggerFactory.getLogger(JobCompletionNotificationListener::class.java)

    override fun beforeJob(jobExecution: JobExecution) {
        log.info("=============================================")
        log.info("${jobExecution.status}: ${jobExecution.jobId}")
    }

    override fun afterJob(jobExecution: JobExecution) {
        log.info("${jobExecution.status}: ${jobExecution.jobId}")
        log.info("=============================================")
    }

}
