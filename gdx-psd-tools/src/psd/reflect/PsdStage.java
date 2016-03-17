package psd.reflect;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import psd.utils.PsdReflectListener;
import psd.utils.PsdReflectUtil;

/**
 * PSD µÄÎèÌ¨
 */
public class PsdStage extends Stage {

	//
	public PsdStage(Object reflectObject) {
		this(reflectObject, PsdReflectUtil.reflect(reflectObject));
	}

	//
	public PsdStage(Object reflectObject, Viewport viewport) {
		this(reflectObject, PsdReflectUtil.reflect(reflectObject), viewport);
	}

	//
	public PsdStage(Object reflectObject, PsdGroup psdGroup) {
		this(reflectObject, psdGroup, new ScalingViewport(Scaling.stretch, psdGroup.getWidth(),
				psdGroup.getHeight(), new OrthographicCamera()));
	}

	//
	public PsdStage(Object reflectObject, PsdGroup psdGroup, Viewport viewport) {
		super(viewport);
		addActor(psdGroup);
		if (reflectObject instanceof PsdReflectListener) {
			((PsdReflectListener) reflectObject).onViewportChange(getViewport());
		}

	}

	public static final PsdStage reflect(Object object) {
		return new PsdStage(object, PsdReflectUtil.reflect(object));
	}

	public static final PsdStage reflect(Object object, Viewport viewport) {
		return new PsdStage(object, PsdReflectUtil.reflect(object), viewport);
	}

}
