package io.imast.core.scheduler;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The result of job query
 * 
 * @author davitp
 * @param <T> The type of job definition
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequestResult<T extends JobDefinition> implements Serializable { 
    
    /**
     * The set of result jobs
     */
    private List<T> jobs;
    
    /**
     * The total number of jobs
     */
    private Long total;
}