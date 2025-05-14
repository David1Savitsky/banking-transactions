package com.savitsky.bankingtransactions.config;

import com.savitsky.bankingtransactions.job.BalanceGrowthJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final String BALANCE_GROWTH_TRIGGER = "balanceGrowthTrigger";
    private static final String BALANCE_GROWTH_JOB = "balanceGrowthJob";
    private static final int BALANCE_GROWTH_INTERVAL_IN_SECONDS = 30;

    @Bean
    public JobDetail balanceJobDetail() {
        return JobBuilder.newJob(BalanceGrowthJob.class)
                .withIdentity(BALANCE_GROWTH_JOB)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger balanceJobTrigger() {
        var scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(BALANCE_GROWTH_INTERVAL_IN_SECONDS)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(balanceJobDetail())
                .withIdentity(BALANCE_GROWTH_TRIGGER)
                .withSchedule(scheduleBuilder)
                .build();
    }
}
