package io.imast.core.ext;

import io.vavr.control.Try;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

/**
 * The String extensions
 * 
 * @author davitp
 */
public class Zdt {
    
    /**
     * The set of valid timezones
     */
    public static final Set<String> VALID_TIMEZONES = Set.of(TimeZone.getAvailableIDs());
    
    /**
     * The plus infinity of zoned date time. Value corresponds to the biggest
     * time
     */
    public static ZonedDateTime PLUS_INFINITY = Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC);
       
    /**
     * Get the current time in UTC
     * 
     * @return Returns the current time in UTC
     */
    public static ZonedDateTime utc(){
       return ZonedDateTime.now(ZoneOffset.UTC);
    }
    
    /**
     * The now time in the given timezone
     * 
     * @param timezone The timezone
     * @return Returns current time in the given time zone
     */
    public static ZonedDateTime now(String timezone){
        return Try.of(() -> ZonedDateTime.now(ZoneId.of(timezone))).getOrNull();
    }
    
    /**
     * Get the given time in UTC
     * 
     * @param time The time to convert
     * @return Returns the given time in UTC
     */
    public static ZonedDateTime utc(ZonedDateTime time){
        
        // safety check
        if(time == null){
            return null;
        }
        
        return ZonedDateTime.ofInstant(time.toInstant(), ZoneOffset.UTC);
    }
    
    /**
     * Zoned Date Time as Date
     * 
     * @param zdt The zoned date time
     * @return The UTC-based Date instance
     */
    public static Date toDate(ZonedDateTime zdt){
        
        // safety check
        if(zdt == null){
            return null;
        }
        
        return Date.from(zdt.toInstant());
    }
    
    /**
     * First if first and second times are same point in time
     * @param first The first time
     * @param second The second time
     * @return Returns true if same
     */
    public static boolean sameTime(ZonedDateTime first, ZonedDateTime second){
        
        // if one is null then not equal
        if(first == null || second == null){
            return false;
        }
        
        // if instant is same time
        return first.toInstant().equals(second.toInstant());
    }

    /**
     * The UTC converter for Date type
     * 
     * @param date The date to convert
     * @return Returns zoned date time of date in UTC
     */
    public static ZonedDateTime utc(Date date) {
        
        // handle null date
        if(date == null){
            return null;
        }
        
        return date.toInstant().atZone(ZoneOffset.UTC);
    }

    /**
     * Checks if valid timezone
     * 
     * @param timezone The time zone
     * @return Returns true if valid
     */
    public static boolean validTimezone(String timezone) {
        
        // check timezone 
        if(timezone == null){
            return false;
        }
        
        // is valid
        return VALID_TIMEZONES.contains(timezone);
    }
    
    /**
     * Safely parse ISO date time 
     * 
     * @param timeString The time string
     * @return Returns zoned date time 
     */
    public static ZonedDateTime safeParse(String timeString){
        
        if(timeString == null || timeString.isBlank()){
            return null;
        }
        
        // try parse time string
        try{ return ZonedDateTime.parse(timeString); }
        catch(Throwable e){}
        
        return null;
    }
    
    /**
     * Get minutes from duration
     *
     * @param duration The duration
     * @return Returns number of minutes
     */
    public static Double toMinutes(Duration duration) {
        if (duration == null) {
            return null;
        }
        return duration.getSeconds() / 60.0;
    }
    
    /**
     * Gets duration from minutes
     *
     * @param duration The duration in minutes
     * @return Returns duration
     */
    public static Duration fromMinutes(Double duration) {
        if (duration == null) {
            return null;
        }

        return Duration.ofSeconds((long) (duration * 60.0));
    }
    
    /**
     * Gets duration from hours
     *
     * @param duration The duration in hours
     * @return Returns duration
     */
    public static Duration fromHours(Double duration) {
        if (duration == null) {
            return null;
        }

        return Duration.ofSeconds((long) (duration * 60.0 * 60.0));
    }
    
    /**
     * Gets duration from days
     *
     * @param duration The duration in days
     * @return Returns duration
     */
    public static Duration fromDays(Double duration) {
        if (duration == null) {
            return null;
        }

        return Duration.ofSeconds((long) (duration * 60.0 * 60.0 * 24));
    }

    /**
     * Get hours from duration
     *
     * @param duration The duration
     * @return Returns number of hours
     */
    public static Double toHours(Duration duration) {
        if (duration == null) {
            return null;
        }
        return duration.getSeconds() / 3600.0;
    }
    
    /**
     * Divides left-hand operand by right-hand operand and returns remainder
     *
     * @param l The left operand
     * @param r The right operand
     * @return The result in duration
     */
    public static Duration mod(Duration l, Duration r) {
        return Duration.ofMillis(l.toMillis() % r.toMillis());
    }

    /**
     * Divides left-hand operand by right-hand operand
     *
     * @param d The duration
     * @param c The scalar coefficient
     * @return The result in duration
     */
    public static Duration divide(Duration d, long c) {
        return Duration.ofMillis(d.toMillis() / c);
    }

    /**
     * Divides left-hand operand by right-hand operand and ceil the result
     *
     * @param d The duration
     * @param c The duration
     * @return The result of divide after ceiling
     */
    public static long divideCeiling(Duration d, Duration c) {
        return d.toMillis() / c.toMillis() + (d.toMillis() % c.toMillis() == 0 ? 0 : 1);
    }

    /**
     * Multiply left-hand operand with right-hand operand
     *
     * @param d The duration
     * @param c The scalar coefficient
     * @return The result in duration
     */
    public static Duration multiply(Duration d, long c) {
        return Duration.ofMillis(d.toMillis() * c);
    }

    /**
     * Difference between dates
     *
     * @param l Left operand
     * @param r Right operand
     * @return Returns time span
     */
    public static Duration minus(Date l, Date r) {
        return Duration.ofMillis(l.getTime() - r.getTime());
    }

    /**
     * Difference between date and duration
     *
     * @param date The date
     * @param duration The duration
     * @return Returns new date
     */
    public static Date minus(Date date, Duration duration) {
        return new Date(date.getTime() - duration.toMillis());
    }

    /**
     * Add duration to date
     *
     * @param date The date
     * @param duration The duration
     * @return Returns new date
     */
    public static Date plus(Date date, Duration duration) {
        return new Date(date.getTime() + duration.toMillis());
    }
    
    /**
     * Get array from zoned date time
     * 
     * @param times The times
     * @return Returns array of zoned date times
     */
    public static ZonedDateTime[] array(ZonedDateTime... times){
        return times;
    }
}
