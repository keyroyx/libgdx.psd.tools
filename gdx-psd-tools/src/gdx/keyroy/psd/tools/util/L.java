package gdx.keyroy.psd.tools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

//”Ô—‘ (Language)
public class L {
	private static final Properties PROPERTIES = new Properties();

	public static final void load(String file) {
		load(L.class.getResourceAsStream(file));
	}

	public static final void load(File file) {
		try {
			load(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void load(InputStream inputStream) {
		try {
			PROPERTIES.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void print() {
		Enumeration<?> enumeration = PROPERTIES.propertyNames();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			System.out.println(key + " : " + PROPERTIES.getProperty(key));
		}
	}

	public static final String get(String key) {
		String rt = PROPERTIES.getProperty(key.toLowerCase());
		if (rt != null) {
			return rt;
		} else {
			return key;
		}
	}
}
