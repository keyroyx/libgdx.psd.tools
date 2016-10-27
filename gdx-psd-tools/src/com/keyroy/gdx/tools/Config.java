package com.keyroy.gdx.tools;

import java.io.File;
import java.io.FileInputStream;

import com.keyroy.util.json.Json;

import gdx.keyroy.psd.tools.util.FileUtil;

public class Config {
	// 打包前 清空文件夹
	public static boolean cleanFolder;
	// 保存图片
	public static boolean saveImage;
	// 使用 TexturePacker 打包图片
	public static boolean saveAtlas;
	// 支持旋转图片
	public static boolean rotateImage;
	// 格式化图层名称
	public static boolean formatLayerName;

	public static final void load() {
		try {
			File file = getFile();
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				Json json = new Json(inputStream);
				inputStream.close();
				json.toObject(Config.class);
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void save() {
		try {
			Json json = new Json(new Config());
			String text = json.toString();
			FileUtil.save(getFile(), text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final File getFile() {
		return new File(".conf");
	}
}
