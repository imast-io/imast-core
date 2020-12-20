package io.imast.core.ext;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The language extensions
 * 
 * @author davitp
 */
public class Lang {
    
    /**
     * Primitive types for the check
     */
    private static final Set<Class<?>> TERMINAL_CLASSES = getTerminalClasses();

    /**
     * The list of wrapper primitive types
     * 
     * @return list of wrapper primitive types
     */
    @SuppressWarnings("unchecked")
    private static Set<Class<?>> getTerminalClasses() {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(String.class);
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
    
    /**
     * Safely cast the object to a deduced type
     * 
     * @param <T> The type to convert to
     * @param o The object to convert
     * @return Returns cast operation result
     */
    @SuppressWarnings("unchecked")
    public static <T> T safeCast(Object o){
        
        if(o == null){
            return null;
        }
        
        try{
            return (T) o;
        }
        catch(Throwable e){
            // supress any error
            return null;
        }
    }
    
    /**
     * Checks if class is of primitive type
     * 
     * @param clazz The class to test
     * @return Returns if primitive
     */
    public static boolean isTerminal(Class<?> clazz) {
        return clazz.isPrimitive() || TERMINAL_CLASSES.contains(clazz);
    }
    
    
    /**
     * Or operation to choose first not-null value
     * 
     * @param <T> The type of variants
     * @param variants The variants collection
     * @return The first not-null variant
     */
    public static  <T> T or(T... variants){
        
        // loop over variants
        for(T variant : variants){
            
            // if variant is not null return it
            if(variant != null){
                return variant;
            }
        }
        
        // not found
        return null;
    } 
    
    /**
     * Or operation to choose first not-null value
     * 
     * @param <T> The type of variants
     * @param first The first variant
     * @param variants The variants collection
     * @return The first not-null variant
     */
    public static  <T> T or(T first, Supplier<T>... variants){
        
        // check if first is valid
        if(first != null){
            return first;
        }
        
        // loop over variants
        for(Supplier<T> variantSupplier : variants){
            T variant = variantSupplier.get();
            // if variant is not null return it
            if(variant != null){
                return variant;
            }
        }
        
        // not found
        return null;
    }

    /**
     * Checks whether the passed object is t type
     * @param <T> Required class type
     * @param t Required class
     * @param o Passed parameter
     * @return Returns the converted class if it is the instance, else null
     */
    public static <T> T as(Class<T> t, Object o) {
        return t.isInstance(o) ? t.cast(o) : null;
    }
    
    /**
     * Wait for some milliseconds
     * 
     * @param milliseconds The delay milliseconds
     */
    public static void wait(int milliseconds){
        
        try{
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }       
    }
}
