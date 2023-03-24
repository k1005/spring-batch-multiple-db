package com.example.config.support

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
class SpringDataConfig {

    companion object {

        fun initializeDataSource(
            dataSourceProperties: DataSourceProperties
        ): HikariDataSource {
            return dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource::class.java)
                .build()
        }

        fun createEntityManagerFactory(
            persistenceUnitManagerProvider: ObjectProvider<PersistenceUnitManager>,
            jpaProperties: JpaProperties,
            hibernateProperties: HibernateProperties,
            dataSource: DataSource,
            exampleEntityClass: Class<*>,
            persistenceUnit: String
        ): LocalContainerEntityManagerFactoryBean {
            return createEntityManagerFactoryBuilder(
                persistenceUnitManagerProvider.ifAvailable,
                jpaProperties,
                hibernateProperties
            )
                .dataSource(dataSource)
                .packages(exampleEntityClass)
                .persistenceUnit(persistenceUnit)
                .build()
        }

        private fun createEntityManagerFactoryBuilder(
            persistenceUnitManager: PersistenceUnitManager?,
            jpaProperties: JpaProperties,
            hibernateProperties: HibernateProperties
        ): EntityManagerFactoryBuilder {
            val jpaVendorAdapter = createJpaVendorAdapter(jpaProperties)
            val expandedProperties =
                hibernateProperties.determineHibernateProperties(jpaProperties.properties, HibernateSettings())
            return EntityManagerFactoryBuilder(jpaVendorAdapter, expandedProperties, persistenceUnitManager)
        }

        private fun createJpaVendorAdapter(jpaProperties: JpaProperties): JpaVendorAdapter {
            val adapter: AbstractJpaVendorAdapter = HibernateJpaVendorAdapter()
            adapter.setShowSql(jpaProperties.isShowSql)
            adapter.setDatabasePlatform(jpaProperties.databasePlatform)
            adapter.setGenerateDdl(jpaProperties.isGenerateDdl)
            if (jpaProperties.database != null) {
                adapter.setDatabase(jpaProperties.database)
            }
            return adapter
        }
    }
}