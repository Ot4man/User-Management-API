
package ma.usermanagement.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ma.usermanagement.repository")
public class PersistenceConfig {

    private final Environment environment;

    public PersistenceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName(
                environment.getRequiredProperty("db.driver")
        );

        dataSource.setJdbcUrl(
                environment.getRequiredProperty("db.url")
        );

        dataSource.setUsername(
                environment.getRequiredProperty("db.username")
        );

        dataSource.setPassword(
                environment.getProperty("db.password", "")
        );

        dataSource.setMaximumPoolSize(
                environment.getProperty(
                        "db.pool.maximumPoolSize", Integer.class, 10
                )
        );

        dataSource.setMinimumIdle(
                environment.getProperty(
                        "db.pool.minimumIdle", Integer.class, 2
                )
        );

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);
        factory.setPackagesToScan("ma.usermanagement.model");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties properties = new Properties();

        properties.setProperty(
                "hibernate.dialect",
                environment.getRequiredProperty("hibernate.dialect")
        );

        properties.setProperty(
                "hibernate.show_sql",
                environment.getProperty("hibernate.show_sql", "true")
        );

        properties.setProperty(
                "hibernate.format_sql",
                environment.getProperty("hibernate.format_sql", "true")
        );

        properties.setProperty(
                "hibernate.hbm2ddl.auto",
                environment.getProperty("hibernate.hbm2ddl.auto", "update")
        );

        factory.setJpaProperties(properties);

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
