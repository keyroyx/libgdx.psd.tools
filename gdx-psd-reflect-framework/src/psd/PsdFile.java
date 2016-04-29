package psd;

import com.badlogic.gdx.files.FileHandle;
import com.keyroy.util.json.Json;
import com.keyroy.util.json.JsonAn;

/**
 * PSD 的文件描述
 * 
 * @author roy
 */
public class PsdFile extends Folder {

	// 最大大小
	public int maxWidth, maxHeight;
	// 文件名
	public String psdName;
	// 图片集
	public String atlas;
	// 文件句柄
	@JsonAn(skip = true)
	public FileHandle handle;

	public PsdFile() {
		this.layerName = psdName;
	}

	public PsdFile(FileHandle handle) {
		try {
			Json.fill(this, handle.read());
			updateParam();
			this.handle = handle;
			this.layerName = psdName;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAtlasPath() {
		if (handle != null) {
			String path = handle.parent().path().replace("\\", "/");
			if (path.length() > 0) {
				return path + "/" + atlas;
			}
		}
		return atlas;
	}
}
