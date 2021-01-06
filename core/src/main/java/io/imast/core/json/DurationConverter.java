package io.imast.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.Duration;

/**
 * GSON serializer/deserializer for Duration type
 */
public class DurationConverter implements JsonSerializer<Duration>, JsonDeserializer<Duration>
{
  
    /**
     * Serialize duration to json string 
     * 
     * @param src The source object
     * @param typeOfSrc The type of source object
     * @param context The serialization context
     * @return Returns JSON primitive for the Duration
     */
    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.toString());
    }


    /**
     * Deserialize Json element to a Duration object
     * 
     * @param json The JSON to convert
     * @param typeOfT The target type
     * @param context The deserialization context
     * @return Returns converted duration
     * @throws JsonParseException 
     */
    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        // if JSON object, consider es (second, nanos) pair
        if(json.isJsonObject()){
            // as object
            var obj = json.getAsJsonObject();
            
            // build duration from seconds and nanos
            return Duration.ofSeconds(obj.get("seconds").getAsLong(), obj.get("nanos").getAsInt());
        }

        return Duration.parse(json.getAsString());
    }
}