package io.github.fvarrui.helloworld;

public class Main {

	public static void main(String[] args) {
		if (args.length > 0 && args[0].equals("--help")) {
			System.out.println("HelloWorld 1.0.0");
			return;
		}
		HelloWorldFrame.main(args);
	}

}
