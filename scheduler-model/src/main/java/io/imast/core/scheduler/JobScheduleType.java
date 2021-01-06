package io.imast.core.scheduler;

/**
 * The schedule type
 * 
 * @author davitp
 */
public enum JobScheduleType {
    
    /**
     * The static period schedule
     */
    STATIC_PERIOD,
    
    /**
     * The Cron type of schedule
     */
    CRON,
    
    /**
     * One-time execution job
     */
    ONE_TIME    
}
