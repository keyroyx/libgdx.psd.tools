package psd.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import psd.PsdFile;

@SuppressWarnings("rawtypes")
public class FileManage {
	// 默认的资源加载器
	private static AssetManagerProxy assetManager;

	/** 获取资源加载器 **/
	public static final AssetManager getAssetManager() {
		if (assetManager == null) {
			assetManager = new AssetManagerProxy();
		}
		return assetManager;
	}

	/** 设置资源加载的文档适配器 , 用于数据的解密操作 **/
	public static final void setHandleResolver(FileHandleResolver fileHandleResolver) {
		assetManager = new AssetManagerProxy(fileHandleResolver);
	}

	/** 标记资源 */
	public static final void mark(String tag) {
		if (assetManager != null) {
			assetManager.mark(tag);
		}
	}

	/** 获取标记资源 */
	public static Mark getCurrentMark() {
		if (assetManager != null) {
			return assetManager.getCurrentMark();
		}
		return null;
	}

	/** 释放资源 */
	public static void unload(List<AssetDescriptor> descriptors) {
		if (assetManager != null) {
			assetManager.unload(descriptors);
		}
	}

	/** 加载资源 */
	public static void load(List<AssetDescriptor> descriptors) {
		if (assetManager != null) {
			assetManager.load(descriptors);
		}
	}

	/** 重新加载图片 , 用于解决返回时 图片丢失的问题 */
	public synchronized void reload(List<String> textures) {
		if (assetManager != null) {
			assetManager.reload(textures);
		}
	}

	/** 重新加载图片 , 用于解决返回时 图片丢失的问题 */
	public synchronized void reload(Mark mark) {
		if (assetManager != null) {
			List<AssetDescriptor> descriptors = mark.filter(Texture.class);
			assetManager.load(descriptors);
		}
	}

	/**
	 * 资源加载器的代理,用于监听数据加载状况 <br>
	 * 功能 1 , 记录当前加载状态<br>
	 * 功能 2 , 设置资源加载记录点<br>
	 * 功能 3 , 批量恢复加载资源
	 */
	private static class AssetManagerProxy extends AssetManager {
		private Stack<Mark> markTags = new Stack<Mark>();
		private Mark currentMark;

		public AssetManagerProxy() {
			this(new InternalFileHandleResolver());
		}

		public AssetManagerProxy(FileHandleResolver fileHandleResolver) {
			super(fileHandleResolver);
			initLoader(fileHandleResolver);
		}

		private final void initLoader(FileHandleResolver resolver) {
			setLoader(PsdFile.class, new PsdFileAssetsLoader(resolver));
		}

		private final void mark(String tag) {
			Mark markTag = new Mark(tag);
			markTags.push(markTag);
			currentMark = markTag;
		}

		private Mark getCurrentMark() {
			return currentMark;
		}

		public final synchronized <T> void load(String fileName, Class<T> type,
				AssetLoaderParameters<T> parameter) {
			super.load(fileName, type, parameter);
			if (currentMark != null) {
				currentMark.record(fileName, type, parameter);
			}
		}

		public synchronized void unload(List<AssetDescriptor> descriptors) {
			for (AssetDescriptor assetDescriptor : descriptors) {
				unload(assetDescriptor.fileName);
			}
		}

		public synchronized void load(List<AssetDescriptor> descriptors) {
			for (AssetDescriptor assetDescriptor : descriptors) {
				load(assetDescriptor);
			}
		}

		public synchronized void reload(List<String> textures) {
			for (String texture : textures) {
				assetManager.load(texture, Texture.class);
			}
		}
	}

	public static class Mark {
		// 资源
		private Array<AssetDescriptor> elements = new Array<AssetDescriptor>(50);
		// 标签名称
		private final String tag;

		public Mark(String tag) {
			this.tag = tag;
		}

		// 记录
		@SuppressWarnings("unchecked")
		private final void record(String fileName, Class<?> type, AssetLoaderParameters<?> parameter) {
			for (AssetDescriptor assetDescriptor : elements) {
				if (assetDescriptor.fileName.equals(fileName) && assetDescriptor.type.equals(type)) {
					return;
				}
			}
			elements.add(new AssetDescriptor(fileName, type, parameter));
		}

		/** 过滤标记资源 */
		public final List<AssetDescriptor> filter(Filter<AssetDescriptor> filter) {
			List<AssetDescriptor> list = new ArrayList<AssetDescriptor>();
			for (AssetDescriptor assetDescriptor : elements) {
				if (filter.accept(assetDescriptor)) {
					list.add(assetDescriptor);
				}
			}
			return list;
		}

		/** 过滤标记资源 */
		public final List<AssetDescriptor> filter(Filter<String> filter, Class<?> clazz) {
			List<AssetDescriptor> list = new ArrayList<AssetDescriptor>();
			for (AssetDescriptor assetDescriptor : elements) {
				if (clazz.equals(assetDescriptor.type)
						&& (filter == null || filter.accept(assetDescriptor.fileName))) {
					list.add(assetDescriptor);
				}
			}
			return list;
		}

		/** 过滤标记资源 */
		public final List<AssetDescriptor> filter(Class<?> clazz) {
			return filter(null, clazz);
		}

		// 获取标签名称
		public final String getTag() {
			return tag;
		}

		// 获取记录的资源列表
		public final Array<AssetDescriptor> getElements() {
			return elements;
		}
	}
}
