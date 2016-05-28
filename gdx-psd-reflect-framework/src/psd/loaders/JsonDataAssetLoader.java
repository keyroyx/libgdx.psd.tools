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
	// 对象
	protected Json json;

	//
	public JsonDataAssetLoader(FileHandleResolver resolver, final Class<T> clazz) {
		super(resolver);
		this.clazz = clazz;
	}

	@Override
	// 同步加载
	public T loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<T> parameter) {
		T t = null;
		try {
			t = json.toObject(clazz);
			json = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<T> parameter) {
		json = new Json(file.read());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<T> parameter) {
		return null;
	}

}
