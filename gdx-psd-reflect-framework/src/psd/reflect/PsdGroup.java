package psd.reflect;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import psd.Param;
import psd.ParamProvider;
import psd.PsdFile;
import psd.framework.PsdReflectUtil;
import psd.loaders.FileManage;

/**
 * PSD 的文件夹
 * 
 * @author roy
 */
public class PsdGroup extends WidgetGroup implements ParamProvider {
	// 文件夹的源
	private final psd.Folder psdFolder;

	public PsdGroup(PsdFile psdFile) {
		this(psdFile, getAssetManager(), null);
	}

	public PsdGroup(psd.Folder psdFolder, AssetManager assetManager) {
		this(psdFolder, assetManager, null);
	}

	public PsdGroup(psd.Folder psdFolder, AssetManager assetManager, PsdReflectListener listener) {
		this.psdFolder = psdFolder;
		setName(psdFolder.layerName);
		setParams(psdFolder.params);
		PsdReflectUtil.setBounds(psdFolder, this);
		//
		for (psd.Element element : this.psdFolder.childs) {
			try {
				Actor actor = null;
				if (listener != null) {
					actor = listener.onReflectElement(PsdGroup.this, element, assetManager);
				} else {
					actor = PsdReflectUtil.toGdxActor(element, assetManager, null);
				}
				if (actor != null) {
					PsdReflectUtil.setBounds(element, actor);
					addActor(actor);
					if (actor instanceof ParamProvider) {
						((ParamProvider) actor).setParams(element.getParams());
					}
				}
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

	@SuppressWarnings("unchecked")
	public <T extends Actor> T findActor(final String name, int index) {
		if (name.indexOf('/') == -1) {
			return super.findActor(name);
		} else {
			return (T) getActorByPath(name, index);
		}
	}

	// 根据路径查找对象
	public final Actor getActorByPath(String path, int index) {
		if (path != null) {
			return getActorByPath(path.split("/"), index);
		}
		return null;
	}

	// 根据路径查找对象
	private final Actor getActorByPath(String[] paths, int index) {
		Group group = this;
		Actor rt = null;
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (path == null || path.length() == 0) {
				continue;
			} else {
				if (i == paths.length - 1) {
					rt = getChild(group, paths[i], index);
				} else {
					rt = getChild(group, paths[i], 0);
				}
				//
				if (rt != null && rt instanceof Group) {
					group = (Group) rt;
				} else if (i != paths.length - 1) {
					return null;
				}
			}
		}
		return rt;
	}

	// 根据名称查找子对象
	private static final Actor getChild(Group group, String name, int index) {
		int counter = 0;
		for (Actor actor : group.getChildren()) {
			if (name.equals(actor.getName())) {
				if (counter == index) {
					return actor;
				} else {
					counter++;
				}
			}
		}
		return null;
	}

	// 过滤元素
	public final List<Actor> filter(psd.utils.Filter<Actor> filter) {
		List<Actor> actors = new ArrayList<Actor>();
		filter(this, filter, actors);
		return actors;
	}

	// 过滤元素
	private final void filter(Actor actor, psd.utils.Filter<Actor> filter, List<Actor> out) {
		if (filter.accept(actor)) {
			out.add(actor);
		} else if (actor instanceof Group) {
			for (Actor cActor : ((Group) actor).getChildren()) {
				filter(cActor, filter, out);
			}
		}
	}

	//
	public psd.PsdFile getPsdFile() {
		if (psdFolder != null && psdFolder instanceof PsdFile) {
			return (PsdFile) psdFolder;
		}
		return (PsdFile) psdFolder;
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

	//
	private List<Param> params;

	@Override
	public void setParams(List<Param> params) {
		this.params = params;
	}

	@Override
	public List<Param> getParams() {
		return params;
	}

	@Override
	public Param getParam(String key) {
		if (params != null) {
			for (Param param : params) {
				if (key.equals(param.getId())) {
					return param;
				}
			}
		}
		return null;
	}

}
