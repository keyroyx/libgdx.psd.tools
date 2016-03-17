package psd.utils;

import com.badlogic.gdx.utils.viewport.Viewport;

import psd.reflect.PsdGroup;

/**
 * Ó³ÉäµÄ¼àÌý
 * 
 * @author roy
 * 
 */
public interface PsdReflectListener {
	public void onReflectSuccess(PsdGroup psdGroup);

	public void onViewportChange(Viewport viewport);
}
