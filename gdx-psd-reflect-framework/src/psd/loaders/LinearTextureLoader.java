package psd.loaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class LinearTextureLoader extends com.badlogic.gdx.assets.loaders.TextureLoader {
	public LinearTextureLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Texture loadSync(AssetManager manager, String fileName, FileHandle file,
			TextureParameter parameter) {
		Texture texture = super.loadSync(manager, fileName, file, parameter);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return texture;
	}

}
