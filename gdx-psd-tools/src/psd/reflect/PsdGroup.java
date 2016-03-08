package psd.reflect;

import java.util.ArrayList;
import java.util.List;

import psd.Element;
import psd.ElementFilter;
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

	// 过滤元素
	public final List<Actor> filter(final ActorFilter filter) {
		List<Element> elements = psdFolder.filter(new ElementFilter() {

			@Override
			public boolean accept(Element element) {
				return filter.accept(element, (Actor) element.getUserObject());
			}
		});
		List<Actor> actors = new ArrayList<Actor>(elements.size());
		for (Element element : elements) {
			actors.add((Actor) element.getUserObject());
		}
		return actors;
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

	public static interface ActorFilter {
		public boolean accept(Element element, Actor actor);
	}

}
