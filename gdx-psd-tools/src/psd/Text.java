package psd;

/***
 * PSD 的文字
 * 
 * @author roy
 */
public class Text extends Element {
	// 显示文字
	public String text;
	// 文字颜色
	public float a, r, g, b;
	// 文字大小
	public int fontSize;

	public void setPsdText(String text, float a, float r, float g, float b, int fontSize) {
		this.text = text;
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
		this.fontSize = fontSize;
	}
}
