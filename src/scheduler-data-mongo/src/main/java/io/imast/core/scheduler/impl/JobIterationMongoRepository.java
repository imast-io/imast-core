package io.imast.core.scheduler.impl;

import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.imast.core.Coll;
import io.imast.core.Str;
import io.imast.core.mongo.BaseMongoRepository;
import io.imast.core.scheduler.JobIterationRepository;
import io.imast.core.scheduler.iterate.IterationStatus;
import io.imast.core.scheduler.iterate.JobIteration;
import io.imast.core.scheduler.iterate.JobIterationsResult;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;

/**
 * The job iteration repository
 * 
 * @author davitp
 */
public class JobIterationMongoRepository extends BaseMongoRepository<JobIteration> implements JobIterationRepository {

    /**
     * Creates new instance of job iterations mongo repository
     * 
     * @param mongoDatabase The underlying mongo database
     */
    public JobIterationMongoRepository(MongoDatabase mongoDatabase){
        super(mongoDatabase, "job_iterations", JobIteration.class);
    }
    
    /**
     * Gets all the job iterations for the given job
     * 
     * @param jobId The job id to filter
     * @return Returns set of all job iterations 
     */
    @Override
    public List<JobIteration> getAll(String jobId) {
        
        // check if no job id is given get all
        if(Str.blank(jobId)){
            return this.getAll();
        }
        
        return this.toList(this.getCollection().find(eq("jobId", jobId)));
    }

    /**
     * Gets the page of iterations ordered by timestamp (optionally filter by job id and statuses)
     * 
     * @param jobId The job id to filter iterations
     * @param statuses The set of target statuses to lookup
     * @param page The page number
     * @param size The page size
     * @return Returns a page of iterations with given filter
     */
    @Override
    public JobIterationsResult<JobIteration> getPageByTimestamp(String jobId, List<IterationStatus> statuses, int page, int size) {
        var filters = new ArrayList<Bson>();
        
        // add jobId filter if given
        if(!Str.blank(jobId)){
            filters.add(eq("jobId", jobId));
        }
        
        // add target statuses filter if any
        if(Coll.hasItems(statuses)){
            filters.add(in("status", statuses));
        }
        
        // the query to execute
        var query = this.getCollection().find(and(filters)).skip(page * size).limit(size);
        
        // the query to see total number
        var total = this.getCollection().countDocuments(and(filters));
        
        return new JobIterationsResult<>(this.toList(query), total);
    }

    /**
     * Updates (or inserts) a job iteration into the data store
     * 
     * @param jobIteration The job iteration to save
     * @return Returns saved job iteration
     */
    @Override
    public Optional<JobIteration> update(JobIteration jobIteration) {
        return this.upsert(jobIteration, j -> j.getId());
    }

    /**
     * Deletes all the entries before given timestamp
     * 
     * @param timestamp The timestamp to filter
     * @return Returns number of deleted items
     */
    @Override
    public long deleteBefore(ZonedDateTime timestamp) {
        return this.getCollection().deleteMany(lt("timestamp", timestamp)).getDeletedCount();
    }   
    
    /**
     * A special procedure to prepare schema
     * 
     * @return Returns if schema is ready
     */
    @Override
    public boolean prepare() {
        
        // create named index if does not exist
        var result = this.getCollection().createIndex(Indexes.descending("timestamp"), new IndexOptions().name("iteration_by_ts_desc"));
        
        // valid name returned
        return !Str.blank(result);
    }
}
