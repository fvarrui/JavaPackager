package io.github.fvarrui.javapackager.utils;

import java.util.Arrays;
import java.util.Optional;

import org.codehaus.plexus.util.StringUtils;

public class ObjectUtils {
	
    @SuppressWarnings("unchecked")
	public static <T> T defaultIfNull(final T ... values) {
        Optional<T> value = Arrays.asList(values).stream().filter(v -> v != null).findFirst();
        if (value.isPresent()) return value.get();
        return null;
    }
    
	public static String defaultIfBlank(final String ... values) {
        Optional<String> value = Arrays.asList(values).stream().filter(v -> v != null && !StringUtils.isBlank(v)).findFirst();
        if (value.isPresent()) return value.get();
        return null;
    }
    
    public static void main(String[] args) {
		Object o = defaultIfBlank(null, "   ", "hola", null); 
		System.out.println(o);
	}

}
