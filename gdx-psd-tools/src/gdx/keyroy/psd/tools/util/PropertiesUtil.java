package gdx.keyroy.psd.tools.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class PropertiesUtil {
	public static final void save(Class<?> clazz) throws Exception {
		save(clazz, new File(clazz.getSimpleName().toLowerCase()));
	}

	public static final void save(Class<?> clazz, File file) throws Exception {
		Properties properties = PropertiesUtil.toProperties(clazz);
		FileOutputStream outputStream = new FileOutputStream(file);
		// ≤ª≈≈–Ú
		// properties.store(outputStream, null);
		// ≈≈–Ú±£¥Ê
		store(properties, outputStream, null);
		outputStream.close();
	}

	public static final Properties toProperties(Class<?> clazz) {
		Properties properties = new Properties();
		List<Pack> list = getFields();
		for (Pack pack : list) {
			if (pack.getVal() != null) {
				properties.put(pack.getKey(), pack.getVal());
			}
		}
		return properties;
	}

	protected static final List<Pack> getFields() {
		List<Pack> list = new ArrayList<Pack>();

		Class[] classes = ZN.class.getDeclaredClasses();
		for (Class<?> clazz : classes) {
			getFields(list, clazz);
		}
		return list;
	}

	protected static final void getFields(List<Pack> out, Class<?> clazz) {
		if (Modifier.isStatic(clazz.getModifiers())) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				out.add(new Pack(clazz, field));
			}
		}
	}

	//
	public static void store(Properties properties, OutputStream out, String comments) throws IOException {
		store0(properties, new BufferedWriter(new OutputStreamWriter(out, "8859_1")), comments, true);
	}

	//
	private static void store0(Properties properties, BufferedWriter bw, String comments, boolean escUnicode)
			throws IOException {
		bw.write("#" + new Date().toString());
		bw.newLine();
		synchronized (bw) {
			List<String> keys = new ArrayList<String>(properties.size());
			Enumeration<?> keies = properties.keys();
			while (keies.hasMoreElements()) {
				String key = (String) keies.nextElement();
				keys.add(key);
			}

			Collections.sort(keys, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			for (String key : keys) {
				String val = (String) properties.getProperty(key);
				key = saveConvert(key, true, escUnicode);
				val = saveConvert(val, false, escUnicode);
				bw.write(key + "=" + val);
				bw.newLine();
			}
		}
		bw.flush();
	}

	/*
	 * Converts unicodes to encoded &#92;uxxxx and escapes special characters
	 * with a preceding slash
	 */
	private static String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuffer outBuffer = new StringBuffer(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				if (x == 0 || escapeSpace)
					outBuffer.append('\\');
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				outBuffer.append('\\');
				outBuffer.append(aChar);
				break;
			default:
				if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	/**
	 * Convert a nibble to a hex character
	 * 
	 * @param nibble
	 *            the nibble to convert.
	 */
	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	/** A table of hex digits */
	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
			'D', 'E', 'F' };

	private static final class Pack {
		protected Class<?> clazz;
		protected Field field;
		protected String val;

		public Pack(Class<?> clazz, Field field) {
			this.clazz = clazz;
			this.field = field;
			try {
				if (Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					val = field.get(null).toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public final String getKey() {
			if (clazz.equals(ZN.class)) {
				return field.getName().toLowerCase();
			} else {
				return (clazz.getSimpleName() + "." + field.getName()).toLowerCase();
			}

		}

		public final String getVal() {
			return val;
		}

		@Override
		public String toString() {
			return getKey() + " = " + getVal();
		}
	}
}
