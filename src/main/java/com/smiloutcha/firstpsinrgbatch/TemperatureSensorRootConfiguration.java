package com.smiloutcha.firstpsinrgbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
public class TemperatureSensorRootConfiguration extends DefaultBatchConfiguration  {

    @Value("classpath:input/HTE2NP.txt")
    private Resource rawDailyInputResource;

    @Value("file:HTE2PN.xml")
    private WritableResource aggregatedDailyOutputXmlResource;

    @Value("file:HTE2NP-anomalies.csv")
    private WritableResource anomalyDataResource;

    @Bean
    public Job temperatureSensorJob(JobRepository jobRepository,
                                    @Qualifier("aggregateSensorStep") Step aggregateSensorStep,
                                    @Qualifier("reportAnomaliesStep") Step reportAnomaliesStep) {
        return new JobBuilder("temperatureSensorJob",jobRepository)
                .start(aggregateSensorStep)
                .next(reportAnomaliesStep)
                .build();
    }


    @Bean
    @Qualifier("aggregateSensorStep")
    public Step aggregateSensorStep(JobRepository jobRepository , PlatformTransactionManager transactionManager) {
        return new StepBuilder("aggregator-sensor",jobRepository)
                .<DailySensorData,DailyAggregatedSensorData>chunk(1,transactionManager)
                .reader(new FlatFileItemReaderBuilder<DailySensorData>()
                        .name("dailySensorData")
                        .resource(rawDailyInputResource)
                        .lineMapper(new SensorDataTextMapper())
                        .build())
                .processor(new RawToAggregateSensorDataProcessor())
                .writer(new StaxEventItemWriterBuilder<DailyAggregatedSensorData>()
                        .name("dailyAggregatedSensorDataWriter")
                        .marshaller(DailyAggregatedSensorData.getMarshaller())
                        .resource(aggregatedDailyOutputXmlResource)
                        .rootTagName("data")
                        .overwriteOutput(true)
                        .build())
                .build();
    }

    @Bean
    @Qualifier("reportAnomaliesStep")
    public Step reportAnomaliesStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager) {
        return new StepBuilder("report-anomalies",jobRepository)
                .<DailyAggregatedSensorData,DataAnomaly>chunk(1,transactionManager)
                .reader(new StaxEventItemReaderBuilder<DailyAggregatedSensorData>()
                        .name("dailyAggregatedSensorDataReader")
                        .unmarshaller(DailyAggregatedSensorData.getMarshaller())
                        .resource(aggregatedDailyOutputXmlResource)
                        .addFragmentRootElements(DailyAggregatedSensorData.ITEM_ROOT_ELEMENT_NAME)
                        .build())
                .processor(new SensorDataAnomalyProcessor())
                .writer(new FlatFileItemWriterBuilder<DataAnomaly>()
                        .name("dataAnomalyWriter")
                        .resource(anomalyDataResource)
                        .delimited()
                        .delimiter(",")
                        .names(new String[]{"date","type","value"})
                        .build())
                .build();
    }

    @Bean
    public DataSource dataSource(@Value("${spring.datasource.driver-class-name}") String driverClassName,
                                 @Value("${spring.datasource.url}") String url,
                                 @Value("${spring.datasource.username}") String username,
                                 @Value("${spring.datasource.password}") String password) {
       var dataSource = new DriverManagerDataSource();
       dataSource.setDriverClassName(driverClassName);
       dataSource.setUrl(url);
       dataSource.setUsername(username);
       dataSource.setPassword(password);
       return dataSource;
    }


    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        var transactionManager = new JdbcTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    public BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer(DataSource dataSource,
                                                                               BatchProperties batchProperties) {
        return new BatchDataSourceScriptDatabaseInitializer(dataSource,batchProperties.getJdbc());
    }

    @Bean
    public BatchProperties batchProperties(@Value("${batch.db.initialize-schema}") DatabaseInitializationMode initializationMode) {
        var batchProperties = new BatchProperties();
        batchProperties.getJdbc().setInitializeSchema(initializationMode);
        return batchProperties;
    }




}
