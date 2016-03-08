package psd;

import com.badlogic.gdx.files.FileHandle;
import com.keyroy.util.json.Json;

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
	public FileHandle handle;

	public PsdFile() {
	}

	public PsdFile(FileHandle handle) {
		try {
			Json.fill(this, handle.read());
			this.handle = handle;
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
