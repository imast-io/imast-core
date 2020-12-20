package io.imast.core.json;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.Duration;

/**
 * The JSON Conversion utility
 * 
 * @author davitp
 */
public class Json {
    /**
     * Gets the date format
     * 
     * @return Returns date format
     */
    private static String getDateFormat(){
        return "yyyy-MM-dd'T'HH:mm:ssZ";
    }
    
    /**
     * Gets the GSON builder with all required pre-registrations
     * 
     * @return Ready to use Gson Builder
     */
    private static GsonBuilder getBuilder(){
        return Converters.registerAll(new GsonBuilder()).registerTypeAdapter(Duration.class, new DurationConverter());
    }
    
    /**
     * Convert the object to JSON string
     *
     * @param <T> The generic parameter
     * @param object The object to serialize
     * @return Returns serialized string
     */
    public static <T> String to(T object) {
        // create json string
        return getBuilder().setPrettyPrinting().setDateFormat(getDateFormat()).enableComplexMapKeySerialization().serializeNulls().create().toJson(object);
    }
       
    /**
     * Import JSON file in to the object
     *
     * @param <T> The T parameter for input
     * @param json The JSON text
     * @param classType The class type
     * @return The object
     */
    public static <T> T from(String json, Class<T> classType) {
        // return object
        return getBuilder().setDateFormat(getDateFormat()).enableComplexMapKeySerialization().create().fromJson(json, classType);
    }
    
    /**
     * Import JSON file in to the object
     *
     * @param <T> The T parameter for input
     * @param jsonReader The JSON reader
     * @param classType The class type
     * @return The object
     */
    public static <T> T from(Reader jsonReader, Class<T> classType) {
        // return object
        return getBuilder().setDateFormat(getDateFormat()).enableComplexMapKeySerialization().create().fromJson(jsonReader, classType);
    }
    
    /**
     * Import JSON file in to the object
     *
     * @param <T> The T parameter for input
     * @param json The JSON text
     * @param classType The class type
     * @return The object
     */
    public static <T> T from(String json, Type classType) {
        // return object
        return getBuilder().setDateFormat(getDateFormat()).enableComplexMapKeySerialization().create().fromJson(json, classType);
    }
    
    /**
     * A fast adapter method to change types with exact same schema
     * 
     * @param <From> The source type
     * @param <To> The target type
     * @param object The object
     * @param targetType The target type
     * @return 
     */
    public static <From, To> To adapt(From object, Class<To> targetType){
        if(object == null) return null;
        
        return from(to(object), targetType);
    }
    
    /**
     * A fast adapter method to change types with exact same schema
     * 
     * @param <From> The source type
     * @param <To> The target type
     * @param object The object
     * @param targetType The target type
     * @return 
     */
    public static <From, To> To adapt(From object, Type targetType){
        if(object == null) return null;
        
        return from(to(object), targetType);
    }
}
