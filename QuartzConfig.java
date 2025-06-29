package com.airtribe.jobscheduler.config;

import com.airtribe.jobscheduler.listener.GlobalJobListener;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    private final ApplicationContext applicationContext;
    private final DataSource dataSource;
    private final QuartzProperties quartzProperties;

    public QuartzConfig(ApplicationContext applicationContext, DataSource dataSource, QuartzProperties quartzProperties) {
        this.applicationContext = applicationContext;
        this.dataSource = dataSource;
        this.quartzProperties = quartzProperties;
    }

    @Bean
    public SchedulerFactoryBean scheduler(GlobalJobListener globalJobListener) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);

        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setSchedulerName("Job-Scheduler");

        // Register Listeners
        schedulerFactoryBean.setGlobalJobListeners(globalJobListener);

        // Load properties from application.properties
        Properties props = new Properties();
        props.putAll(quartzProperties.getProperties());
        schedulerFactoryBean.setQuartzProperties(props);

        return schedulerFactoryBean;
    }
}