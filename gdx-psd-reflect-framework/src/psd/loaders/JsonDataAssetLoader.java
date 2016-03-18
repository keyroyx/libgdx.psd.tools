package psd.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.keyroy.util.json.Json;

//JSON 数据加载器
public class JsonDataAssetLoader<T> extends AsynchronousAssetLoader<T, AssetLoaderParameters<T>> {
	// 目标类
	protected final Class<T> clazz;

	//
	public JsonDataAssetLoader(FileHandleResolver resolver, final Class<T> clazz) {
		super(resolver);
		this.clazz = clazz;
	}

	@Override
	// 同步加载
	public T loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<T> parameter) {
		try {
			return new Json(file.read()).toObject(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<T> parameter) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<T> parameter) {
		return null;
	}

}
