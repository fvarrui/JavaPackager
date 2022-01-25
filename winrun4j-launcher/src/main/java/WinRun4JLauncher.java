import java.lang.reflect.Method;
import java.util.ResourceBundle;

public class WinRun4JLauncher {

	public static void main(String[] args) throws Exception {
		String className = ResourceBundle.getBundle("winrun4j").getString("main.class");
		Class<?> clazz = Class.forName(className);
		Method method = clazz.getMethod("main", String[].class);
		method.invoke(null, new Object[] { args });	
	}

}
