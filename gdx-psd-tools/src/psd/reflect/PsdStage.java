package psd.reflect;

import psd.PsdFile;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

/**
 * PSD 的舞台
 * 
 * @author roy
 */
public class PsdStage extends Stage {
	// PSD 文件
	protected PsdFile psdFile;
	// 加载缓存
	protected AssetManager assetManager;

	public PsdStage(PsdFile psdFile) {
		super(new ScalingViewport(Scaling.stretch, psdFile.width, psdFile.height, new OrthographicCamera()));
		this.psdFile = psdFile;
		this.assetManager = new AssetManager();
		//
		for (psd.Element element : psdFile.childs) {
			Actor actor = PsdElement.toGdxActor(psdFile, element, assetManager);
			addActor(actor);
		}
	}

	public PsdStage(PsdGroup psdGroup) {
		super(new ScalingViewport(Scaling.stretch, psdGroup.getWidth(), psdGroup.getHeight(),
				new OrthographicCamera()));
		if (psdGroup.getPsdFolder() instanceof PsdFile) {
			this.psdFile = (PsdFile) psdGroup.getPsdFolder();
		}
		addActor(psdGroup);
	}

	//
	public PsdFile getPsdFile() {
		return psdFile;
	}

	//
	public AssetManager getAssetManager() {
		return assetManager;
	}

	public static final PsdStage reflect(Object object) {
		return new PsdStage(PsdElement.reflect(object));
	}

}
