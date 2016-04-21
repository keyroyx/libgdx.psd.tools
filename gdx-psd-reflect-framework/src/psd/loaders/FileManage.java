package psd.loaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import psd.PsdFile;
import psd.loaders.PsdFileLoader.PsdFileParameter;
import psd.loaders.RunnableAssetLoader.RunnableParameter;
import psd.utils.Filter;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FileManage {
	// 默认的 文件处理句柄
	private static FileHandleResolver fileHandleResolver;
	// 默认的资源加载器
	private static AssetManagerProxy assetManager;

	/** 加载文件资源 **/
	public static final FileHandle file(String fileName) {
		if (fileHandleResolver != null) {
			return fileHandleResolver.resolve(fileName);
		}
		return Gdx.files.internal(fileName);
	}

	/** 获取资源加载器 **/
	public static final AssetManagerProxy getAssetManager() {
		if (assetManager == null) {
			assetManager = new AssetManagerProxy();
		}
		return assetManager;
	}

	/** 设置资源加载的文档适配器 , 用于数据的解密操作 **/
	public static final void setHandleResolver(FileHandleResolver resolver) {
		fileHandleResolver = resolver;
		assetManager = new AssetManagerProxy(resolver);

	}

	/** 标记资源 */
	public static final void mark(String tag) {
		getAssetManager().mark(tag);
	}

	/** 获取标记资源 */
	public static Mark getCurrentMark() {
		return getAssetManager().getCurrentMark();
	}

	/** 释放资源 */
	public static void unload(List<AssetDescriptor> descriptors) {
		getAssetManager().unload(descriptors);
	}

	/** 立即获取资源 */
	public static <T> T get(String fileName, Class<T> clazz) {
		AssetManager assetManager = getAssetManager();
		if (assetManager.isLoaded(fileName, clazz) == false) {
			assetManager.load(fileName, clazz);
			assetManager.finishLoading();
		}
		return assetManager.get(fileName, clazz);
	}

	/**
	 * 加载 PsdGroup 需要使用的资源
	 * 
	 * @param fileName
	 */
	public static void loadPsdResource(String fileName) {
		AssetManager assetManager = getAssetManager();
		if (assetManager.isLoaded(fileName, PsdFile.class) == false) {
			assetManager.load(fileName, PsdFile.class, new PsdFileParameter(true));
		}
	}

	/**
	 * 加载进度
	 * 
	 * @param runnable
	 */
	public static void load(Runnable runnable) {
		AssetManagerProxy assetManager = getAssetManager();
		assetManager.load(runnable);
	}

	/** 加载资源 */
	public static void load(String fileName, Class<?> clazz) {
		getAssetManager().load(fileName, clazz);
	}

	/** 加载资源 */
	public static void load(List<AssetDescriptor> descriptors) {
		getAssetManager().load(descriptors);
	}

	/** 查询是否加载了资源 */
	public static boolean isLoad(String fileName, Class<?> clazz) {
		return getAssetManager().isLoaded(fileName, clazz);
	}

	/** 重新加载图片 , 用于解决返回时 图片丢失的问题 */
	public static void reload(List<String> textures) {
		getAssetManager().reload(textures);
	}

	/** 重新加载图片 , 用于解决返回时 图片丢失的问题 */
	public static void reload(Mark mark) {
		if (mark != null) {
			List<AssetDescriptor> descriptors = mark.filter(Texture.class);
			getAssetManager().load(descriptors);
		}
	}

	/** 重新加载图片 , 用于解决返回时 图片丢失的问题 */
	public static void reload() {
		reload(getCurrentMark());
	}

	/** 设置文件加载器 */
	public static void setLoader(Class type, AssetLoader loader) {
		getAssetManager().setLoader(type, loader);
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
		private FileHandleResolver resolver;

		public AssetManagerProxy() {
			this(new InternalFileHandleResolver());
		}

		public AssetManagerProxy(FileHandleResolver resolver) {
			super(fileHandleResolver);
			this.resolver = resolver;
			initLoader();
		}

		private final void initLoader() {
			// 抗锯齿 的图片加载器
			setLoader(Texture.class, new LinearTextureLoader(resolver));
			// PsdFile 加载器
			setLoader(PsdFile.class, new PsdFileLoader(resolver));
			// TextureAtlas 的源 , 不知道为什么 , 使用默认的不行
			setLoader(TextureAtlas.class, new PsdTextureAtlasLoader(resolver));
			// 用于加载进度条
			setLoader(Runnable.class, new RunnableAssetLoader(resolver));

		}

		private final void mark(String tag) {
			Mark markTag = new Mark(tag);
			markTags.push(markTag);
			currentMark = markTag;
		}

		private Mark getCurrentMark() {
			return currentMark;
		}

		public final synchronized void load(Runnable runnable) {
			super.load(runnable.toString(), Runnable.class, new RunnableParameter(runnable));
		}

		public final synchronized <T> void load(String fileName, Class<T> type,
				AssetLoaderParameters<T> parameter) {
			// 检查 Loader是否存在 , 默认为 JSON 解析
			if (getLoader(type) == null) {
				setLoader(type, new JsonDataAssetLoader<T>(resolver, type));
			}

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
