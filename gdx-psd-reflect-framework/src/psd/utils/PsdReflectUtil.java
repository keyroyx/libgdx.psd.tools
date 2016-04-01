package psd.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import psd.Element;
import psd.PsdFile;
import psd.loaders.FileManage;
import psd.reflect.PsdAn;
import psd.reflect.PsdGroup;
import psd.reflect.PsdImage;
import psd.reflect.PsdLabel;
import psd.reflect.PsdReflectListener;

public class PsdReflectUtil {

	// 设置大小
	protected static final void setBounds(psd.Element element, Actor actor) {
		if (element != null && actor != null) {
			actor.setBounds(element.x, element.y, element.width, element.height);
			// 设置用户对象
			element.setActor(actor);
		}
	}

	// 准备项目
	public static final <T> T load(AssetManager assetManager, String fileName, Class<T> clazz) {
		if (assetManager.isLoaded(fileName, clazz)) {
		} else {
			assetManager.load(fileName, clazz);
			assetManager.finishLoading();
		}
		return assetManager.get(fileName, clazz);
	}

	public static final PsdGroup reflect(Object object) {
		Class<?> reflectClass = (object instanceof Class<?>) ? (Class<?>) object : object.getClass();
		PsdAn an = reflectClass.getAnnotation(PsdAn.class);
		if (an == null) {
			throw new IllegalArgumentException(
					"class : " + reflectClass.getName() + "  not annotation @PsdAn");
		} else {
			try {
				String path = null;
				if (an.value().length == 0) {
					path = reflectClass.getSimpleName() + ".json";
				} else {
					path = an.value()[0];
				}
				// 加载对象

				FileHandle fileHandle = FileManage.file(path);
				PsdFile psdFile = new PsdFile(fileHandle);
				// 生成结构
				PsdGroup psdGroup = new PsdGroup(psdFile);
				PsdReflectUtil.setBounds(psdFile, psdGroup);
				// 映射 参数
				Field[] fields = reflectClass.getDeclaredFields();
				for (Field field : fields) {
					an = field.getAnnotation(PsdAn.class);
					if (an != null && (Actor.class.isAssignableFrom(field.getType())
							|| Element.class.isAssignableFrom(field.getType()))) {
						Element element = psdFile.get(new NameFilter(field, an));
						if (element != null && element.getActor() != null) {
							Actor actor = element.getActor();
							field.setAccessible(true);
							if (Actor.class.isAssignableFrom(field.getType())) {
								field.set(object, actor);
							} else if (Element.class.isAssignableFrom(field.getType())) {
								field.set(object, element);
							}
						}
					}
				}
				// 映射 函数
				Method[] methods = reflectClass.getDeclaredMethods();
				for (Method method : methods) {
					an = method.getAnnotation(PsdAn.class);
					if (an != null) {
						List<Element> elements = psdFile.filter(new NameFilter(method, an));
						for (Element element : elements) {
							Actor actor = element.getActor();
							if (actor != null) {
								actor.addListener(new MethordClickListener(object, method, actor, element));
							}
						}
					}
				}
				// 激活监听
				if (object instanceof PsdReflectListener) {
					((PsdReflectListener) object).onReflectSuccess(psdGroup);
				}
				//
				return psdGroup;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// 将 JSON 对象 , 转换成 Actor 对象
	public static final Actor toGdxActor(PsdFile psdFile, psd.Element element, AssetManager assetManager)
			throws Exception {
		Actor actor = null;
		if (element instanceof psd.Folder) {
			psd.Folder psdFolder = (psd.Folder) element;
			actor = new PsdGroup(psdFolder, psdFile, assetManager);
		} else if (element instanceof psd.Pic) {
			psd.Pic pic = (psd.Pic) element;
			actor = new PsdImage(psdFile, pic, assetManager);
		} else if (element instanceof psd.Text) {
			psd.Text psdText = (psd.Text) element;
			actor = new PsdLabel(psdText);
		}
		PsdReflectUtil.setBounds(element, actor);
		actor.setVisible(element.isVisible);
		return actor;
	}

	protected static final class MethordClickListener extends ClickListener {
		final Object object;
		final Method method;
		final Element element;
		final Actor actor;
		final Object[] params;

		public MethordClickListener(Object object, Method method, Actor actor, Element element) {
			this.object = object;
			this.method = method;
			this.element = element;
			this.actor = actor;
			this.params = new Object[method.getParameterTypes().length];
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			method.setAccessible(true);
			try {
				Class<?>[] classes = method.getParameterTypes();
				int elementIndex = getIndex(classes, Element.class);
				if (elementIndex != -1) {
					params[elementIndex] = element;
				}
				int actorIndex = getIndex(classes, Actor.class);
				if (actorIndex != -1) {
					params[actorIndex] = actor;
				}
				method.invoke(object, params);
				for (int i = 0; i < params.length; i++) {
					params[i] = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private final int getIndex(Class<?>[] classes, Class<?> target) {
			for (int i = 0; i < classes.length; i++) {
				if (target.isAssignableFrom(classes[i])) {
					return i;
				}
			}
			return -1;
		}
	}

	private static final class NameFilter implements ElementFilter {
		final String elementName;

		public NameFilter(Field field, PsdAn an) {
			if (an.value().length == 0) {
				elementName = field.getName();
			} else {
				elementName = an.value()[0];
			}
		}

		public NameFilter(Method method, PsdAn an) {
			if (an.value().length == 0) {
				elementName = method.getName();
			} else {
				elementName = an.value()[0];
			}
		}

		@Override
		public boolean accept(Element element) {
			return elementName.equals(element.layerName);
		}

	}
}
