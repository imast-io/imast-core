package io.imast.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The String extensions
 * 
 * @author davitp
 */
public class Str {
    
    /**
     * The line separator constant
     */
    public static final String NEWLINE = System.lineSeparator();
    
    /**
     * The empty string
     */
    public static final String EMPTY = "";
    
    /**
     * The null string
     */
    public static final String NULL = null;
    
    /**
     * An empty list
     */
    public static final List<String> EMPTY_LIST = Arrays.asList();
    
    /**
     * An empty array
     */
    public static final String[] EMPTY_ARRAY = new String[0];
    
    /**
     * Convert to array
     * 
     * @param strs The list of strings
     * @return The array of strings
     */
    public static String[] toArray(String... strs){
        return strs;
    }
    
    /**
     * Use string as stream
     * 
     * @param str The string to stream
     * @param encoding The target encoding
     * @return Returns stream of string
     */
    public static InputStream asStream(String str, String encoding){
        
        // null if string is not given
        if(str == null){
            return null;
        }
        
        // use UTF-8 as default
        if(encoding == null){
            encoding = "UTF-8";
        }
        
        try{
            // try build stream with given encoding
            return new ByteArrayInputStream(str.getBytes(encoding));
        }
        catch(UnsupportedEncodingException e){
            return null;
        }
    }
    
    /**
     * Use string as stream
     * 
     * @param str The string to stream
     * @return Returns stream of string
     */
    public static InputStream asStream(String str){
        return asStream(str, null);
    }
    
    /**
     * Check if string is blank
     * 
     * @param str The string to check
     * @return Returns true if string is blank
     */
    public static boolean blank(String str){
        
        // null string is considered as blank
        if(str == null){
            return true;
        }
        
        return str.isBlank();
    }
    
    /**
     * Safely stringify the object
     * 
     * @param obj The object to stringify
     * @return Returns string value of object or null
     */
    public static String toString(Object obj){
        
        // null string is considered as blank
        if(obj == null){
            return null;
        }
        
        return obj.toString();
    }
    
    /**
     * Append line to string builder
     * 
     * @param builder The builder 
     * @param line The line to append
     * @return Returns the builder
     */
    public static StringBuilder appendLine(StringBuilder builder, String line){
        
        // append line
        builder.append(line);
        
        // append new line
        builder.append(NEWLINE);
        
        return builder;
    }
    
    /**
     * Append text to string builder
     * 
     * @param builder The builder 
     * @param text The text to append
     * @return Returns the builder
     */
    public static StringBuilder append(StringBuilder builder, String text){
        
        // append line
        builder.append(text);
        
        return builder;
    }
    
    /**
     * Get longest text from given set
     * 
     * @param texts The text to check
     * @return Returns the longest text
     */
    public static String longest(String... texts){
        
        // maximum length text
        String max = null;
        
        // check texts
        for(var text : texts){
            
            // skip empty
            if(Str.blank(text)){
                continue;
            }
            
            // if first try
            if(max == null){
                max = text;
            }
            
            // check if text is longer than maximum set new string
            if(max != null && text.length() > max.length()){
                max = text;
            }
        }
        
        return max;
    }
    
    /**
     * Append text to string builder
     * 
     * @param builder The builder 
     * @param texts The texts to append
     * @return Returns the builder
     */
    public static StringBuilder append(StringBuilder builder, String... texts){
        
        // append texts
        Arrays.stream(texts).forEach(text -> builder.append(text));
        
        return builder;
    }
    
    /**
     * Check if string is blank
     * 
     * @param str The string to check
     * @return Returns true if string is blank
     */
    public static boolean blank(Object str){
        
        // null string is considered as blank
        if(str == null){
            return true;
        }
        
        // consider blank if not string
        if(!(str instanceof String)){
            return true;
        }
        
        return ((String)str).isBlank();
    }
 

    /**
     * Generate a random string
     * 
     * @param length The length of string
     * @return Returns a randomly generated string
     */
    public static String random(int length) 
    { 
        // check if negative
        if(length < 1){
            return "";
        }
  
        // chose a Character random from this String 
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ012345678abcdefghijklmnopqrstuvxyz"; 
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(length); 
  
        // generate symbols
        for (int i = 0; i < length; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index = (int)(alphaNumericString.length() * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(alphaNumericString.charAt(index)); 
        } 
  
        return sb.toString(); 
    } 
    
    /**
     * Encodes string to base64
     * 
     * @param str The string to encode
     * @return Returns encoded string
     */
    public static String toBase64(String str){
        
        // null check
        if(str == null){
            return null;
        }
        
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Encodes string to base64
     * 
     * @param bytes The bytes to encode
     * @return Returns encoded string
     */
    public static String toBase64(byte[] bytes){
        
        // null check
        if(bytes == null){
            return null;
        }
        
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Decodes string from base64
     * 
     * @param str The string to decode
     * @return Returns decoded string
     */
    public static String fromBase64(String str){
        // null check
        if(str == null){
            return null;
        }
        
        return new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8);
    }
    
    /**
     * Get from Base64 to byte stream
     * 
     * @param str The string to get bytes
     * @return Returns bytes from base64 string
     */
    public static byte[] fromBase64ToBytes(String str){
        // null check
        if(str == null){
            return null;
        }
        
        return Base64.getDecoder().decode(str);
    }
    
    /**
     * Decodes string from base64
     * 
     * @param bytes The bytes to decode
     * @return Returns decoded string
     */
    public static String fromBase64(byte[] bytes){
        // null check
        if(bytes == null){
            return null;
        }
        
        return new String(Base64.getDecoder().decode(bytes), StandardCharsets.UTF_8);
    }
    
    /**
     * Encodes string to Y64
     * 
     * @param str The string to encode
     * @return Returns encoded string
     */
    public static String toY64(String str){
        // null check
        if(str == null){
            return null;
        }

        return toBase64(str).replace('=', '-').replace('+', '.').replace('/', '_');
    }
    
    /**
     * Decodes string from Y64
     * 
     * @param str The string to decode
     * @return Returns decoded string
     */
    public static String fromY64(String str){
        // null check
        if(str == null){
            return null;
        }
        
        return fromBase64(str).replace('-', '=').replace('.', '+').replace('_', '/');
    }
    
    /**
     * Gets the string representation of the object
     * And default value in case of null
     * 
     * @param <T> The type of object
     * @param <V> The value type
     * @param value The value
     * @param selector The property selector
     * @param defaultValue The value to get in case of null 
     * @return Returns string representation
     */
    public static <T, V> String stringfy(T value, Function<T, V> selector, String defaultValue){
        if(value == null){
            return defaultValue;
        }
        
        V val = selector.apply(value);
        
        if(val == null){
            return defaultValue;
        }
        
        return val.toString();
    }    
    
    /**
     * Check string equality
     * 
     * @param left The left string
     * @param right The right string
     * @return Returns if equal
     */
    public static boolean eq(String left, String right){
        if(left == null) {
            return right == null;
        }
        
        return left.equals(right);
    }
    
    /**
     * Check string equality
     * 
     * @param left The left string
     * @param right The right string
     * @return Returns if equal
     */
    public static boolean eqIgnoreCase(String left, String right){
        if(left == null) {
            return right == null;
        }
        
        return left.equalsIgnoreCase(right);
    }
    
    /**
     * Check if string contains without considering case
     * 
     * @param left The left string
     * @param right The right string
     * @return Returns if contains
     */
    public static boolean containsIgnoreCase(String left, String right){
        if(left == null) {
            return right == null;
        }
        
        // does not contain null
        if(right == null){
            return false;
        }
        
        return left.toUpperCase().contains(right.toUpperCase());
    }
    
    /**
     * Shorten the input text
     * 
     * @param text The text to shorten
     * @param length The length of required text
     * @param more The more punctuation
     * @return Returns the shortened text
     */
    public static String shorten(String text, int length, String more){
        
        // check for null or empty
        if(Str.blank(text)){
            return "";
        }
        
        // if text is shorter that required use it as is
        if(text.length() < length){
            return text;
        }
        
        // use "..." by default
        more = Str.blank(more) ? "..." : more;
        
        // return shortened text
        return String.format("%s%s", text.substring(0, length), more);
    }
    
    /**
     * Collection to array conversion
     * 
     * @param collection The target collection
     * @return Returns array of elements, null if collection is null
     */
    public static String[] toArray(Collection<String> collection){
        
        // null check
        if(collection == null){
            return null;
        }
        
        // empty check
        if(collection.isEmpty()){
            return EMPTY_ARRAY;
        }
        
        // array of correct size
        var array = new String[collection.size()];
        
        // current index
        var index = 0;
        
        // on each element in collection
        for(var element : collection){
            array[index++] = element;
        }
        
        return array;
    }
    
    /**
     * Decode given hex-string into by byte array
     * 
     * @param hex The hex string
     * @return The decoded byte array
     */
    public static byte[] decodeHex(String hex) {
        
        // safety check
        if(Str.blank(hex)){
            return null;
        }
        
        // normalize string
        hex = hex.trim();
        
        // get length of hex
        var length = hex.length();
        
        // should have 2x length
        if(length % 2 != 0){
            return null;
        }
        
        // prepare result array
        var data = new byte[length / 2];
        
        // encode every two digits into single byte
        for (var i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
        }
        
        return data;
    }
    
    /**
     * GZIP compress the input source string
     * 
     * @param source The source to compress
     * @return Returns compressed base64 string
     */
    public static String gzip(String source) {
        
        // nothing to do
        if(blank(source)){
            return source;
        }
        
        // create a byte stream
        var byteStream = new ByteArrayOutputStream();
        
        // create gzip stream and write into byte stream
        try (var gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(source.getBytes());
        } catch (IOException ex) {
            return null;
        }
        
        return toBase64(byteStream.toByteArray());
    }
    
    /**
     * Decompress GZIP the compressed base64 string
     * 
     * @param compressed The compressed string to decompress
     * @return Returns decompressed base64 string
     */
    public static String ungzip(String compressed) {
        
        // nothing to do
        if(blank(compressed)){
            return compressed;
        }
        
        // decode base64 formatted compressed string into bytes
        var bytes = Base64.getDecoder().decode(compressed);
                
        // create gzip stream and write into byte stream
        try (var gzipStream = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            
            // read result
            var result = gzipStream.readAllBytes();
            
            // handle null
            if(result == null){
                return null;
            }
            
            return new String(result, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return null;
        }
    }
}
