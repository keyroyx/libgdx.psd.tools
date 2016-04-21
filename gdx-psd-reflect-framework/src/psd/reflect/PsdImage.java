package psd.reflect;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import psd.framework.PsdReflectUtil;

/**
 * PSD 的图片对象
 * 
 * @author roy
 */
public class PsdImage extends Image {
	// 自动拉伸到 PSD设定大小 全局
	private static boolean defAutoScale = false;
	// 自动拉伸到 PSD设定大小
	protected boolean autoScale = defAutoScale;
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
		super(getTexture(psdFile, pic, assetManager, defAutoScale));
		this.psdPic = pic;
	}

	//
	public psd.Pic getPsdPic() {
		return psdPic;
	}

	// 读取图片
	protected static final TextureRegion getTexture(psd.PsdFile psdFile, psd.Pic pic,
			AssetManager assetManager, boolean scaleToPicSize) {
		if (assetManager == null) {
			assetManager = PsdGroup.getAssetManager();
		}
		TextureRegion region = null;

		if (psdFile == null || psdFile.atlas == null) {
			Texture texture = PsdReflectUtil.load(assetManager, pic.textureName, Texture.class);
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			region = new TextureRegion(texture);
		} else {
			TextureAtlas textureAtlas = PsdReflectUtil.load(assetManager, psdFile.getAtlasPath(),
					TextureAtlas.class);
			AtlasRegion atlasRegion = textureAtlas.findRegion(pic.textureName);
			if (atlasRegion != null) {
				atlasRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				region = new TextureRegion(atlasRegion);
			}
		}

		// 拉伸图片
		if (region != null && scaleToPicSize && pic.width > 0 && pic.height > 0) {
			float scaleX = region.getRegionWidth() / pic.width;
			float scaleY = region.getRegionHeight() / pic.height;
			Sprite sprite = new Sprite(region);
			sprite.setScale(scaleX, scaleY);
			//
			region = sprite;
		}

		return region;
	}

	// 自动拉伸到 PSD设定大小 全局
	public static void setDefAutoScale(boolean defAutoScale) {
		PsdImage.defAutoScale = defAutoScale;
	}

	// 自动拉伸到 PSD设定大小
	public void setAutoScale(boolean autoScale) {
		this.autoScale = autoScale;
	}
}
