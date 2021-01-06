package io.imast.core.scheduler;

import io.imast.core.Coll;
import io.imast.core.Lang;
import io.imast.core.Str;
import io.imast.core.Zdt;
import io.imast.core.scheduler.agent.AgentActivityType;
import io.vavr.control.Try;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import io.imast.core.scheduler.api.WorkerChannel;
import io.imast.core.scheduler.agent.AgentDefinition;
import io.imast.core.scheduler.agent.AgentHealth;
import io.imast.core.scheduler.exchange.JobMetadataRequest;
import io.imast.core.scheduler.exchange.JobStatusExchangeRequest;
import io.imast.core.scheduler.quartz.EveryJobListener;
import io.imast.core.scheduler.quartz.JobSchedulerListener;

/**
 * The job executor manager
 * 
 * @author davitp
 */
@Slf4j
public class WorkerController {
        
    /**
     * The job manager configuration
     */
    protected final WorkerControllerConfig config;
    
    /**
     * The job factory
     */
    protected final JobFactory jobFactory;
    
    /**
     * The worker channel
     */
    protected final WorkerChannel workerChannel;
    
    /**
     * The scheduler executor to sync jobs and exchange metadata
     */
    protected final ScheduledExecutorService schedulerExecutor;
    
    /**
     * The scheduler
     */
    protected Scheduler scheduler;
    
    /**
     * The quartz scheduler factory
     */
    protected StdSchedulerFactory schedulerFactory;
        
    /**
     * If the scheduler is valid
     */
    protected boolean valid;
    
    /**
     * The agent definition
     */
    protected AgentDefinition agentDefinition;
    
    /**
     * The job executor module 
     * 
     * @param config The job manager configuration
     * @param jobFactory The job factory
     * @param workerChannel The worker channel
     */
    public WorkerController(WorkerControllerConfig config, JobFactory jobFactory, WorkerChannel workerChannel){
        this.config = config;
        this.jobFactory = jobFactory;
        this.workerChannel = workerChannel;
        this.schedulerExecutor =  Executors.newScheduledThreadPool(1);
    }
    
    /**
     * Initialize the scheduling manager
     * 
     * @return Returns the validity state
     */
    public boolean initialize(){
        
        // properties for the quartz scheduler
        var props = this.initProps();
                
        // assign scheduler factory
        this.schedulerFactory = Try.of(() -> new StdSchedulerFactory(props)).getOrNull();
        
        // assign scheduler if valid
        this.scheduler = Try.of(() -> this.schedulerFactory == null ? null : this.schedulerFactory.getScheduler()).getOrNull();
        
        // validity indicator
        this.valid = this.scheduler != null;
        
        // initialize scheduler context
        this.initializeContext();
        
        return this.valid;
    }
    
    /**
     * Execute the job manager
     * 
     * @return Returns if successful
     */
    public boolean execute(){
        
        // do not execute if not valid
        if(!this.valid) return false;
        
        // the agent definition
        this.agentDefinition = this.ensureRegister(100);
        
        // register agent (try N times)
        if(this.agentDefinition == null){
            return false;
        }
        
        // schedule heartbit reporter
        this.schedulerExecutor.scheduleAtFixedRate(() -> this.heartbeat(), 0, this.config.getWorkerSignalRate().toMillis(), TimeUnit.MILLISECONDS);
        
        // if supervisor then schedule sync jobs
        if(this.config.isSupervise()){  
            // start job sync worker
            this.schedulerExecutor.scheduleAtFixedRate(() -> this.sync(), 0, this.config.getJobSyncRate().toMillis(), TimeUnit.MILLISECONDS);
          
        }
       
        try{
            this.scheduler.start();
        }
        catch(SchedulerException error){
            log.error("WorkerChannel: Could not start quartz scheduler: ", error);
        }
        
        return true;
    }
    
    /**
     * Add scheduler context entity to be used on demand
     * 
     */
    private void initContextModules(){
        try {
            this.scheduler.getContext().put(JobConstants.JOB_FACTORY, this.jobFactory);
            this.scheduler.getContext().put(JobConstants.JOB_MODULES, this.jobFactory.getJobModules());
        } catch (SchedulerException error) {
            log.error("WorkerController: Could not register job modules context: ", error);
        }
    }
    
    /**
     * Add scheduler listeners (job, trigger)
     */
    private void initListeners(){
        try{
            // register job listener
            this.scheduler.getListenerManager().addJobListener(new EveryJobListener(this.workerChannel));
            this.scheduler.getListenerManager().addSchedulerListener(new JobSchedulerListener(this.scheduler, this.workerChannel));
        }
        catch (SchedulerException error) {
            log.error("WorkerController: Could not register job/trigger listener: ", error);
        }
    }
    
    /**
     * Schedules the job definition
     * 
     * @param jobDefinition The job definition to schedule
     */
    public void schedule(JobDefinition jobDefinition){
        
        // the job key
        var key = JobKey.jobKey(jobDefinition.getCode(), jobDefinition.getGroup());
        
        try {
            // check if job exists
            var exists = this.scheduler.checkExists(key);
            
            // do not create if exists
            if(exists){
                log.error("WorkerController: Unable to schedule job that has been already scheduled");
                return;
            }
            
            // try create job
            var jobDetail = this.jobFactory.createJob(key, jobDefinition);
            
            // unschedule if exists
            if(jobDetail == null){
                log.error("WorkerController: Unable to create job via factory");
                return;
            }
            
            // create triggers
            HashSet<Trigger> triggers = this.jobFactory.createTriggers(jobDefinition);
            
            // add job to scheduler;
            this.scheduler.scheduleJob(jobDetail, triggers, true);
        }
        catch(SchedulerException error){
            log.error("WorkerController: Failed to schedule the job", error);
        }
    }
    
    /**
     * Schedules the job definition
     * 
     * @param jobDefinition The job definition to schedule
     */
    public void reschedule(JobDefinition jobDefinition){
        
        // the job key
        JobKey key = JobKey.jobKey(jobDefinition.getCode(), jobDefinition.getGroup());
        
        try {
            // check if job exists
            boolean exists = this.scheduler.checkExists(key);
            
            // unschedule if exists
            if(!exists){
                log.error("WorkerController: Job cannot be updated because it does not exist");
                return;
            }
            
            // try create job
            JobDetail jobDetail = this.scheduler.getJobDetail(key);
            
            // unschedule if exists
            if(jobDetail == null){
                log.error("WorkerController: Unable to find job by the key factory");
                return;
            }
            
            // update definition to data map
            jobDetail.getJobDataMap().put(JobConstants.JOB_DEFINITION, jobDefinition);
            
            // unschedule the triggers
            this.unscheduleTriggers(key);
            
            // create triggers
            var triggers = this.jobFactory.createTriggers(jobDefinition);
            
            // add job to scheduler;
            this.scheduler.scheduleJob(jobDetail, triggers, true);
        }
        catch(SchedulerException error){
            log.error("WorkerController: Failed to schedule the job", error);
        }
    }
    
    /**
     * Unschedule the job
     * 
     * @param jobCode The job code
     * @param jobGroup The group of job
     */
    public void unschedule(String jobCode, String jobGroup){
        
        // try the unschedule procedure
        try {
            // the job key
            JobKey key = JobKey.jobKey(jobCode, jobGroup);
            
            // check if job exists
            boolean exists = this.scheduler.checkExists(key);
            
            // unschedule if exists
            if(!exists){
                log.warn("JobManager: Job cannot be unscheduled because it does not exist");
                return;
            }
            
            // unschedule triggers
            this.unscheduleTriggers(key);
            
            // remove job
            this.scheduler.deleteJob(key);
        }
        catch (SchedulerException error){
            log.error("JobManager: Failed to unschedule the job", error);
        }
    }
    
    /**
     * Unschedule triggers for the given job
     * 
     * @param key The job key
     */
    private void unscheduleTriggers(JobKey key) {
        
        try{
            // unschedule if exists
            if(!this.scheduler.checkExists(key)){
                return;
            }

            // get triggers of job
            List<? extends Trigger> triggers = this.scheduler.getTriggersOfJob(key);

            // use empty stream
            triggers = triggers == null ? new ArrayList() : triggers;

            // unschedule triggers
            for(Trigger trigger : triggers){
                this.scheduler.unscheduleJob(trigger.getKey());
            }
        }
        catch(SchedulerException error){
            log.error("JobManager: Failed to unschedule the triggers", error);
        }       
    }
    
    /**
     * Refresh jobs and sync with server
     * 
     */
    public void sync(){
        
        try {
            this.syncImpl();
        }
        catch(Throwable error){
            log.error(String.format("WorkerController: Could not sync jobs, Error: %s", error.getLocalizedMessage()), error);
        }
    }
    
    /**
     * Sync from controller
     */
    protected void syncImpl(){
        
        // get metadata for cluster
        var metadata = this.workerChannel.metadata(new JobMetadataRequest(this.config.getCluster())).orElse(null);
        
        // check if no groups
        if(metadata == null){
            throw new RuntimeException("WorkerController: Could not pull metadata from controller.");
        }

        // get groups
        var groups = Lang.or(metadata.getGroups(), Str.EMPTY_LIST);
        
        // get types
        var types = Lang.or(metadata.getTypes(), Str.EMPTY_LIST);
        
        // for each group and type to sync process
        Coll.doubleForeach(groups, types, this::syncGroupImpl);
    }

    /**
     * Sync with controller for group and type pair
     * 
     * @param group The target group
     * @param type The target type
     */
    protected void syncGroupImpl(String group, String type){
        
        // get job list
        var statusUpdate = this.workerChannel.statusExchange(this.status(group, type)).orElse(null);

        // handle if not recieved jobs
        if(statusUpdate == null){
            log.warn("WorkerController: Did not get proper response from scheduler.");
            return;
        }

        log.debug(String.format("WorkerController: Syncing jobs (%s in %s) with server. Deleted: %s, Updated: %s, Added: %s", type, group, statusUpdate.getRemoved().size(), statusUpdate.getUpdated().size(), statusUpdate.getAdded().size()));

        // unschedule all the removed jobs
        statusUpdate.getRemoved().forEach((removedJob) -> {
            this.unschedule(removedJob, statusUpdate.getGroup());
        });

        // schedule added jobs
        statusUpdate.getAdded().values().forEach(this::schedule);

        // reschedule updated jobs
        statusUpdate.getUpdated().values().forEach(this::reschedule);
    }
    
    /**
     * Compute current status for exchange
     * 
     * @return The current status
     */
    private JobStatusExchangeRequest status(String group, String type) {
        
        // new set for status
        HashMap<String, ZonedDateTime> status = new HashMap<>();
        try {
            // get job keys in the group
            Set<JobKey> keys = this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group));
        
            // process jobs
            for(JobKey jobKey : keys){
                // get the job
                JobDetail job = this.scheduler.getJobDetail(jobKey);
                
                // skip if not available
                if(job == null || !job.getJobDataMap().containsKey(JobConstants.JOB_DEFINITION)){
                    continue;
                }
                
                // the job definition
                JobDefinition jobDefinition = (JobDefinition) job.getJobDataMap().get(JobConstants.JOB_DEFINITION);
                
                // skip jobs of other types
                if(!Str.eqIgnoreCase(type, jobDefinition.getType())){
                    continue;
                }
                
                // record last modified time
                status.put(jobDefinition.getCode(), jobDefinition.getModified());
            }
        }
        catch(SchedulerException error){
            log.error("JobManager: Could not compute status of executing jobs.", error);
        }
                
        return new JobStatusExchangeRequest(group, type, this.config.getCluster(), status);
    }
    
    /**
     * Ensures that agent client has been registered
     * 
     * @param tryCount Number of tries
     * @return Returns true if successful
     */
    private AgentDefinition ensureRegister(int tryCount){
        
        // try several times
        for(int i = 0; i < tryCount; ++i){
            
            // register
            var agent = this.register();
            
            // if successfuly registered
            if(agent != null){
                return agent;
            }
            
            // delay for the next try
            Lang.wait(5000);
        }
        
        return null;
    }
    
    /**
     * Register itself to the scheduler
     */
    private AgentDefinition register(){
        
        // the cluster code
        var cluster = this.config.getCluster();
        
        // the worker
        var worker = this.config.getWorker();
        
        // the agent signal rate
        Duration singalRate = this.config.getWorkerSignalRate();
        
        // now time
        var now = Zdt.utc();
        
        // worker at cluster identity
        var identity = String.format("%s@%s", worker, cluster);
        
        // the agent definition
        var agent = AgentDefinition.builder()
                .id(identity)
                .worker(worker)
                .cluster(cluster)
                .name(identity)
                .supervisor(this.config.isSupervise())
                .health(new AgentHealth(now, AgentActivityType.REGISTER))
                .expectedSignalMinutes(singalRate.toSeconds() / 60.0)
                .registered(now)
                .build();
        
        return this.workerChannel.registration(agent).orElse(null);
    }

    /**
     * Report the health to scheduler
     */
    private void heartbeat() {
                
        // new health info
        var health = new AgentHealth(Zdt.utc(), AgentActivityType.HEARTBEAT);
        
        this.workerChannel.heartbeat(this.agentDefinition.getId(), health);
    }

    /**
     * Initialize the context modules
     */
    private void initializeContext() {
        
        // check if scheduler is given
        if(this.scheduler == null){
            return;
        }
        
        this.initContextModules();
        this.initListeners();
    }

    /**
     * Initialize quartz properties
     * 
     * @return Returns quartz props
     */
    private Properties initProps() {
        
        // props
        var props = new Properties();
        
        // set instance name for quartz scheduler
        props.setProperty("org.quartz.scheduler.instanceName", "JOB_MANAGER_" + this.config.getCluster());
        props.setProperty("org.quartz.scheduler.instanceId", "JOB_MANAGER_" + this.config.getCluster());
        props.setProperty("org.quartz.threadPool.threadCount", this.config.getParallelism().toString());
        
        // other props
        props.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
        props.setProperty("org.quartz.scheduler.jobFactory.class", "org.quartz.simpl.SimpleJobFactory");

        // if JDBC clustering
        if(this.config.getClusterConfiguration().getClusteringType() == ClusteringType.JDBC){
            props.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
            props.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
            props.setProperty("org.quartz.jobStore.dataSource", this.config.getClusterConfiguration().getDataSource());
            props.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");

            // prefix for data store property
            var dsPropPrefix = String.format("org.quartz.dataSource.%s", this.config.getClusterConfiguration().getDataSource());
            
            // data store properties
            props.setProperty(String.format("%s.%s", dsPropPrefix, "driver"), "com.mysql.jdbc.Driver");
            props.setProperty(String.format("%s.%s", dsPropPrefix, "URL"), this.config.getClusterConfiguration().getDataSourceUri());
            props.setProperty(String.format("%s.%s", dsPropPrefix, "user"), this.config.getClusterConfiguration().getDataSourceUsername());
            props.setProperty(String.format("%s.%s", dsPropPrefix, "password"), this.config.getClusterConfiguration().getDataSourcePassword());
            props.setProperty(String.format("%s.%s", dsPropPrefix, "maxConnections"), "10");
        }
        
        return props;
    }
}
