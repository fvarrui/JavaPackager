package io.github.fvarrui.javapackager.utils;

public class Commandline extends org.codehaus.plexus.util.cli.Commandline {

    public String[] getCommandline()
    {
    	return getShellCommandline();
    }
	
}
