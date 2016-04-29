package psd.framework;

import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;

import psd.Element;
import psd.Param;
import psd.loaders.FileManage;
import psd.reflect.PsdGroup;
import psd.reflect.PsdReflectListener;

/** 简易的反射对象 */
public abstract class PsdReflectAdapter extends FileManage implements PsdReflectListener {
	// 数据源
	private PsdGroup source;

	@Override
	public final void onReflectSuccess(PsdGroup psdGroup) {
		this.source = psdGroup;
		onCreate(psdGroup);
	}

	// 反射函数
	protected abstract void onCreate(PsdGroup psdGroup);

	// 设置了新视图
	protected void onViewportChange(Viewport viewport) {
	}

	/** 暂停显示 */
	protected void onHide() {
	}

	/** 返回显示 */
	protected void onShow() {
	}

	/** 显示对象 */
	protected final void show(Object object) {
		PsdReflectApplicationAdapter.set(object);
	}

	/** 压栈显示下一个对象 */
	protected final void push(Object object) {
		PsdReflectApplicationAdapter.push(object);
	}

	/** 显示上一个压栈对象 */
	protected final boolean back() {
		return PsdReflectApplicationAdapter.pop();
	}

	/** 获取源 */
	protected PsdGroup getSource() {
		return source;
	}

	/** 获取Psd导出Json的路径 */
	protected String getPsdJsonPath() {
		return null;
	}

	/** 反射对象完成 */
	public Actor onReflectElement(PsdGroup parent, Element element, List<Param> params,
			AssetManager assetManager) throws Exception {
		return PsdReflectUtil.toGdxActor(element, assetManager, this);
	}

}
