package io.imast.core.scheduler.quartz;

import io.imast.core.Zdt;
import io.imast.core.scheduler.JobOps;
import io.imast.core.scheduler.api.WorkerChannel;
import io.imast.core.scheduler.iterate.IterationStatus;
import io.imast.core.scheduler.iterate.JobIteration;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * The base job listener 
 * 
 * @author davitp
 */
@Slf4j
public class EveryJobListener implements JobListener {

    /**
     * The worker channel
     */
    protected final WorkerChannel workerChannel;
    
    /**
     * Creates new instance of Every Job Listener
     * 
     * @param workerChannel The worker channel
     */
    public EveryJobListener(WorkerChannel workerChannel) {
        this.workerChannel = workerChannel;   
    }
    
    /**
     * Gets name of job listener
     * 
     * @return Returns job listener name
     */
    @Override
    public String getName() {
        return "EveryJobListener";
    }

    /**
     * The job is about to be executed
     * 
     * @param context The job execution context
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
    }

    /**
     * The job execution is vetoed by trigger listener
     * 
     * @param context The job execution context
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    /**
     * The job was executed 
     * 
     * @param context The job execution context
     * @param jobException The job exception
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

        // get the job definition
        var definition = JobOps.getJobDefinition(context);
        
        // check if definition is null
        if(definition == null){
            return;
        }
        
        // the job id
        var jobId = definition.getId();
        
        // the job status
        var status = jobException == null ? IterationStatus.SUCCESS : IterationStatus.FAILURE;
        
        // if silent reporting 
        var silent = definition.getExecution() != null && definition.getExecution().isSilentIterations();
        
        // if silent reporting is enabled will just silently skip iteration report
        if(silent){
            log.debug(String.format("JobSchedulerListener: Job Iteration for %s(%s) completed with status %s", definition.getCode(), definition.getGroup(), status));
            return;
        }
        
        // the job run time
        var runtime = context.getJobRunTime();
        
        // create iteration entity
        var iteration = JobIteration.builder()
                .id(null)
                .jobId(jobId)
                .runtime(runtime)
                .status(status)
                .message(jobException == null ? null : jobException.toString())
                .timestamp(Zdt.utc())
                .build();
        
        // register iteration and get the result
        var result = this.workerChannel.iterate(iteration);
        
        if(!result.isPresent()){
            log.warn(String.format("EveryJobListener: Could not register %s iteration for job %s (%s)", iteration.getStatus(), definition.getCode(), definition.getGroup()));
        }
    }    
}
