package io.imast.core.pattern;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * The extensions for regular expressions
 * 
 * @author davitp
 */
public class Rgx {
   
    /**
     * Matches the pattern to groups
     * 
     * @param pattern The pattern to match
     * @param target The target to match
     * @return Returns if matched with groups
     */
    public static RegexMatchResult match(Pattern pattern, String target){
        
        // the matcher of pattern on a text
        var matcher = pattern.matcher(target);
        
        // groups
        var groups = new ArrayList<String>();
        
        // no matches
        if(!matcher.find()){
            return new RegexMatchResult(false, groups);
        }
        
        // process groups
        for(var i = 0; i <= matcher.groupCount(); ++i){
            groups.add(matcher.group(i));
        }
        
        // success
        return new RegexMatchResult(true, groups);
    }
}
