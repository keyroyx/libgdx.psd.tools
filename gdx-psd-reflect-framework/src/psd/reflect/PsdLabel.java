package psd.reflect;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import psd.Param;
import psd.ParamProvider;

/**
 * PSD 的文本对象
 * 
 * @author roy
 */
public class PsdLabel extends Label implements ParamProvider {
	// 文本源
	protected final psd.Text psdText;

	public PsdLabel(psd.Text psdText) {
		super(psdText.text, getLabelStyle(psdText));
		this.psdText = psdText;
	}

	//
	public psd.Text getPsdText() {
		return psdText;
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

	private static final LabelStyle getLabelStyle(psd.Text psdText) {
		Label.LabelStyle style = new LabelStyle(new BitmapFont(),
				new Color(psdText.r, psdText.g, psdText.b, psdText.a));
		return style;
	}
}
