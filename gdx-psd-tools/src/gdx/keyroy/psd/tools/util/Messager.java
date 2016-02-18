package gdx.keyroy.psd.tools.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@SuppressWarnings("rawtypes")
public class Messager {
	private static final Hashtable<Class, List<MessageListener>> HASHTABLE = new Hashtable<Class, List<MessageListener>>();
	private static final boolean debug = false;

	// 注册消息组件
	public static final <T> void register(Class<T> clazz, MessageListener<T> listener) {
		List<MessageListener> listeners = getListeners(clazz);
		if (listeners.contains(listener) == false) {
			listeners.add(listener);
			log("register", clazz.getName());
		}
	}

	// 删除
	public static final <T> void unregister(Class<T> clazz, MessageListener<T> listener) {
		List<MessageListener> listeners = getListeners(clazz);
		listeners.remove(listener);
		log("unregister", clazz.getName());
	}

	// 发送信息
	@SuppressWarnings("unchecked")
	public static synchronized final <T> void send(T t, Object... params) {
		List<MessageListener> listeners = null;
		if (t instanceof Class<?>) {
			listeners = getListeners((Class<?>) t);
		} else {
			listeners = getListeners(t.getClass());
		}

		if (listeners != null) {
			for (int i = 0; i < listeners.size(); i++) {
				MessageListener messageListener = listeners.get(i);
				try {
					messageListener.onMessage(t, params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static synchronized final List<MessageListener> getListeners(Class<?> clazz) {
		List<MessageListener> listeners = HASHTABLE.get(clazz);
		if (listeners == null) {
			listeners = new ArrayList<MessageListener>();
			HASHTABLE.put(clazz, listeners);
		}
		return listeners;
	}

	private static final void log(String... strings) {
		if (debug) {
			StringBuffer buffer = new StringBuffer();
			for (String string : strings) {
				buffer.append(string);
				buffer.append(" ");
			}
			System.out.println(buffer.toString());
		}
	}
}
