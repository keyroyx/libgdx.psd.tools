package psd;

import library.psd.parser.object.PsdText;

/***
 * PSD 的文字
 * @author roy
 */
public class Text extends Element {
	// 显示文字
	public String text;
	// 文字颜色
	public float a, r, g, b;
	// 文字大小
	public int fontSize;

	public void setPsdText(PsdText psdText) {
		this.text = psdText.value;
		this.a = psdText.a;
		this.r = psdText.r;
		this.g = psdText.g;
		this.b = psdText.b;
		this.fontSize = psdText.fontSize;
	}
}
