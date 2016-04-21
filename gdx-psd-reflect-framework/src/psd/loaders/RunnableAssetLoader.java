package psd.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import psd.loaders.RunnableAssetLoader.RunnableParameter;

@SuppressWarnings("rawtypes")
public class RunnableAssetLoader extends AsynchronousAssetLoader<Runnable, RunnableParameter> {

	public RunnableAssetLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			RunnableParameter parameter) {
		try {
			parameter.runnable.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Runnable loadSync(AssetManager manager, String fileName, FileHandle file,
			RunnableParameter parameter) {
		return parameter.runnable;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			RunnableParameter parameter) {
		return null;
	}

	static public class RunnableParameter extends AssetLoaderParameters<Runnable> {
		private Runnable runnable;

		public RunnableParameter(Runnable runnable) {
			this.runnable = runnable;
		}
	}

}
