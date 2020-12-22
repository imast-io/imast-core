package io.imast.core.client;

import java.util.HashMap;

/**
 * The client registry module
 * 
 * @author davitp
 */
public class ClientRegistry {
   
    /**
     * The registry for clients
     */
    private final HashMap<String, BaseClient> clients;
    
    /**
     * Creates new instance for clients registry
     */
    public ClientRegistry(){
        this.clients = new HashMap<>();
    }
    
    /**
     * Register the client with name
     * 
     * @param name The name of client
     * @param client The client to register
     */
    public void add(String name, BaseClient client){
        this.clients.put(name, client);
    }
    
    /**
     * Gets the client with a name
     * @param <T> The expected type
     * @param name The name of client
     * @return Returns client of the given type
     */
    public <T extends BaseClient> T get(String name){
        return (T)this.clients.get(name);
    }
}
