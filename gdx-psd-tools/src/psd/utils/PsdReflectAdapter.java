package psd.utils;

import com.badlogic.gdx.utils.viewport.Viewport;

import psd.reflect.PsdGroup;

/** 简易的反射对象 */
public abstract class PsdReflectAdapter implements PsdReflectListener {
	// 数据源
	private PsdGroup source;

	@Override
	public void onReflectSuccess(PsdGroup psdGroup) {
		this.source = psdGroup;
		onCreate(psdGroup);
	}

	/** 获取源 */
	public final PsdGroup getSource() {
		return source;
	}

	@Override
	// 设置了新视图
	public void onViewportChange(Viewport viewport) {

	}

	// 反射函数
	protected abstract void onCreate(PsdGroup psdGroup);
}
