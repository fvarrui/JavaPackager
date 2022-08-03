package io.github.fvarrui.helloworld;

import java.lang.module.ModuleDescriptor;

public class Main {

	public static void main(String[] args) {
		if (args.length > 0) {
			String argument = args[0];
			switch (argument) {
			case "--version":
				version();
				return;
			case "--module-info":
				moduleInfo();
				return;
			}
		}
		HelloWorldFrame.main(args);
	}

	private static void version() {
		System.out.println("HelloWorld 1.0.0");
	}

	private static void moduleInfo() {
		ModuleDescriptor descriptor = Main.class.getModule().getDescriptor();
		if (descriptor != null) {
			System.out.println("Modular version of HelloWorldMaven:");
			System.out.println("- Module name : " + descriptor.name());
			System.out.println("- Requires    : " + descriptor.requires());			
			System.out.println("- Exports     : " + descriptor.exports());			
		} else {
			System.out.println("Non modular version of HelloWorldMaven!");
		}
	}

}
