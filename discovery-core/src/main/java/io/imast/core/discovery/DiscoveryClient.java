package io.imast.core.discovery;

/**
 * The discovery client
 * 
 * @author davitp
 */
public interface DiscoveryClient {
    
    /**
     * Get the discovery type
     * 
     * @return Returns the discovery type
     */
    public String getType();
    
    /**
     * Resolves the instance of service
     * 
     * @param service The service to response
     * @return Returns the service instance
     */
    public DiscoveryInstance getInstance(String service);
    
    /**
     * Gets base URL for the given service
     * 
     * @param service The service to resolve
     * @return Returns next base URL
     */
    public String getNextBaseUrl(String service);
    
    /**
     * Gets the API URL
     * 
     * @param service The service 
     * @param api The API
     * @return Returns API URL
     */
    public String getApiUrl(String service, String api);
}