package psd.lg0311;

import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.keyroy.util.json.Json;

import psd.Element;
import psd.Folder;
import psd.Param;
import psd.framework.PsdReflectAdapter;
import psd.lg0311.params.FS;
import psd.lg0311.params.Particle;
import psd.lg0311.widget.FntWidget;
import psd.lg0311.widget.FrameAnimationWidget;
import psd.lg0311.widget.ParticleWidge;
import psd.lg0311.widget.ProgressBarWidget;
import psd.reflect.PsdAn;
import psd.reflect.PsdGroup;
import psd.reflect.PsdImage;
import psd.utils.Filter;

/**
 * �?单的 反射操作对象
 */
public abstract class PsdAdapter extends PsdReflectAdapter {
	public static boolean debug = true;
	// 对话�?
	private Array<DialogAdapter> dialogs = new Array<DialogAdapter>(2);

	@Override
	protected void onCreate(PsdGroup psdGroup) {

	}

	// 替换指定的对�?
	protected final DialogAdapter showDialog(DialogAdapter dialogAdapter) {
		// 每个dialog对象 都应该具有唯�?�?
		for (DialogAdapter dialog : dialogs) {
			if (dialog.getClass().equals(dialogAdapter.getClass())) {
				return dialogAdapter;
			}
		}
		// 创建菜单对象
		PsdGroup.reflect(dialogAdapter);
		dialogAdapter.setParent(this);
		dialogs.add(dialogAdapter);
		// 添加到显示对�?
		float duration = dialogAdapter.show(getSource());
		getSource().addAction(Actions.delay(duration, new Action() {
			@Override
			public boolean act(float delta) {
				onDialogChange(dialogs.size);
				return true;
			}
		}));
		return dialogAdapter;

	}

	// 关闭对话�?
	protected final DialogAdapter hideDialog() {
		if (dialogs.size > 0) {
			DialogAdapter dialogAdapter = dialogs.pop();
			// 这里显示关闭动画
			dialogAdapter.close();
			onDialogChange(dialogs.size);
			return dialogAdapter;
		}
		return null;
	}

	protected final void hideDialog(DialogAdapter adapter) {
		dialogs.removeValue(adapter, false);
		onDialogChange(dialogs.size);
	}

	// 对话框行为改�?
	protected void onDialogChange(int dialogSize) {
	}

	// 替换指定的对�?
	protected final static void replace(Actor target, Actor insteadOf, int align) {
		insteadOf.setPosition(target.getX(align), target.getY(align), align);
		insteadOf.setName(target.getName());
		target.getParent().addActorBefore(target, insteadOf);
		target.remove();
	}

	// 替换指定的对�?
	protected final static void replace(Actor target, Actor insteadOf) {
		insteadOf.setBounds(target.getX(), target.getY(), target.getWidth(), target.getHeight());
		insteadOf.setName(target.getName());
		target.getParent().addActorBefore(target, insteadOf);
		target.remove();
	}

	// 替换指定的路�?
	protected final Actor replace(String path, Actor insteadOf) {
		return replace(path, insteadOf, 0, Align.bottomLeft);
	}

	// 替换指定的路�?
	protected final Actor replace(String path, Actor insteadOf, int index) {
		return replace(path, insteadOf, index, Align.bottomLeft);
	}

	// 替换指定的路�?
	protected final Actor replace(String path, Actor insteadOf, int index, int align) {
		Actor actor = getSource().findActor(path, index);
		if (actor != null) {
			replace(actor, insteadOf, align);
			return insteadOf;
		} else {
			throw new IllegalArgumentException("actor not found : " + path);
		}
	}

	// 查找对象
	public final <T extends Actor> T findActor(String name) {
		return getSource().findActor(name);
	}

	// 查找对象
	public final <T extends Actor> T findActor(String name, int index) {
		return getSource().findActor(name, index);
	}

	// 查找对象
	public final Array<Actor> findActor(String... paths) {
		Array<Actor> array = new Array<Actor>(paths.length);
		for (int i = 0; i < paths.length; i++) {
			array.add(findActor(paths[i]));
		}
		return array;
	}

	// 中心对齐
	protected final void center(Actor actor, String path) {
		center(actor, findActor(path));
	}

	// 中心对齐
	protected final void center(Actor actor, Actor anchor) {
		anchor(actor, anchor, Align.center);
	}

	// 对齐目标
	protected final void anchor(Actor actor, Actor anchor, int align) {
		actor.setPosition(anchor.getX(align), anchor.getY(align), align);
	}

	protected final FrameAnimationWidget translateToFrameAnimationWidget(String actorPath) {
		FrameAnimationWidget frameAnimationWidget = new FrameAnimationWidget((PsdGroup) findActor(actorPath));
		replace(findActor(actorPath), frameAnimationWidget);
		return frameAnimationWidget;
	}

	// 转换指定位置 为进度条模式
	protected final ProgressBarWidget translateToProgressBarWidget(String actorPath) {
		ProgressBarWidget progressBarWidget = new ProgressBarWidget((PsdImage) findActor(actorPath));
		replace(findActor(actorPath), progressBarWidget);
		return progressBarWidget;
	}

	// 增加按钮效果
	protected final List<Actor> initButtonStyle(Filter<Actor> filter) {
		List<Actor> array = getSource().filter(filter);
		for (Actor actor : array) {
			try {
				initButtonStyle(actor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	// 增加按钮效果
	protected final Array<Actor> initButtonStyle(String... paths) {
		Array<Actor> array = new Array<Actor>();
		for (String path : paths) {
			try {
				Actor actor = getSource().findActor(path);
				initButtonStyle(actor);
				array.add(actor);
			} catch (Exception e) {
				throw new IllegalArgumentException("error on : " + path);
			}

		}
		return array;
	}

	// 增加按钮效果
	protected static final void initButtonStyle(Actor... actors) {
		for (Actor actor : actors) {
			initButtonStyle(actor);
		}
	}

	// 增加按钮效果
	protected static final Actor initButtonStyle(final Actor actor) {
		actor.setOrigin(actor.getWidth() / 2, actor.getHeight() / 2);
		actor.addListener(new ClickListener() {
			private float pressedScale = 0.85f;
			private float duration = 0.1f;

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
				play(actor, pressedScale);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int b) {
				play(actor, 1);
			}

			public void play(final Actor actor, float scale) {
				if (actor != null) {
					actor.clearActions();
					actor.addAction(Actions.scaleTo(scale, scale, duration));
				}
			}
		});
		return actor;
	}

	// 隐藏 元素
	protected final void hide(String... paths) {
		for (String path : paths) {
			if (path.endsWith("/")) {
				PsdGroup group = findActor(path.substring(0, path.length() - 1));
				for (Actor actor : group.getChildren()) {
					actor.setVisible(false);
				}
				group.setVisible(false);
			} else {
				findActor(path).setVisible(false);
			}
		}
	}

	// 显示 元素
	protected final void display(String... paths) {
		for (String path : paths) {
			if (path.endsWith("/")) {
				PsdGroup group = findActor(path.substring(0, path.length() - 1));
				for (Actor actor : group.getChildren()) {
					actor.setVisible(true);
				}
				group.setVisible(true);
			} else {
				Actor actor = findActor(path);
				actor.setVisible(true);
				if (actor.getParent() != null && actor.getParent().isVisible() == false) {
					actor.getParent().setVisible(true);
				}
			}
		}
	}

	// 生成映射对象
	public final PsdGroup reflect() {
		return PsdGroup.reflect(this);
	}

	// 生成映射对象
	public static final PsdGroup reflect(Object object) {
		return PsdGroup.reflect(object);
	}

	@SuppressWarnings("unchecked")
	// 获取指定�? Action 对象
	public static final <T extends Action> T getAction(Actor actor, Class<T> clazz) {
		for (Action action : actor.getActions()) {
			if (action.getClass().equals(clazz)) {
				return (T) action;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	// 获取指定�? Actor 对象
	public static final <T extends Actor> T getActor(Group group, Class<T> clazz) {
		for (Actor actor : group.getChildren()) {
			if (actor.getClass().equals(clazz)) {
				return (T) actor;
			}
		}
		return null;
	}

	// 缓存指定的界�?
	public static final void loadPsdResource(Class<? extends PsdAdapter> clazz) {
		String jsonPath = getJsonPath(clazz);
		if (jsonPath != null) {
			loadPsdResource(jsonPath);
		}
	}

	// 获取Json的加载路�?
	private static final String getJsonPath(Object object) {
		// 映射的PSD路径
		String psdPath = null;
		if (psdPath == null) { // 没有获取到PSD 路径 , 尝试解析 @PsdAn
			Class<?> reflectClass = (object instanceof Class<?>) ? (Class<?>) object : object.getClass();
			PsdAn an = reflectClass.getAnnotation(PsdAn.class);
			if (an != null) {
				if (an.value().length == 0) {
					psdPath = reflectClass.getSimpleName() + ".json";
				} else {
					psdPath = an.value()[0];
				}
			}
		}
		return psdPath;
	}

	public final void __print(String msg) {
		if (debug) {
			System.out.println(getMethodName() + msg);
		}
	}

	public final void __print(Object... msg) {
		if (debug) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < msg.length; i++) {
				buffer.append(msg[i].toString());
				if (i != msg.length - 1) {
					buffer.append("    ");
				}
			}
			System.out.println(getMethodName() + buffer.toString());
		}
	}

	private final String getMethodName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		return "[" + stacktrace[3].getLineNumber() + " " + getClass().getSimpleName() + "."
				+ stacktrace[3].getMethodName() + "]";
	}

	@Override
	public Actor onReflectElement(PsdGroup parent, Element element, AssetManager assetManager)
			throws Exception {
		Actor actor = null;
		if (element.getParams() != null) {
			for (Param param : element.getParams()) {
				if(param.getId().equals(FntWidget.getId())){
					
				}
				
				
				
				if (param.getId().equals("fs") && element instanceof Folder) {
					actor = super.onReflectElement(parent, element, assetManager);
					if (actor instanceof PsdGroup) {
						FrameAnimationWidget fsWidget = new FrameAnimationWidget((PsdGroup) actor);
						FS fs = param.reflect(FS.class);
						if (fs != null) {
							fsWidget.setCurrentIndex(fs.i);
							fsWidget.setDelay(fs.delay);
							fsWidget.setDuration(fs.t);
							if (fs.play) {
								fsWidget.startAnimation();
							}
						}
						actor = fsWidget;
					}
				} else if (param.getId().equals("part")) {
					Particle particle = new Json(param.getJson()).toObject(Particle.class);
					ParticleWidge particleWidge = new ParticleWidge(particle.getPath());
					//
					actor = particleWidge;
				} else {
					System.out.println(param.getId());
				}
			}
		}
		//
		if (actor == null) {
			return super.onReflectElement(parent, element, assetManager);
		} else {
			return actor;
		}
	}
}
