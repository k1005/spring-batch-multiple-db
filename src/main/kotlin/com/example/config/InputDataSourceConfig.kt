package com.example.config

import com.example.config.support.SpringDataConfig
import com.example.config.support.YamlPropertySourceFactory
import com.example.entity.input.InputDepartment
import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager
import javax.sql.DataSource


@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(
    entityManagerFactoryRef = "inputEntityManagerFactory",
    transactionManagerRef = "inputTransactionManager",
    basePackages = ["com.example.entity.input"]
)
@PropertySource(
    value = ["classpath:/input-datasource.yml"],
    factory = YamlPropertySourceFactory::class
)
class InputDataSourceConfig(
    private val env: Environment
) {
    companion object {
        const val INPUT_TRANSACTION_MANAGER = "inputTransactionManager"
    }

    @Bean
    @ConfigurationProperties("input.datasource")
    fun inputDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @ConfigurationProperties("input.datasource.hikari")
    fun inputDataSource(
        @Qualifier("inputDataSourceProperties")
        inputDataSourceProperties: DataSourceProperties
    ): DataSource {
        return SpringDataConfig.initializeDataSource(inputDataSourceProperties)
    }

    @Bean
    fun inputEntityManagerFactory(
        persistenceUnitManagerProvider: ObjectProvider<PersistenceUnitManager>,
        springJpaProperties: JpaProperties,
        springHibernateProperties: HibernateProperties,
        @Qualifier("inputDataSource") springDataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean? {
        return SpringDataConfig.createEntityManagerFactory(
            persistenceUnitManagerProvider,
            springJpaProperties,
            springHibernateProperties,
            springDataSource,
            InputDepartment::class.java,
            "input-persistence-unit"
        )
    }

    @Bean(INPUT_TRANSACTION_MANAGER)
    fun transactionManager(
        @Qualifier("inputEntityManagerFactory")
        entityManagerFactory: EntityManagerFactory
    ): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
