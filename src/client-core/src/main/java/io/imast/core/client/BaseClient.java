package io.imast.core.client;

import io.imast.core.Str;
import io.imast.core.discovery.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;

/**
 * The base client for Wisdom services
 * 
 * @author davitp
 */
@Slf4j
public class BaseClient {
    
    /**
     * The auth header key
     */
    protected final String AUTH_HEADER = "Authorization";
    
    /**
     * The content-type header key
     */
    protected final String CONTENT_TYPE_HEADER = "Content-Type";
    
    /**
     * The JSON media value for content header
     */
    protected final String CLIENT_HEADER = "X-Imast-Client";
    
    /**
     * The JSON media value for content header
     */
    protected final String JSON_MEDIA = "application/json";    
    
    /**
     * The client name
     */
    protected final String client;
    
    /**
     * The service name
     */
    protected final String service;
    
    /**
     * The wisdom discovery for client
     */
    protected final DiscoveryClient discovery;
    
    /**
     * The base client for Wisdom Services
     * 
     * @param client The client name
     * @param service The service name
     * @param discovery The discovery module
     */
    public BaseClient(String client, String service, DiscoveryClient discovery){
        this.client = client;
        this.service = service;
        this.discovery = discovery;
    }
    
    /**
     * Gets the API URL based on discovery
     * 
     * @param api The API to call
     * @return Returns base URL for API
     */
    public String getApiUrl(String api){
        return this.discovery.getApiUrl(this.service, api);
    }
    
    /**
     * The authentication token specification
     * 
     * @param token The token
     * @return Returns header
     */
    protected HeaderSpec auth(String token){
        return new HeaderSpec(AUTH_HEADER, String.format("Bearer %s", token));
    }
    
    /**
     * The content type header specification
     * 
     * @param contentType The content type
     * @return Returns header
     */
    protected HeaderSpec contentType(String contentType){
        return new HeaderSpec(CONTENT_TYPE_HEADER, contentType);
    }
    
    /**
     * The basic authentication specification
     * 
     * @param username The username
     * @param password The password
     * @return Returns header
     */
    protected HeaderSpec basicauth(String username, String password){
        return new HeaderSpec(AUTH_HEADER, String.format("Basic %s", Str.toBase64(String.format("%s:%s", username, password))));
    }
}
