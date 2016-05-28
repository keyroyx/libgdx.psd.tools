package psd.framework;

import java.lang.reflect.Constructor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

import psd.reflect.PsdStage;

/***
 * 舞台组
 */
public abstract class PsdReflectApplicationAdapter extends ApplicationAdapter {
	private static PsdReflectStageGroup stageGroup = new PsdReflectStageGroup();
	private static Class<? extends PsdStage> stageClass;

	@Override
	public void create() {
		Gdx.input.setInputProcessor(stageGroup);
		onCreate();
	}

	// 初始化调用
	protected abstract void onCreate();

	@Override
	public void render() {
		// 清除屏幕颜色
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// 支持颜色混合
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		// 支持透明通道
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		// 执行动画
		stageGroup.act();
		// 执行绘制
		stageGroup.draw();
	}

	// 获取到对象
	public static PsdReflectStageGroup getStageGroup() {
		return stageGroup;
	}

	/** 设置当前页面 */
	public static final void set(Object object) {
		Stage stage = toStage(object);
		if (stage != null) {
			stageGroup.clean();
			stageGroup.add(stage);
		} else {
			throw new IllegalArgumentException("stage == null , form " + object.getClass().getName());
		}
	}

	/** 压栈 , 显示新页面 */
	public static final void push(Object object) {
		Stage stage = toStage(object);
		if (stage != null) {
			stageGroup.push(stage);
		} else {
			throw new IllegalArgumentException("stage == null , form " + object.getClass().getName());
		}
	}

	public static final boolean pop() {
		return stageGroup.pop();
	}

	protected static final Stage toStage(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Stage) {
			return (Stage) object;
		} else if (stageClass != null) {
			try {
				Constructor<? extends PsdStage> constructor = stageClass.getConstructor(Object.class);
				return constructor.newInstance(object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new PsdStage(object);
	}

	public static void setStageClass(Class<? extends PsdStage> stageClass) {
		PsdReflectApplicationAdapter.stageClass = stageClass;
	}

}
