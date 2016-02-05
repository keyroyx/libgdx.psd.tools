package psd;

import library.psd.parser.object.PsdText;

public class Text extends Element {
	/** The value. */
	public String text;

	public float a;
	public float r;
	public float g;
	public float b;
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
