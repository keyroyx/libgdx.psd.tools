package psd.reflect;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

/**
 * PSD µƒÕº∆¨∂‘œÛ
 * 
 * @author roy
 */
public class PsdImage extends Image {
	// Õº∆¨‘¥
	protected final psd.Pic psdPic;

	public PsdImage(psd.Pic pic, AssetManager assetManager) {
		super(getTexture(pic, assetManager));
		this.psdPic = pic;
	}

	public PsdImage(psd.PsdFile psdFile, psd.Pic pic, AssetManager assetManager) {
		super(getTexture(psdFile, pic, assetManager));
		this.psdPic = pic;
	}

	//
	public psd.Pic getPsdPic() {
		return psdPic;
	}

	// ∂¡»°Õº∆¨
	private static final Texture getTexture(psd.Pic pic, AssetManager assetManager) {
		Texture texture = PsdElement.load(assetManager, pic.textureName, Texture.class);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return texture;
	}

	// ∂¡»°Õº∆¨
	private static final TextureRegion getTexture(psd.PsdFile psdFile, psd.Pic pic, AssetManager assetManager) {
		TextureAtlas textureAtlas = PsdElement.load(assetManager, psdFile.psdName + ".atlas",
				TextureAtlas.class);
		Array<AtlasRegion> array = textureAtlas.getRegions();
		for (AtlasRegion atlasRegion : array) {
			if (atlasRegion.name.equals(pic.textureName)) {
				atlasRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				return atlasRegion;
			}
		}
		return null;
	}
}
