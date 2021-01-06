package io.imast.core.scheduler;

/**
 * The scheduling operation
 * 
 * @author davitp
 */
public enum SchedulingOperation {
    
    /**
     * Schedule the job
     */
    SCHEDULE,
    
    /**
     * Reschedule the job
     */
    RESCHEDULE,
    
    /**
     * Unschedule the job
     */
    UNSCHEDULE
}
