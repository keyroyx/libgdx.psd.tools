package gdx.keyroy.data.tools.models;

import java.io.File;

//类 文件的描述
public class ImagePath {
	// 文件的地址
	protected String filePath;

	public ImagePath() {

	}

	public ImagePath(File file) {
		this.filePath = file.getPath();
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

}
