package psd;

/***
 * PSD µÄÍ¼Æ¬
 * 
 * @author roy
 */
public class Pic extends Element {
	// Í¼Æ¬Ãû³Æ
	public String textureName;

	@Override
	protected void onParam(String paramString) {
		super.onParam(paramString);
		textureName = textureName.replace(paramString, "").replace("@", "");
	}
}
