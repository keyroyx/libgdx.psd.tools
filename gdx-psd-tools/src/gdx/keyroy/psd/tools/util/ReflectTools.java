package gdx.keyroy.psd.tools.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.keyroy.util.json.JsonAn;

public class ReflectTools {
	private static final Set<Class<?>> BASE_CLASS_SET = new HashSet<Class<?>>();
	static {
		BASE_CLASS_SET.add(String.class);
		BASE_CLASS_SET.add(CharSequence.class);
		BASE_CLASS_SET.add(Boolean.class);
		BASE_CLASS_SET.add(Byte.class);
		BASE_CLASS_SET.add(Short.class);
		BASE_CLASS_SET.add(Integer.class);
		BASE_CLASS_SET.add(Float.class);
		BASE_CLASS_SET.add(Long.class);
		BASE_CLASS_SET.add(Double.class);
	}

	public static final Object parser(String s, Class<?> clazz) {
		if (isBaseType(clazz)) {
			// boolean, byte, char, short, int, long, float, and double.
			if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
				return Boolean.parseBoolean(s);
			} else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
				return Integer.parseInt(s);
			} else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
				return Long.parseLong(s);
			} else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
				return Float.parseFloat(s);
			} else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
				return Double.parseDouble(s);
			} else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
				return Byte.parseByte(s);
			} else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
				return Short.parseShort(s);
			}
		}
		return s;
	}

	public static final boolean isBaseType(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.isPrimitive() || BASE_CLASS_SET.contains(clazz);
	}

	public static final boolean isDefaultValue(Object value) {
		if (value == null) {
			return true;
		} else if (isSubClass(value.getClass(), Number.class)) {
			return Float.parseFloat(value.toString()) == 0;
		} else if (value instanceof String) {
			return value.equals("");
		} else if (value.getClass().equals(boolean.class) || value.getClass().equals(Boolean.class)) {
			return value.equals(Boolean.FALSE);
		}

		return false;
	}

	public static final boolean isString(Object object) {
		return String.class.isInstance(object);
	}

	public static final boolean isNumber(Object object) {
		return Number.class.isInstance(object);
	}

	public static final boolean isBoolean(Object object) {
		return Boolean.class.isInstance(object);
	}

	public static final boolean isArray(Object object) {
		if (object instanceof Class<?>) {
			return ((Class<?>) object).isArray();
		} else if (object instanceof Field) {
			return isArray((Field) object);
		} else if (object instanceof Type) {
			return isArray((Type) object);
		}
		return object.getClass().isArray();
	}

	public static final boolean isArray(Class<?> clazz) {
		return clazz.isArray();
	}

	public static final boolean isArray(Field field) {
		return isArray(field.getType());
	}

	public static final Class<?> getClass(Object object) {
		if (object instanceof Class<?>) {
			return (Class<?>) object;
		} else if (object instanceof Field) {
			return getClass((Field) object);
		} else if (object instanceof Type) {
			return getClass((Type) object);
		}
		return object.getClass();
	}

	public static final Class<?> getClass(Field field) {
		return getClass(field.getType());
	}

	public static final Class<?> getClass(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] params = (parameterizedType).getActualTypeArguments();
			return getClass(params[0]);
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			return (Class<?>) genericArrayType.getGenericComponentType();
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			return (Class<?>) wildcardType.getUpperBounds()[0];
		} else if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			return (Class<?>) typeVariable.getGenericDeclaration();
		} else {
			return (Class<?>) type;
		}
	}

	// ----------------------------------------------------------------------------------------------------

	private static final void addField(Field field, List<Field> list) {
		JsonAn annotation = field.getAnnotation(JsonAn.class);
		if (annotation == null || annotation.skip() == false) {
			field.setAccessible(true);
			list.add(field);
		}
	}

	public static final List<Field> getFields(Class<?> clazz) {
		List<Field> list = new ArrayList<Field>();
		int modifiers = 0;
		while (clazz != null && clazz.equals(Object.class) == false) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				modifiers = field.getModifiers();
				if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
					// ²»ÄÜÐÞ¸Ä
				} else {
					addField(field, list);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return list;
	}

	public static final List<Field> getFields(Object object, final String header) {
		if (object instanceof Class<?>) {
			return getFields((Class<?>) object, header);
		} else {
			return getFields(object.getClass(), header);
		}
	}

	public static final Class<?>[] getTemplates(Field field) {
		ParameterizedType type = (ParameterizedType) field.getGenericType();
		if (type != null && type.getActualTypeArguments().length == 2) {
			Class<?> keyClass = (Class<?>) type.getActualTypeArguments()[0];
			Class<?> valClass = (Class<?>) type.getActualTypeArguments()[1];
			return new Class[] { keyClass, valClass };
		} else {
			return new Class<?>[2];
		}
	}

	public static final Class<?> getTemplate(Field field) {
		if (field.getType().isArray()) {
			return field.getType().getComponentType();
		} else if (isSubClass(field, List.class)) {
			return getClass(field.getGenericType());
		}
		return null;
	}

	public static final boolean isStatic(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	public static final boolean isSubClass(Field field, Class<?> superClass) {
		return isSubClass(field.getType(), superClass);
	}

	public static final boolean isSubClass(Class<?> childClass, Class<?> superClass) {
		return childClass.equals(superClass) || superClass.isAssignableFrom(childClass);
	}

}
