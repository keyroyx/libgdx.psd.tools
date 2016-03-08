package psd.reflect;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * PSD 的图片对象
 * 
 * @author roy
 */
public class PsdImage extends Image {
	// 图片源
	protected final psd.Pic psdPic;

	public PsdImage(psd.Pic pic) {
		this(pic, null);
	}

	public PsdImage(psd.Pic pic, AssetManager assetManager) {
		this(null, pic, assetManager);
	}

	public PsdImage(psd.PsdFile psdFile, psd.Pic pic) {
		this(psdFile, pic, null);
	}

	public PsdImage(psd.PsdFile psdFile, psd.Pic pic, AssetManager assetManager) {
		super(getTexture(psdFile, pic, assetManager));
		this.psdPic = pic;
	}

	//
	public psd.Pic getPsdPic() {
		return psdPic;
	}

	// 读取图片
	protected static final TextureRegion getTexture(psd.PsdFile psdFile, psd.Pic pic,
			AssetManager assetManager) {
		if (assetManager == null) {
			assetManager = PsdGroup.getAssetManager();
		}
		if (psdFile == null || psdFile.atlas == null) {
			Texture texture = PsdElement.load(assetManager, pic.textureName, Texture.class);
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			return new TextureRegion(texture);
		} else {
			TextureAtlas textureAtlas = PsdElement.load(assetManager, psdFile.getAtlasPath(),
					TextureAtlas.class);
			AtlasRegion atlasRegion = textureAtlas.findRegion(pic.textureName);
			if (atlasRegion != null) {
				atlasRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				return atlasRegion;
			}
		}

		return null;
	}
}
