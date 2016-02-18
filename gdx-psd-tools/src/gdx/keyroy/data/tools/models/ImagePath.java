package gdx.keyroy.data.tools.models;

import gdx.keyroy.psd.tools.util.TextureUnpacker;

import java.io.File;

import com.keyroy.util.json.JsonAn;

//类 文件的描述
public class ImagePath {
	// 文件的地址
	protected String filePath;

	@JsonAn(skip = true)
	protected TextureUnpacker unpacker;

	public ImagePath() {

	}

	public ImagePath(File file) {
		this.filePath = file.getPath();
	}

	public boolean exist() {
		return new File(filePath).exists();
	}

	public boolean isAtlas() {
		return filePath.endsWith(".atlas");
	}

	// 文件的地址
	public String getFilePath() {
		return filePath;
	}

	// 文件的地址
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public final String getFileName() {
		return new File(filePath).getName();
	}

	public TextureUnpacker getUnpacker() {
		if (unpacker == null) {
			File file = new File(filePath);
			unpacker = new TextureUnpacker(file, file.getParentFile(), false);
		}
		return unpacker;
	}

}
