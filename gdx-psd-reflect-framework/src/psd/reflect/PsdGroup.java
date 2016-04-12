package psd.reflect;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import psd.Element;
import psd.PsdFile;
import psd.loaders.FileManage;
import psd.utils.ElementFilter;
import psd.utils.PsdReflectUtil;

/**
 * PSD 的文件夹
 * 
 * @author roy
 */
public class PsdGroup extends WidgetGroup {

	// 文件夹的源
	private final psd.Folder psdFolder;

	public PsdGroup(PsdFile psdFile) {
		this(psdFile, psdFile, getAssetManager());
	}

	public PsdGroup(psd.Folder psdFolder, PsdFile psdFile, AssetManager assetManager) {
		this.psdFolder = psdFolder;
		this.setSize(psdFolder.width, psdFolder.height);
		for (psd.Element element : this.psdFolder.childs) {
			try {
				addActor(PsdReflectUtil.toGdxActor(psdFile, element, assetManager));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public float getPrefWidth() {
		return getWidth();
	}

	@Override
	public float getPrefHeight() {
		return getHeight();
	}

	@Override
	public <T extends Actor> T findActor(final String name) {
		return findActor(name, 0);
	}

	public <T extends Actor> T findActor(final String name, int index) {
		Element element = psdFolder.get(name, index);
		if (element != null) {
			return element.getActor();
		}
		return null;
	}

	// 过滤元素
	public final List<Actor> filter(final ActorFilter filter) {
		List<Element> elements = psdFolder.filter(new ElementFilter() {
			@Override
			public boolean accept(Element element) {
				return filter.accept(element, (Actor) element.getActor());
			}
		});
		List<Actor> actors = new ArrayList<Actor>(elements.size());
		for (Element element : elements) {
			actors.add((Actor) element.getActor());
		}
		return actors;
	}

	//
	public psd.Folder getPsdFolder() {
		return psdFolder;
	}

	public static final PsdGroup reflect(Object object) {
		return PsdReflectUtil.reflect(object);
	}

	public static final AssetManager getAssetManager() {
		return FileManage.getAssetManager();
	}

	public static interface ActorFilter {
		public boolean accept(Element element, Actor actor);
	}

}
