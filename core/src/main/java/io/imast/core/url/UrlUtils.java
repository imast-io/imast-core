package io.imast.core.url;

import io.vavr.control.Try;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * The URL Utilities
 * 
 * @author davitp
 */
public class UrlUtils {
   
    /**
     * Pattern to match any HTTP(S) URL
     */
    public static final Pattern ANY_URL_PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    
    /**
     * Check if link represents the absolute URL
     * 
     * @param link The link to test
     * @return Returns true if URL is absolute
     */
    public static boolean isAbsoluteUrl(String link){
        
        // The predicate to check if url is absolute link
        Predicate<String> mathces = Pattern.compile("^(?:[a-z]+:)?//", Pattern.CASE_INSENSITIVE).asMatchPredicate();
        
        // tets to check result
        return mathces.test(link);
    }
    
    /**
     * Handle the link cases
     * 
     * @param baseUri The base URI
     * @param link The link to process
     * @return Returns normalized link
     */
    public static String handleLink(String baseUri, String link) {
        
        // if URL is absolute or not
        if(isAbsoluteUrl(link)){
            return link;
        }
        
        // resolve relative path
        return UrlResolver.resolveUrl(baseUri, link);
    }
    
    /**
     * Exact domain regex pattern (domain or www version of domain)
     * 
     * @param domain The domain to add
     * @return Returns pattern of domain
     */
    public static String exactDomainPattern(String domain){
        
        // replace dot with "exact match of dot"
        String patternify = domain.replace(".", "\\.");
        
        // build pattern of url with exact match
        return String.format("https?:\\/\\/(www\\.)?%s\\/.*", patternify);     
    }
    
    /**
     * Any domain regex pattern
     * 
     * @return Returns pattern of any domain
     */
    public static String anyDomainPattern(){
        
        // build pattern of url with exact match
        return "https?:\\/\\/.*\\/.*";     
    }
    
    /**
     * Checks if given URL is a valid URL
     * 
     * @param url The URL to check
     * @return Returns true if valid URL is given
     */
    public static boolean isHttpUrl(String url){
        return ANY_URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * Get query parameters from the URL
     * 
     * @param url The URL
     * @return Returns query params map
     */
    public static Map<String, List<String>> getQueryParams(String url) {
        
        // result set
        var params = new HashMap<String, List<String>>();
        
        // split into query
        var urlParts = url.split("\\?");
        
        // nothing to do
        if(urlParts.length < 2){
            return params;
        }
        
        // get query
        String query = urlParts[1];
        
        // split query
        var kvPairs = query.split("&");
        
        // for each pair
        for (String param : kvPairs) {
            
            // the param pair
            var pair = param.split("=");
            
            // decode parameter
            String key = Try.of(() -> URLDecoder.decode(pair[0], "UTF-8")).getOrNull();
            
            // could not parse key
            if(key == null){
                continue;
            }
            
            // value is empty by default
            String value = "";
            
            // if given try get decoded value
            if (pair.length > 1) {
                value = Try.of(() -> URLDecoder.decode(pair[1], "UTF-8")).getOrElse("");
            }

            // values of parameter
            var values = params.getOrDefault(key, null);
            
            // check and if no yet parameter value added, add collection
            if (values == null) {
                values = new ArrayList<>();
                params.put(key, values);
            }
            
            // record value
            values.add(value);
        }
        
        return params;
    }
}
