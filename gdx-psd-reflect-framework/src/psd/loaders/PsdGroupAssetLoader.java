package psd.loaders;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import psd.Element;
import psd.Pic;
import psd.PsdFile;
import psd.reflect.PsdGroup;
import psd.utils.ElementFilter;

@SuppressWarnings("rawtypes")
public class PsdGroupAssetLoader extends AsynchronousAssetLoader<PsdGroup, AssetLoaderParameters<PsdGroup>> {
	// ∂‘œÛ
	PsdFile psdFile;

	//
	public PsdGroupAssetLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<PsdGroup> parameter) {
		psdFile = new PsdFile(file);
	}

	@Override
	public PsdGroup loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<PsdGroup> parameter) {
		PsdGroup psdGroup = new PsdGroup(psdFile);
		psdFile = null;
		return psdGroup;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<PsdGroup> parameter) {
		PsdFile psdFile = new PsdFile(file);
		List<Element> images = psdFile.filter(new ElementFilter() {

			@Override
			public boolean accept(Element element) {
				return element instanceof Pic;
			}
		});
		Array<AssetDescriptor> array = new Array<AssetDescriptor>();
		for (Element element : images) {
			Pic pic = (Pic) element;
			AssetDescriptor descriptor = null;
			if (psdFile == null || psdFile.atlas == null) {
				descriptor = new AssetDescriptor<>(pic.textureName, Texture.class);
			} else {
				descriptor = new AssetDescriptor<>(psdFile.getAtlasPath(), TextureAtlas.class);
			}
			array.add(descriptor);
		}

		return array;
	}

}
