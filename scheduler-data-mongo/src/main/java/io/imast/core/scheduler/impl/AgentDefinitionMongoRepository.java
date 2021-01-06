package io.imast.core.scheduler.impl;

import com.mongodb.client.MongoDatabase;
import io.imast.core.mongo.BaseMongoRepository;
import io.imast.core.scheduler.AgentDefinitionRepository;
import io.imast.core.scheduler.agent.AgentDefinition;
import java.util.Optional;

/**
 * The agent definition repository
 * 
 * @author davitp
 */
public class AgentDefinitionMongoRepository extends BaseMongoRepository<AgentDefinition> implements AgentDefinitionRepository{

    /**
     * Creates new instance of agent definitions mongo repository
     * 
     * @param mongoDatabase The underlying mongo database
     */
    public AgentDefinitionMongoRepository(MongoDatabase mongoDatabase){
        super(mongoDatabase, "agent_definitions", AgentDefinition.class);
    }
    
    /**
     * Updates (inserts or updates) a agent definition into the data store
     * 
     * @param agentDefinition The agent definition to save
     * @return Returns saved agent definition
     */
    @Override
    public Optional<AgentDefinition> update(AgentDefinition agentDefinition) {
        return this.upsert(agentDefinition, a -> a.getId());
    }   

    /**
     * A special procedure to prepare schema
     * 
     * @return Returns if schema is ready
     */
    @Override
    public boolean prepare() {
        return true;
    }
}
