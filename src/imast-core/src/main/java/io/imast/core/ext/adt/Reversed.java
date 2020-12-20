package io.imast.core.ext.adt;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import lombok.AllArgsConstructor;

/**
 * The reverse collection iteration 
 * 
 * @author davitp
 * @param <T> The type of object
 */
@AllArgsConstructor
public class Reversed<T> implements Iterable<T> {
    
    /**
     * The original list
     */
    private final List<T> original;

    /**
     * Implements iterator method using last iterator (reverse)
     * 
     * @return Reverse iterator
     */
    @Override
    public Iterator<T> iterator() {
        // gets the last iterator of list
        final ListIterator<T> i = this.original.listIterator(this.original.size());

        // return new object that implements iterator in reverse way
        return new Iterator<T>() {
            /**
             * Checks if list has next element
             * 
             * @return End indicator
             */
            @Override
            public boolean hasNext() {
                return i.hasPrevious(); 
            }
            
            /**
             * Goes to next element
             * 
             * @return The next element
             */
            @Override
            public T next() { 
                return i.previous(); 
            }
            
            
            @Override
            public void remove() { 
                i.remove(); 
            }
        };
    }

    /**
     * Reverses the collection
     * 
     * @param <T> The type of element
     * @param original The original collection
     * @return Returns the reverse iterable collection
     */
    public static <T> Reversed<T> r(List<T> original) {
        return new Reversed<>(original);
    }
}
