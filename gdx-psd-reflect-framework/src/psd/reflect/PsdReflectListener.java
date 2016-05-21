package psd.reflect;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;

import psd.Element;

/**
 * 映射的监听
 * 
 */
public interface PsdReflectListener {
	/** 反射对象完成 */
	public void onReflectSuccess(PsdGroup psdGroup);

	/** 反射对象完成 */
	public Actor onReflectElement(PsdGroup parent, Element element, AssetManager assetManager)
			throws Exception;
}
