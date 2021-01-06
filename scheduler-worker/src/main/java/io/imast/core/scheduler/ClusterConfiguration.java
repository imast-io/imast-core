package io.imast.core.scheduler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The cluster configuration
 * 
 * @author davitp
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClusterConfiguration {
    
    /**
     * The type of instance
     */
    private String instanceType;
    
    /**
     * The instance cluster
     */
    private String instanceCluster;
    
    /**
     * The instance worker
     */
    private String instanceWorker;
    
    /**
     * The type of clustering
     */
    private ClusteringType clusteringType;
    
    /**
     * The data source
     */
    private String dataSource;
    
    /**
     * The data source URI
     */
    private String dataSourceUri;
    
    /**
     * The data source username
     */
    private String dataSourceUsername;
    
    /**
     * The data source password
     */
    private String dataSourcePassword;
}
