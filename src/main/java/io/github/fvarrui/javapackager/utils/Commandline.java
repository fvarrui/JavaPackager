package io.github.fvarrui.javapackager.utils;

import java.io.File;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * Commandline  
 */
public class Commandline extends org.codehaus.plexus.util.cli.Commandline {
	
	public Commandline() {
		super();
		getShell().setQuotedArgumentsEnabled(false);		
		if (SystemUtils.IS_OS_WINDOWS) {
			getShell().setShellArgs(new String[] { "/s", "/c" } );
		}		
	}

    public String[] getCommandline()
    {
    	return getShellCommandline();
    }
    
	public void createArguments(Object... arguments) {
		for (Object argument : arguments) {
			
			if (argument == null)
				continue;

			// expands collections
			if (argument instanceof Collection) {
				createArguments(((Collection<?>) argument).toArray(new Object[0]));
				continue;
			}

			// expands array
			if (argument.getClass().isArray()) {
                createArguments((Object[])argument);
				continue;
			}

			// process file argument
			if (argument instanceof File) {
				
				File argFile = (File) argument;
				if (argFile.getName().contains("*")) {
					argument = org.codehaus.plexus.util.StringUtils.quoteAndEscape(argFile.getParentFile().getAbsolutePath(), '\"') + File.separator + argFile.getName();
				} else {
					argument = ((File) argument).getAbsolutePath();
				}
				
			}

			String arg = argument.toString().trim(); 
			if (!arg.contains("\"") && StringUtils.containsWhitespace(arg)) {
				arg = org.codehaus.plexus.util.StringUtils.quoteAndEscape(arg, '\"');
			}
			this.createArg().setValue(arg);

		}
	}
	
	public String getCommandLineAsString() {
		return StringUtils.join(getCommandline(), " ");		
	}
	
}
