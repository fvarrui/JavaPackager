package io.github.fvarrui.javapackager.utils;

import java.util.Arrays;
import java.util.Optional;

import org.codehaus.plexus.util.StringUtils;

/**
 * Object utils
 */
public class ObjectUtils {
	
	/**
	 * Returns the first non-null object
	 * @param <T> Type
	 * @param values List of objects 
	 * @return First non-null object from values list
	 */
    @SuppressWarnings("unchecked")
	public static <T> T defaultIfNull(final T ... values) {
        Optional<T> value = Arrays.asList(values).stream().filter(v -> v != null).findFirst();
        if (value.isPresent()) return value.get();
        return null;
    }
    
    /**
	 * Returns the first non-blank String
     * @param values List of String
     * @return First non-blank string
     */
	public static String defaultIfBlank(final String ... values) {
        Optional<String> value = Arrays.asList(values).stream().filter(v -> v != null && !StringUtils.isBlank(v)).findFirst();
        if (value.isPresent()) return value.get();
        return null;
    }

}
