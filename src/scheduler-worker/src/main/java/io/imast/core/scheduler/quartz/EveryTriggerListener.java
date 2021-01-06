package io.imast.core.scheduler.quartz;

import io.imast.core.scheduler.api.WorkerChannel;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

/**
 * The trigger listener
 * 
 * @author davitp
 */
@Slf4j
public class EveryTriggerListener implements TriggerListener {
    
    /**
     * The worker channel
     */
    protected final WorkerChannel workerChannel;
    
    /**
     * Creates new instance of Every Job Listener
     * 
     * @param workerChannel The worker channel
     */
    public EveryTriggerListener(WorkerChannel workerChannel) {
        this.workerChannel = workerChannel;
    }

    /**
     * Gets the name of listener
     * 
     * @return Returns listener name
     */
    @Override
    public String getName() {
        return "EveryTriggerListener";
    }

    /**
     * The trigger fired event handler
     * 
     * @param trigger The trigger to fire
     * @param context The job execution context
     */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
    }

    /**
     * Veto the job execution
     * 
     * @param trigger The trigger to fire
     * @param context The job execution context
     * @return Returns veto instruction
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    /**
     * The trigger misfired event handler
     * 
     * @param trigger The trigger to fire
     */
    @Override
    public void triggerMisfired(Trigger trigger) {
    }

    /**
     * The trigger completion trigger
     * 
     * @param trigger The trigger to fire
     * @param context The job execution context
     * @param triggerInstructionCode The trigger instruction code
     */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
    }
}
