package psd.reflect;

import psd.PsdFile;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * PSD 的文件夹
 * 
 * @author roy
 */
public class PsdGroup extends Group {
	// 默认的资源加载器
	private static AssetManager assetManager;
	// 文件夹的源
	private final psd.Folder psdFolder;

	public PsdGroup(PsdFile psdFile) {
		this(psdFile, psdFile, getAssetManager());
	}

	public PsdGroup(psd.Folder psdFolder, PsdFile psdFile, AssetManager assetManager) {
		this.psdFolder = psdFolder;
		for (psd.Element element : psdFile.childs) {
			Actor actor = PsdElement.toGdxActor(psdFile, element, assetManager);
			addActor(actor);
		}
	}

	//
	public psd.Folder getPsdFolder() {
		return psdFolder;
	}

	public static final PsdGroup reflect(Object object) {
		return PsdElement.reflect(object);
	}

	public static final AssetManager getAssetManager() {
		if (assetManager == null) {
			assetManager = new AssetManager();
		}
		return assetManager;
	}
}
