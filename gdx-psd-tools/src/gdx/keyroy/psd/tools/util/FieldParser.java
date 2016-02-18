package gdx.keyroy.psd.tools.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class FieldParser {

	public String toString(Object object) {
		return String.valueOf(object);
	}

	public abstract Object parser(String s);

	public abstract Object getDefault();

	public abstract boolean isDefault(Object object);

	public final static FieldParser get(final Field field) {
		return table.get(field.getType());
	}

	public final static FieldParser get(Type type) {
		return table.get(type);
	}

	public final static FieldParser get(Class<?> clazz) {
		return table.get(clazz);
	}

	public final static FieldParser get(String simpleName) {
		return classNameTable.get(simpleName);
	}

	public final static boolean has(Type type) {
		return table.containsKey(type);
	}

	public final static boolean has(Class<?> clazz) {
		return table.containsKey(clazz);
	}

	public final static boolean has(Field field) {
		return table.get(field) != null;
	}

	public final static boolean has(String className) {
		return classNameTable.containsKey(className);
	}

	private final static Hashtable<Class<?>, FieldParser> table = new Hashtable<Class<?>, FieldParser>();
	private final static Hashtable<String, FieldParser> classNameTable = new Hashtable<String, FieldParser>();
	static {
		table.put(int.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				if (s.length() > 0) {
					try {
						return Integer.parseInt(s);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return 0;
			}

			@Override
			public boolean isDefault(Object object) {
				return (Integer) object == 0;
			}

			@Override
			public Object getDefault() {
				return 0;
			}
		});
		table.put(long.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				if (s.length() > 0) {
					try {
						return Long.parseLong(s);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return 0;
			}

			@Override
			public boolean isDefault(Object object) {
				return (Long) object == 0;
			}

			@Override
			public Object getDefault() {
				return 0;
			}
		});
		table.put(float.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				return Float.parseFloat(s);
			}

			@Override
			public boolean isDefault(Object object) {
				return (Float) object == 0;
			}

			@Override
			public Object getDefault() {
				return 0;
			}
		});
		table.put(double.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				return Double.parseDouble(s);
			}

			@Override
			public boolean isDefault(Object object) {
				return (Double) object == 0;
			}

			@Override
			public Object getDefault() {
				return 0;
			}
		});
		table.put(boolean.class, new FieldParser() {

			@Override
			public Object parser(String s) {
				return Boolean.parseBoolean(s);
			}

			@Override
			public boolean isDefault(Object object) {
				return (Boolean) object == false;
			}

			@Override
			public Object getDefault() {
				return false;
			}
		});

		table.put(Integer.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				return Integer.parseInt(s);
			}

			@Override
			public boolean isDefault(Object object) {
				return (Integer) object == 0;
			}

			@Override
			public Object getDefault() {
				return 0;
			}
		});
		table.put(Long.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				return Long.parseLong(s);
			}

			@Override
			public boolean isDefault(Object object) {
				return (Long) object == 0;
			}

			@Override
			public Object getDefault() {
				return 0;
			}
		});
		table.put(Float.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				return Float.parseFloat(s);
			}

			@Override
			public boolean isDefault(Object object) {
				return (Float) object == 0;
			}

			@Override
			public Object getDefault() {
				return 0;
			}
		});
		table.put(Double.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				return Double.parseDouble(s);
			}

			@Override
			public boolean isDefault(Object object) {
				return (Double) object == 0;
			}

			@Override
			public Object getDefault() {
				return 0;
			}
		});
		table.put(Boolean.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				return Boolean.parseBoolean(s);
			}

			@Override
			public boolean isDefault(Object object) {
				return (Boolean) object == false;
			}

			@Override
			public Object getDefault() {
				return false;
			}
		});

		table.put(String.class, new FieldParser() {
			@Override
			public Object parser(String s) {
				return s;
			}

			@Override
			public boolean isDefault(Object object) {
				return object.equals("");
			}

			@Override
			public Object getDefault() {
				return null;
			}
		});
		Enumeration<Class<?>> enumeration = table.keys();
		while (enumeration.hasMoreElements()) {
			Class<?> clazz = (Class<?>) enumeration.nextElement();
			FieldParser parser = table.get(clazz);
			classNameTable.put(clazz.getName(), parser);
			classNameTable.put(clazz.getSimpleName(), parser);
		}
	}
}
