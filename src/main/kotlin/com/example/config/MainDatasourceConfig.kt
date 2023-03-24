package com.example.config

import com.example.config.support.SpringDataConfig
import com.example.entity.main.MainDepartment
import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager
import javax.sql.DataSource


@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(
    entityManagerFactoryRef = "mainEntityManagerFactory",
    transactionManagerRef = "mainTransactionManager",
    basePackages = ["com.example.entity.main"],
)
class MainDatasourceConfig {
    companion object {
        const val MAIN_TRANSACTION_MANAGER = "mainTransactionManager"
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    fun mainDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    fun mainDataSource(
        @Qualifier("mainDataSourceProperties")
        dataSourceProperties: DataSourceProperties
    ): DataSource {
        return SpringDataConfig.initializeDataSource(dataSourceProperties)
    }

    @Primary
    @Bean
    fun mainEntityManagerFactory(
        persistenceUnitManagerProvider: ObjectProvider<PersistenceUnitManager>,
        springJpaProperties: JpaProperties,
        springHibernateProperties: HibernateProperties,
        @Qualifier("mainDataSource") springDataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean? {
        return SpringDataConfig.createEntityManagerFactory(
            persistenceUnitManagerProvider,
            springJpaProperties,
            springHibernateProperties,
            springDataSource,
            MainDepartment::class.java,
            "main-persistence-unit"
        )
    }

    @Primary
    @Bean(MAIN_TRANSACTION_MANAGER)
    fun mainTransactionManager(
        @Qualifier("mainEntityManagerFactory")
        entityManagerFactory: EntityManagerFactory
    ): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }

}
