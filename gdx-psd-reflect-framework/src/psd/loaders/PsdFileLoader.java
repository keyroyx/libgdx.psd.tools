package psd.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import psd.PsdFile;
import psd.loaders.PsdFileLoader.PsdFileParameter;

//
public class PsdFileLoader extends AsynchronousAssetLoader<PsdFile, PsdFileParameter> {
	//
	public PsdFileLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			PsdFileParameter parameter) {
	}

	@Override
	public PsdFile loadSync(AssetManager manager, String fileName, FileHandle file,
			PsdFileParameter parameter) {
		return new PsdFile(file);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			PsdFileParameter parameter) {
		return null;
	}

	static public class PsdFileParameter extends AssetLoaderParameters<PsdFile> {
	}

}
