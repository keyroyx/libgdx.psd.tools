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
import psd.loaders.PsdFileLoader.PsdFileParameter;
import psd.utils.ElementFilter;

//
public class PsdFileLoader extends AsynchronousAssetLoader<PsdFile, PsdFileParameter> {
	//
	private PsdFile psdFile;

	public PsdFileLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			PsdFileParameter parameter) {
		psdFile = new PsdFile(file);
	}

	@Override
	public PsdFile loadSync(AssetManager manager, String fileName, FileHandle file,
			PsdFileParameter parameter) {
		return psdFile;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			PsdFileParameter parameter) {
		if (parameter != null && parameter.loadResource) {
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
					if (pic.textureName != null) {
						descriptor = new AssetDescriptor(pic.textureName, Texture.class);
					}
				} else {
					descriptor = new AssetDescriptor(psdFile.getAtlasPath(), TextureAtlas.class);
				}
				array.add(descriptor);
			}
			return array;
		}
		return null;
	}

	static public class PsdFileParameter extends AssetLoaderParameters<PsdFile> {
		// 是否加载资源文件
		public boolean loadResource;

		public PsdFileParameter() {
		}

		public PsdFileParameter(boolean loadResource) {
			this.loadResource = loadResource;
		}
	}

}
