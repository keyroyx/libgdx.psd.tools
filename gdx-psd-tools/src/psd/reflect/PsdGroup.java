package psd.reflect;

import psd.Element;
import psd.PsdFile;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * PSD 的文件夹
 * 
 * @author roy
 */
public class PsdGroup extends WidgetGroup {
	// 默认的资源加载器
	private static AssetManager assetManager;
	// 文件夹的源
	private final psd.Folder psdFolder;

	public PsdGroup(PsdFile psdFile) {
		this(psdFile, psdFile, getAssetManager());
	}

	public PsdGroup(psd.Folder psdFolder, PsdFile psdFile, AssetManager assetManager) {
		this.psdFolder = psdFolder;
		for (psd.Element element : this.psdFolder.childs) {
			addActor(PsdElement.toGdxActor(psdFile, element, assetManager));
		}
	}

	@Override
	public <T extends Actor> T findActor(final String name) {
		Element element = psdFolder.get(name);
		if (element != null) {
			return element.getUserObject();
		}
		return null;
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
