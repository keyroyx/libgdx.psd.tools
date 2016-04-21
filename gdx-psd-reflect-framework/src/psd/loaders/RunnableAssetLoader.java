package psd.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import psd.loaders.RunnableAssetLoader.ProgressParameter;

@SuppressWarnings("rawtypes")
public class RunnableAssetLoader extends AsynchronousAssetLoader<Runnable, ProgressParameter> {

	public RunnableAssetLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			ProgressParameter parameter) {
		parameter.runnable.run();
	}

	@Override
	public Runnable loadSync(AssetManager manager, String fileName, FileHandle file,
			ProgressParameter parameter) {
		parameter.runnable.run();
		return parameter.runnable;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			ProgressParameter parameter) {
		return null;
	}

	static public class ProgressParameter extends AssetLoaderParameters<Runnable> {
		private Runnable runnable;

		public ProgressParameter(Runnable runnable) {
			this.runnable = runnable;
		}
	}

}
