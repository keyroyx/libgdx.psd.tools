package psd.reflect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * PSD 的文本对象
 * 
 * @author roy
 */
public class PsdLabel extends Label {
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

	private static final LabelStyle getLabelStyle(psd.Text psdText) {
		Label.LabelStyle style = new LabelStyle(new BitmapFont(),
				new Color(psdText.r, psdText.g, psdText.b, psdText.a));
		try {
			// GDX 的默认大小为 15pt ,com/badlogic/gdx/utils/arial-15.png
			if (style.font != null) {
				style.font.scale((float) psdText.fontSize / 15f);
			}
		} catch (Exception e) {
		}
		return style;
	}
}
