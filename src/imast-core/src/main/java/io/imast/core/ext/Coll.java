package io.imast.core.ext;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The collection-related extension methods
 * 
 * @author davitp
 */
public class Coll {
   
    /**
     * Combine sets into one
     * 
     * @param <T> The entry type
     * @param sets The sets to combine
     * @return Returns combined set
     */
    public static <T> Set<T> union(Set<T>... sets){
        
        // empty set if not given any
        if(sets == null || sets.length == 0){
            return Set.of();
        }
        
        // new set
        Set<T> all = new HashSet<>();
        
        // combine all
        for(Set<T> set : sets){
            all.addAll(set);
        }
        
        return all;
    }
    
    /**
     * Double for each operator
     * 
     * @param <T1> The type of element in first set
     * @param <T2> The type of element in second set
     * @param first The first set
     * @param second The second set
     * @param action The action to apply
     */
    public static <T1, T2> void doubleForeach(Iterable<T1> first, Iterable<T2> second, BiConsumer<T1, T2> action){
        
        // check if not given
        if(first == null || second == null || action == null){
            return;
        }
        
        // process two loops
        for(T1 elem1 : first){
            for(T2 elem2 : second){
                action.accept(elem1, elem2);
            }
        }
    }
    
    /**
     * Double for each operator
     * 
     * @param <T1> The type of element in first set
     * @param <T2> The type of element in second set
     * @param first The first set
     * @param secondSelector The selector of second set
     * @param action The action to apply
     */
    public static <T1, T2> void doubleForeach(Iterable<T1> first, Function<T1, Iterable<T2>> secondSelector, BiConsumer<T1, T2> action){
        
        // check if not given
        if(first == null || secondSelector == null || action == null){
            return;
        }
        
        // process two loops
        for(T1 elem1 : first){
            
            // second set
            Iterable<T2> second = secondSelector.apply(elem1);
            
            // skip empty set
            if(second == null) {
                continue;
            }
            
            // traverse second set
            for(T2 elem2 : second){
                action.accept(elem1, elem2);
            }
        }
    }
    
    /**
     * Method to convert collection to hash map
     *
     * @param <T> The type of item
     * @param <K> The type of key
     * @param <V> The type of value
     * @param collection The collection to convert
     * @param keySelector The key selector
     * @param valueSelector The value selector
     * @return Hash Map of collection
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection, Function<T, K> keySelector, Function<T, V> valueSelector) {
        HashMap<K, V> result = new HashMap<>();

        for (T item : collection) {
            result.put(keySelector.apply(item), valueSelector.apply(item));
        }

        return result;
    }
    
    /**
     * Checks if collection does not have any value or is null
     * 
     * @param <T> The type of item
     * @param collection The target collection
     * @return Returns true if empty
     */
    public static <T> boolean noItems(Collection<T> collection){
    
        // safety check
        if(collection == null){
            return true;
        }
        
        return collection.isEmpty();
    }
    
    /**
     * Checks if collection have items
     * 
     * @param <T> The type of item
     * @param collection The target collection
     * @return Returns true if not empty
     */
    public static <T> boolean hasItems(Collection<T> collection){
    
        // safety check
        if(collection == null){
            return false;
        }
        
        return !collection.isEmpty();
    }
    
    /**
     * Checks if collection have items
     * 
     * @param <T> The type of item
     * @param collection The target collection
     * @return Returns true if not empty
     */
    public static <T> boolean hasItems(T[] collection){
    
        // safety check
        if(collection == null){
            return false;
        }
        
        return collection.length > 0;
    }
}
