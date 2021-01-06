package io.imast.core.scheduler.api;

import io.imast.core.scheduler.JobDefinition;
import io.imast.core.scheduler.JobStatus;
import io.imast.core.scheduler.agent.AgentDefinition;
import io.imast.core.scheduler.agent.AgentHealth;
import io.imast.core.scheduler.exchange.JobMetadataRequest;
import io.imast.core.scheduler.exchange.JobMetadataResponse;
import io.imast.core.scheduler.exchange.JobStatusExchangeRequest;
import io.imast.core.scheduler.exchange.JobStatusExchangeResponse;
import io.imast.core.scheduler.iterate.JobIteration;
import java.util.Optional;

/**
 * The worker channel interface 
 * 
 * @author davitp
 */
public interface WorkerChannel {
    
    /**
     * Pull job groups for the given cluster
     * 
     * @param request The job group request filter
     * @return Returns job group identities
     */
    public Optional<JobMetadataResponse> metadata(JobMetadataRequest request);
    
    /**
     * Exchange current status with modified entries
     * 
     * @param status The status exchange structure
     * @return Returns modified entries
     */
    public Optional<JobStatusExchangeResponse> statusExchange(JobStatusExchangeRequest status);
    
    /**
     * Adds iteration information to scheduler
     * 
     * @param iteration The iteration to register
     * @return Returns registered iteration
     */
    public Optional<JobIteration> iterate(JobIteration iteration);
    
    /**
     * Sets the status of job definition 
     * 
     * @param id The identifier of job definition
     * @param status The new status of job
     * @return Returns updated job definition
     */
    public Optional<JobDefinition> markAs(String id, JobStatus status);
    
    /**
     * Registers agent definition into the system 
     * 
     * @param agent The agent definition to register
     * @return Returns registered agent definition
     */
    public Optional<AgentDefinition> registration(AgentDefinition agent);
    
    /**
     * Send a Heartbit signal to scheduler
     * 
     * @param id The identifier of agent definition
     * @param health The health status of agent
     * @return Returns updated agent definition
     */
    public Optional<AgentDefinition> heartbeat(String id, AgentHealth health);
}
