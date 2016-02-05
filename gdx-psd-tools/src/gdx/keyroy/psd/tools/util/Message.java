package gdx.keyroy.psd.tools.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@SuppressWarnings("rawtypes")
public class Message {
	private static final Hashtable<Class, List<MessageListener>> HASHTABLE = new Hashtable<Class, List<MessageListener>>();

	// 注册消息组件
	public static final <T> void register(Class<T> clazz, MessageListener<T> listener) {
		List<MessageListener> listeners = getListeners(clazz);
		listeners.add(listener);
	}

	// 删除
	public static final <T> void unregister(Class<T> clazz, MessageListener<T> listener) {
		List<MessageListener> listeners = getListeners(clazz);
		listeners.remove(listener);
	}

	// 发送信息
	@SuppressWarnings("unchecked")
	public static final <T> void send(T t, Object... params) {
		if (t instanceof Class<?>) {
			List<MessageListener> listeners = getListeners((Class<?>) t);
			for (MessageListener messageListener : listeners) {
				try {
					messageListener.onMessage(null, params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			List<MessageListener> listeners = getListeners(t.getClass());
			for (MessageListener messageListener : listeners) {
				try {
					messageListener.onMessage(t, params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static final List<MessageListener> getListeners(Class<?> clazz) {
		List<MessageListener> listeners = HASHTABLE.get(clazz);
		if (listeners == null) {
			listeners = new ArrayList<MessageListener>();
			HASHTABLE.put(clazz, listeners);
		}
		return listeners;
	}

}
