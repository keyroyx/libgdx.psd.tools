package psd.framework;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.viewport.Viewport;

import psd.reflect.PsdGroup;
import psd.reflect.PsdReflectListener;
import psd.utils.FileManage;

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

	/** 加载数据 */
	protected final <T> T get(Class<T> clazz, String fileName) {
		AssetManager assetManager = getAssetManager();
		if (assetManager.isLoaded(fileName, clazz) == false) {
			assetManager.load(fileName, clazz);
			assetManager.finishLoading();
		}
		return assetManager.get(fileName, clazz);
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
	protected final PsdGroup getSource() {
		return source;
	}

}
