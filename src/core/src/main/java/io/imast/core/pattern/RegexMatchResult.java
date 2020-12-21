package io.imast.core.pattern;

import io.imast.core.Coll;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The match result of regex
 * 
 * @author davitp
 */
@AllArgsConstructor
public class RegexMatchResult {
    
    /**
     * Indicates that pattern successfully matched
     */
    @Getter
    private final boolean success;
    
    /**
     * The matched groups
     */
    private final List<String> groups;
    
    /**
     * Get the group with index i
     * 
     * @param i The group index
     * @return Returns group value or null
     */
    public String group(int i){
        
        // if group present return
        if(i < this.groups.size()){
            return this.groups.get(i);
        }
        
        // no group
        return null;
    }
    
    /**
     * The number of groups
     * 
     * @return Returns number of groups
     */
    public int groupCount(){
        return Coll.noItems(this.groups) ? 0 : this.groups.size() - 1;
    }
    
    /**
     * Number of all groups including special one
     * 
     * @return Returns the number of all groups including 0
     */
    public int allGroups(){
        return this.groups.size();
    }
}
