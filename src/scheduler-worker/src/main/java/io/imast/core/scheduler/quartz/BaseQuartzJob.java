package io.imast.core.scheduler.quartz;

import io.imast.core.scheduler.JobConstants;
import io.imast.core.scheduler.JobDefinition;
import io.imast.core.scheduler.JobOps;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * The base quartz job 
 * 
 * @author davitp
 */
@Slf4j
public abstract class BaseQuartzJob implements Job {
    
    /**
     * Get the job definition
     * 
     * @param executionContext The execution context
     * @return Returns the job definition
     */
    protected JobDefinition getJobDefinition(JobExecutionContext executionContext){
        return JobOps.getJobDefinition(executionContext);
    }
    
    /**
     * Get type of job definition
     * 
     * @param executionContext The job execution context
     * @return Returns job type
     */
    protected String getType(JobExecutionContext executionContext){
        
        // get definition
        var definition = this.getJobDefinition(executionContext);
        
        return definition == null ? JobConstants.UNKNOWN_JOB_TYPE : definition.getType();
    }
    
    /**
     * Get the context module with the given key
     * 
     * @param <T> The type of module
     * @param key The module key
     * @param executionContext The execution context
     * @return Returns the module from context if given
     */
    protected <T> T getContextModule(String key, JobExecutionContext executionContext){
        return JobOps.getContextModule(key, this.getType(executionContext), executionContext);
    }
}
