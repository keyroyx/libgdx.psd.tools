package psd.reflect;

import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import psd.Param;
import psd.ParamProvider;
import psd.framework.PsdReflectUtil;

/**
 * PSD 的图片对象
 * 
 * @author roy
 */
public class PsdImage extends Image implements ParamProvider {
	// 图片源
	protected final psd.Pic psdPic;

	public PsdImage(psd.Pic pic) {
		this(pic, null);
	}

	public PsdImage(psd.Pic pic, AssetManager assetManager) {
		super(getTexture(pic.getPsdFile(), pic, assetManager));
		this.psdPic = pic;
	}

	//
	public psd.Pic getPsdPic() {
		return psdPic;
	}

	private List<Param> params;

	@Override
	public void setParams(List<Param> params) {
		this.params = params;
	}

	@Override
	public List<Param> getParams() {
		return params;
	}

	@Override
	public Param getParam(String key) {
		if (params != null) {
			for (Param param : params) {
				if (key.equals(param.getId())) {
					return param;
				}
			}
		}
		return null;
	}

	// 读取图片
	protected static final TextureRegion getTexture(psd.PsdFile psdFile, psd.Pic pic,
			AssetManager assetManager) {
		if (assetManager == null) {
			assetManager = PsdGroup.getAssetManager();
		}
		TextureRegion region = null;

		if (psdFile == null || psdFile.atlas == null) {
			if (pic.textureName != null && pic.textureName.length() > 0) {
				Texture texture = PsdReflectUtil.load(assetManager, pic.textureName, Texture.class);
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				region = new TextureRegion(texture);
			}
		} else {
			TextureAtlas textureAtlas = PsdReflectUtil.load(assetManager, psdFile.getAtlasPath(),
					TextureAtlas.class);
			AtlasRegion atlasRegion = textureAtlas.findRegion(pic.textureName);
			if (atlasRegion != null) {
				atlasRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				region = new TextureRegion(atlasRegion);
			}
		}

		return region;
	}
}
