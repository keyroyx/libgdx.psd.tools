package gdx.keyroy.psd.tools.models;

import gdx.keyroy.psd.tools.util.FileUtil;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Message;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import com.keyroy.util.json.Json;

public class EditorConfig {
	// 导出路径
	public static String export_path;
	// 使用 LIBGDX 的坐标系
	public static boolean used_libgdx_coordinate;
	// 使用 TexturePacker 打包图片
	public static boolean used_texture_packer;
	// 使用 Android Assets 名称规范
	public static boolean used_android_assets_name;

	public static final void load() {
		try {
			File file = getFile();
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				Json json = new Json(inputStream);
				inputStream.close();
				json.toObject(EditorConfig.class);
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (export_path == null) {
			export_path = new File(FileUtil.getRoot(), "out").getPath();
		}
	}

	public static final void save() {
		try {
			Json json = new Json(new EditorConfig());
			String text = json.toString();
			System.out.println("save config : " + text);
			FileUtil.save(getFile(), text);
			Message.send(L.get("Message.config_save") + "    " + new Date().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final File getFile() {
		return new File(EditorConfig.class.getSimpleName());
	}
}
