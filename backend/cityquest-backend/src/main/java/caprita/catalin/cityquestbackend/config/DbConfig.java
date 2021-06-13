package caprita.catalin.cityquestbackend.config;

import liquibase.pro.packaged.L;
import org.aspectj.util.PartialOrder;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "caprita.catalin.cityquestbackend",
entityManagerFactoryRef = "entityManagerFactoryBean",
transactionManagerRef = "transactionManager")
@EnableTransactionManagement
public class DbConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(){
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    @Qualifier("custom")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource){
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(dataSource);
        emfb.setPersistenceUnitName("main-unit");
        emfb.setPackagesToScan("caprita.catalin.cityquestbackend");
        emfb.setPersistenceProvider(new HibernatePersistenceProvider());
        emfb.setJpaProperties(hibernateProperties());
        return emfb;
    }

    @Bean
    @Primary
    public JpaTransactionManager transactionManager(@Qualifier("custom") EntityManagerFactory emf){
        JpaTransactionManager jtm = new JpaTransactionManager(emf);
        return jtm;
    }

    private Properties hibernateProperties(){
        Properties properties = new Properties();
        properties.put(Environment.HBM2DDL_AUTO,"update");
        properties.put(Environment.SHOW_SQL,true);
        properties.put(Environment.HIGHLIGHT_SQL,true);
        properties.put(Environment.DIALECT,"org.hibernate.dialect.MySQL8Dialect");

        return properties;
    }
}
