package io.imast.core.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.ReplaceOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.bson.Document;

/**
 * The base mongo repository
 * 
 * @author davitp
 * @param <T> The primary type of repository
 */
public class BaseMongoRepository <T> {
   
    /**
     * The target collection name
     */
    private final String collectionName;
    
    /**
     * The mongo database instance
     */
    private final MongoDatabase mongoDatabase;
    
    /**
     * The target class instance
     */
    private final Class<T> clazz;
    
    /**
     * Creates new instance of mongo repository
     * 
     * @param database The target database
     * @param name The collection name
     * @param clazz The type class instance
     */
    public BaseMongoRepository(MongoDatabase database, String name, Class<T> clazz){
        this.collectionName = name;
        this.mongoDatabase = database;
        this.clazz = clazz;
    }
    
    /**
     * Gets the mongo collection for target type
     * 
     * @return Returns mongo collection
     */
    protected MongoCollection<T> getCollection(){
        return this.mongoDatabase.getCollection(this.collectionName, this.clazz);
    }
    
    /**
     * Gets all items from target collection
     * 
     * @return Returns set of all job definitions
     */
    public List<T> getAll(){
        return this.toList(this.getCollection().find());
    }
    
    /**
     * Gets the item by identifier
     * 
     * @param id The item id
     * @return Returns item if found
     */
    public Optional<T> getById(String id){
        
        // gets the item by id
        var item = this.getCollection().find(eq("_id", id)).first();
        
        // return if valid
        return Optional.ofNullable(item);
    }
    
    /**
     * Upserts (inserts or updates) the entry into a mongo repository
     * 
     * @param item The item to save
     * @param idSelector The ID selector
     * @return Returns saved item
     */
    public Optional<T> upsert(T item, Function<T, String> idSelector){
                
        // insert a single item or update
        var result = this.getCollection().replaceOne(eq("_id", idSelector.apply(item)), item, new ReplaceOptions().upsert(true));
        
        // nothing matched, should not happen
        if(result.getModifiedCount() == 0 && result.getUpsertedId() == null){
            return Optional.empty();
        }
        
        return Optional.of(item);
    }
    
    /**
     * Inserts the entry into a mongo repository
     * 
     * @param item The item to save
     * @return Returns saved item
     */
    public Optional<T> insert(T item){
        
        // try insert one
        var inserted = this.getCollection().insertOne(item);

        // the inserted id
        var id = inserted.getInsertedId();
        
        if(id == null){
            return Optional.empty();
        }

        return this.getById(id.asObjectId().toString());
    }
    
    /**
     * Deletes entity by identifier
     * 
     * @param id The id of document to delete
     * @return Returns deleted document if any
     */
    public Optional<T> deleteById(String id){
        
        // get existing item
        var existing = this.getById(id);
        
        // check if exists to delete
        if(!existing.isPresent()){
            return Optional.empty();
        }
        
        // try delete
        var deleted = this.getCollection().deleteOne(eq("_id", id));
        
        // nothing was deleted
        if(deleted.getDeletedCount() == 0){
            return Optional.empty();
        }
        
        return existing;
    }
    
    /**
     * Deletes all the documents from given collection
     * 
     * @return Returns number of deleted items
     */
    public long deleteAll(){
        return this.getCollection().deleteMany(new Document()).getDeletedCount();
    }
    
    /**
     * Gets distinct values of a given field
     * 
     * @param <TVal> The type of value
     * 
     * @param fieldName The field name
     * @param clazz The field type
     * @return Returns list of distinct values
     */
    protected <TVal> List<TVal> distinctScalar(String fieldName, Class<TVal> clazz){
        return this.getCollection().distinct(fieldName, clazz).into(new ArrayList<>());
    }
    
    /**
     * Converts iterable entity into list
     * 
     * @param iterable The source iterable 
     * @return Returns list representation
     */
    protected List<T> toList(FindIterable<T> iterable){
        
        // creates new result set
        var result = new ArrayList<T>();
        
        iterable.forEach(item -> result.add(item));
        
        return result;
    }
}
