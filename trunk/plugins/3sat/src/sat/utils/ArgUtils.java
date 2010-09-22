package sat.utils;

public class ArgUtils {

	public static String getString(String[] args, String argument, boolean required) {
		String value= null;
		for (int i= 0; i < args.length; i++) {
			String arg= args[i];
			if (arg.equals(argument) && i < args.length-1)
				value= args[++i];
		}
		if (value == null && required)
			throw new RuntimeException("The "+argument+" argument must be specified");
		return value;
	}

}
