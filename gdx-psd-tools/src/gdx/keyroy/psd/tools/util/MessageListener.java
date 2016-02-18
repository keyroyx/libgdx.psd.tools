package gdx.keyroy.psd.tools.util;

public abstract class MessageListener<T> {
	public abstract void onMessage(T t, Object[] params);

	protected final boolean isKey(MessageKey messageKey, Object[] params) {
		return is(messageKey, params);
	}

	protected final boolean is(Object val, Object[] params) {
		if (val == null) {
			return false;
		} else if (params == null || params.length == 0) {
			return false;
		}
		return val.equals(get(val.getClass(), params));
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	protected final <T> T get(Class<T> clazz, Object[] params) {
		for (Object object : params) {
			if (object.getClass().equals(clazz)) {
				return (T) object;
			}
		}
		return null;
	}
}
