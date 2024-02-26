package io.github.fvarrui.javapackager.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.SystemUtils;

public class CharsetUtil {
	
	private static Charset commandLineCharset;
	
    public static Charset getCommandLineCharset(){
    	if (commandLineCharset == null) {
	    	if (SystemUtils.IS_OS_WINDOWS) {
		        commandLineCharset = chcp();
	    	} else {
	    		commandLineCharset = Charset.defaultCharset();
	    	}
    	}
        return commandLineCharset;
    }
    
    private static Charset chcp() {
        try{
            String result = CommandUtils.run("cmd", "/k", "chcp");	        	
            String code = StringUtils.find("\\d+", result);
            Logger.debug("'chcp' code found: " + code);
            switch (code){
            	case "37": 
                case "037": return Charset.forName("IBM037");
                case "936": return Charset.forName("gb2312");
                case "950": return Charset.forName("big5");
                case "1145": return Charset.forName("IBM01145");
                case "1200": return StandardCharsets.UTF_16;
                case "51936": return Charset.forName("EUC-CN");
                case "65001": return StandardCharsets.UTF_8;
            }
        } catch (Exception e){
        	// do nothing
        }
        return Charset.defaultCharset();
    }

}
