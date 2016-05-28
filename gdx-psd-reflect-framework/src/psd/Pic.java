package psd;

/***
 * PSD 的图片
 * 
 * @author roy
 */
public class Pic extends Element {
	// 图片名称
	public String textureName;

	@Override
	protected void onParam(String paramString) {
		super.onParam(paramString);
		textureName = textureName.replace(paramString, "").replace("@", "");
	}
}
