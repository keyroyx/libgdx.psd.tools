package gdx.keyroy.data.tools.models;

import gdx.keyroy.psd.tools.util.TextureUnpacker;

import java.io.File;

import javax.imageio.ImageIO;

import com.keyroy.util.json.JsonAn;

//类 文件的描述
public class ResoucePath {
	// 资源文件名称
	protected String name;
	// 资源文件 文件夹
	protected String folder;

	@JsonAn(skip = true)
	protected TextureUnpacker unpacker;

	public ResoucePath() {

	}

	public ResoucePath(File folder, File file) {
		if (folder != null) {
			this.folder = folder.getAbsolutePath();
			this.name = file.getAbsolutePath().replace(folder.getAbsolutePath() + File.separator, "");
		} else {
			this.name = file.getPath();
		}
	}

	public final String getFolder() {
		return folder;
	}

	public boolean exist() {
		return new File(getFilePath()).exists();
	}

	public boolean isAtlas() {
		return getAssetsPath().endsWith(".atlas");
	}

	public boolean isImage() {
		try {
			ImageIO.read(getFile());
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isTmx() {
		return getAssetsPath().endsWith(".tmx");
	}

	public File getFile() {
		if (folder != null) {
			return new File(folder, name);
		} else {
			return new File(name);
		}
	}

	// 文件的地址
	public String getFilePath() {
		return getFile().getAbsolutePath();
	}

	public final String getAssetsPath() {
		return name;
	}

	public TextureUnpacker getUnpacker() {
		if (unpacker == null) {
			File file = new File(getFilePath());
			unpacker = new TextureUnpacker(file, file.getParentFile(), false);
		}
		return unpacker;
	}

	public String getFolderName() {
		return new File(folder).getName();
	}

}
