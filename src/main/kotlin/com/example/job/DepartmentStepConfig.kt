package com.example.job

import com.example.entity.input.InputDepartment
import com.example.entity.main.MainDepartment
import jakarta.persistence.EntityManagerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class DepartmentStepConfig(
    @Qualifier("mainEntityManagerFactory")
    private val mainEntityManagerFactory: EntityManagerFactory,
    @Qualifier("inputEntityManagerFactory")
    private val inputEntityManagerFactory: EntityManagerFactory
) {

    private val log: Logger = LoggerFactory.getLogger(DepartmentStepConfig::class.java)

    @Bean(name=["departmentStep"])
    fun departmentStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        reader: JpaCursorItemReader<InputDepartment>,
        writer: JpaItemWriter<MainDepartment>
    ): Step {
        return StepBuilder("departmentStep", jobRepository)
            .chunk<InputDepartment, MainDepartment>(1000, transactionManager)
            .reader(reader)
            .processor {
                MainDepartment(
                    deptCode = it.departmentCode,
                    deptName = it.departmentName,
                    parentCode = it.parentDepartmentCode,
                    lvl = it.departmentDepth,
                    sortNumber = it.orderSequence,
                    isEnable = it.isDeleted
                )
            }
            .writer(writer)
            .build()
    }

    @Bean
    fun departmentReader(): JpaCursorItemReader<InputDepartment> {
        return JpaCursorItemReaderBuilder<InputDepartment>()
            .name("readDepartment")
            .entityManagerFactory(inputEntityManagerFactory)
            .queryString("SELECT d FROM InputDepartment d")
            .build()
    }

    @Bean
    fun departmentWriter(): JpaItemWriter<MainDepartment> {
        return JpaItemWriterBuilder<MainDepartment>()
            .entityManagerFactory(mainEntityManagerFactory)
            .build()
    }
}
