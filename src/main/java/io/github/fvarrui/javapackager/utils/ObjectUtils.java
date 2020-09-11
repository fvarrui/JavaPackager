package io.github.fvarrui.javapackager.utils;

import java.util.Arrays;
import java.util.Optional;

public class ObjectUtils {
	
    @SuppressWarnings("unchecked")
	public static <T> T defaultIfNull(final T ... values) {
        Optional<T> value = Arrays.asList(values).stream().filter(v -> v != null).findFirst();
        if (value.isPresent()) return value.get();
        return null;
    }
    
    public static void main(String[] args) {
		Object o = defaultIfNull(null, null, 5, null); 
		System.out.println(o);
	}

}
