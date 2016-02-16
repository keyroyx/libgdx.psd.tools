package gdx.keyroy.data.tools.util;

import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.data.tools.models.ImagePath;
import gdx.keyroy.psd.tools.util.FileUtil;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Message;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.keyroy.util.json.Json;
import com.keyroy.util.json.JsonAn;

// 数据
public class DataManage {
	@JsonAn(skip = true)
	private static int hashCode;
	// 类数据
	private static List<ClassPath> classPaths = new ArrayList<ClassPath>();
	// 图片数据
	private static List<ImagePath> imagePaths = new ArrayList<ImagePath>();

	// 添加新的类
	public static final void addClass(Class<?> clazz, boolean autoSave) {
		addClass((File) null, clazz, autoSave);
	}

	// 添加新的类
	public static final void addClass(File jarFile, Class<?> clazz, boolean autoSave) {
		for (ClassPath classPath : classPaths) {
			if (classPath.getClassName().equals(clazz.getName())) {
				return;
			}
		}
		classPaths.add(new ClassPath(jarFile, clazz));
		if (autoSave) {
			save();
		}
	}

	public static final void addImage(File imageFile, boolean autoSave) {
		_addImage(imageFile);
		if (autoSave) {
			save();
		}
	}

	private static final void _addImage(File file) {
		if (file != null && file.exists()) {
			for (ImagePath imagePath : imagePaths) {
				if (imagePath.getFilePath().equals(file.getPath())) {
					return;
				}
			}
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (File cFile : files) {
						_addImage(cFile);
					}
				}
			} else if (file.getName().toString().endsWith(".atlas")) {
				// 检查图片在不在

				imagePaths.add(new ImagePath(file));
			} else {
				try {
					FileInputStream inputStream = new FileInputStream(file);
					ImageIO.read(inputStream);
					inputStream.close();
					imagePaths.add(new ImagePath(file));
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 
	 * 
	 * 
	 */
	public static final void load() {
		try {
			File file = getFile();
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				Json json = new Json(inputStream);
				inputStream.close();
				json.toObject(DataManage.class);
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void save() {
		try {
			Json json = new Json(new DataManage());
			String text = json.toString();
			if (text.hashCode() != hashCode) {
				hashCode = text.hashCode();
				System.out.println("save data");
				FileUtil.save(getFile(), text);
				Message.send(L.get("Message.data_save") + "    " + new Date().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final File getFile() {
		return new File(DataManage.class.getSimpleName());
	}

	public static final List<ClassPath> getClassPaths() {
		return classPaths;
	}

	public static final List<ImagePath> getImagePaths() {
		return imagePaths;
	}
}
