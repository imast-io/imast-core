package io.imast.core.scheduler;

import io.imast.core.Zdt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * The job factory for job manager
 * 
 * @author davitp
 */
@Slf4j
public class JobFactory {
    
    /**
     * The job modules by type
     */
    @Getter
    protected final Map<String, Map<String, Object>> jobModules;
    
    /**
     * The map of job classes 
     */
    @Getter
    protected final Map<String, Class> jobClasses;
    
    /**
     * Creates new instance of job factory
     */
    public JobFactory(){
        this.jobModules = new HashMap<>();
        this.jobClasses = new HashMap<>();
    }
    
    /**
     * Register a module for the given job type with the given key
     * 
     * @param type The job type
     * @param key The module key
     * @param module The module instance
     */
    public void registerModule(String type, String key, Object module){
        
        // add type if missing
        if(!this.jobModules.containsKey(type)){
            this.jobModules.put(type, new HashMap<>());
        }
        
        // register module
        this.jobModules.get(type).put(key, module);
    }
    
    /**
     * Register a class mapping for the given job type
     * 
     * @param type The job type
     * @param clazz The class type
     */
    public void registerJobClass(String type, Class clazz){
        this.jobClasses.put(type, clazz);
    }
    
    /**
     * Creates the job corresponding to the definition
     * 
     * @param key The job key
     * @param jobDefinition The job definition
     * @return Returns job details instance
     */
    public JobDetail createJob(JobKey key, JobDefinition jobDefinition){
        
        // try resolve class 
        Class jobClass = this.getJobClass(jobDefinition.getType());

        // check if available
        if(jobClass == null){
            log.error("JobFactory: Unknown job class type for the job type: " + jobDefinition.getType());
            return null;
        }
        
        // instantiate a job to schedule 
        var job = JobBuilder.newJob(jobClass)
                .withIdentity(key)
                .storeDurably(false)
                .build();

        // add definition to data map
        job.getJobDataMap().put(JobConstants.JOB_DEFINITION, jobDefinition);
        
        return job;
    }
    
    /**
     * Creates the set of Cron triggers for the given job
     * 
     * @param jobDefinition The job definition
     * @return Returns job triggers
     */
    public HashSet<Trigger> createCronTriggers(JobDefinition jobDefinition){
        // job triggers
        HashSet<Trigger> triggers = new HashSet<>();
        
        // unique expressions
        HashSet<String> expressions = new HashSet<>();
            
        // add all the cron triggers
        for(CronTrigger cronTrigger : jobDefinition.getCronTriggers()){

            // cron expression
            String cronExpression = cronTrigger.getExpression();

            // check for uniqueness
            if(expressions.contains(cronExpression)){
                log.warn("JobFactory: Skipping expression because of duplication: "  + cronExpression);
                continue;
            }
            
            // check validity 
            if(!CronExpression.isValidExpression(cronExpression)){
                log.warn("JobFactory: Skipping cron trigger as it is not valid: " + cronExpression);
                continue;
            }

            // create trigger
            var triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(cronExpression, JobOps.identity(jobDefinition))
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
            
            // if end time is given
            if(jobDefinition.getEndAt() != null){
                triggerBuilder.endAt(Zdt.toDate(jobDefinition.getEndAt()));
            }

            // schedule job with cron trigger
            triggers.add(triggerBuilder.build());
        
            // remember expression to avoid duplicates
            expressions.add(cronExpression);
        }
        
        return triggers;
    }
    
    /**
     * Creates the set of cron triggers for the given job
     * 
     * @param jobDefinition The job definition
     * @return Returns job triggers
     */
    public HashSet<Trigger> createStaticPeriodTriggers(JobDefinition jobDefinition){
        // job triggers
        HashSet<Trigger> triggers = new HashSet<>();
     
        // period in milliseconds
        var periodMs = jobDefinition.getPeriod();
        
        // if not given
        if(periodMs == null || periodMs == 0.0){
            log.warn("JobFactory: Cannot create static period trigger because of missing period.");
            return new HashSet<>();
        }
        
        // convert to seconds
        var periodSecond = (int) (periodMs / 1000.0);
        
        // create trigger
        var triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity("STATIC_PERIOD", JobOps.identity(jobDefinition))
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(periodSecond));
        
        // if end time is given
        if(jobDefinition.getEndAt() != null){
            triggerBuilder.endAt(Zdt.toDate(jobDefinition.getEndAt()));
        }
        
        // add trigger
        triggers.add(triggerBuilder.build());
        
        return triggers;
    }
    
    /**
     * Creates the set of cron triggers for the given job
     * 
     * @param jobDefinition The job definition
     * @return Returns job triggers
     */
    public HashSet<Trigger> createOneTimeTriggers(JobDefinition jobDefinition){
        // job triggers
        HashSet<Trigger> triggers = new HashSet<>();
      
        // create trigger
        var trigger = TriggerBuilder.newTrigger()
                .withIdentity("ONE_TIME_TRIGGER", JobOps.identity(jobDefinition))
                .startNow()
                .build();
        
        // add trigger
        triggers.add(trigger);
        
        return triggers;
    }
    
    /**
     * Creates the set of triggers for the given job
     * 
     * @param jobDefinition The job definition
     * @return Returns job triggers
     */
    public HashSet<Trigger> createTriggers(JobDefinition jobDefinition){
        
        switch(jobDefinition.getScheduleType()){
            case CRON:
                return this.createCronTriggers(jobDefinition);
            case STATIC_PERIOD:
                return this.createStaticPeriodTriggers(jobDefinition);
            case ONE_TIME:
                return this.createOneTimeTriggers(jobDefinition);
            default:
                return new HashSet<>();
        }
    }
    
    /**
     * Gets the job class type
     * 
     * @param type The type of job
     * @return Returns class for job
     */
    protected Class getJobClass(String type){
        return this.jobClasses.getOrDefault(type, null);
    }
}
