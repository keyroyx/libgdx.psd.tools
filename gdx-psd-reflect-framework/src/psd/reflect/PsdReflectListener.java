package psd.reflect;

import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;

import psd.Element;
import psd.Param;

/**
 * 映射的监听
 * 
 */
public interface PsdReflectListener {
	/** 反射对象完成 */
	public void onReflectSuccess(PsdGroup psdGroup);

	/** 反射对象完成 */
	public Actor onReflectElement(PsdGroup parent, Element element, List<Param> params,
			AssetManager assetManager) throws Exception;
}
