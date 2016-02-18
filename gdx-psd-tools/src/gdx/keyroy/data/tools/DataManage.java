package gdx.keyroy.data.tools;

import gdx.keyroy.data.tools.models.ClassElement;
import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.data.tools.models.ImagePath;
import gdx.keyroy.psd.tools.util.FileUtil;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Messager;

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
	@JsonAn(skip = true)
	private static List<ClassPath> classPaths = new ArrayList<ClassPath>();
	// 图片数据
	private static List<ImagePath> imagePaths = new ArrayList<ImagePath>();

	// 添加新的类
	public static final void addClass(Class<?> clazz, boolean autoSave) {
		for (ClassPath classPath : classPaths) {
			if (classPath.getClassName().equals(clazz.getName())) {
				return;
			}
		}
		ClassPath classPath = new ClassPath(clazz);
		classPaths.add(classPath);
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
				json.toObject(DataManage.class);
				inputStream.close();
				//
			}
			// 加载 ClassPath
			for (ClassPath classPath : DataManage.classPaths) {
				File folder = getFile(classPath);
				if (folder.exists()) {
					Class<?> clazz = classPath.getClazz();
					File[] files = folder.listFiles();
					for (File jsonFile : files) {
						try {
							FileInputStream stream = new FileInputStream(jsonFile);
							Json j = new Json(stream);
							stream.close();
							Object object = j.toObject(clazz);
							ClassElement classElement = new ClassElement();
							classElement.setObjId(jsonFile.getName());
							classElement.setObject(object);
							classPath.addElement(classElement);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
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
				Messager.send(L.get("Message.data_save") + "    " + new Date().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void reset(ClassElement classElement, String id) {
		try {
			delete(classElement);
			classElement.setObjId(id);
			save(classElement);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static final void delete(ClassElement classElement) {

		ClassPath classPath = classElement.getParent();
		// 删除元素
		classPath.delElement(classElement);
		// 删除文件
		File folder = getFile(classPath);
		File file = new File(folder, classElement.getObjId());
		file.delete();

	}

	public static final void save(ClassElement classElement) {
		try {
			ClassPath classPath = classElement.getParent();
			File folder = getFile(classPath);
			if (folder.exists() == false) {
				folder.mkdirs();
			}
			File jsonFile = new File(folder, classElement.getObjId());
			jsonFile.createNewFile();
			FileUtil.save(jsonFile, new Json(classElement.getObject()).toString());
			Messager.send(L.get("message.class_element_save") + "  " + classPath.getClassName() + "  "
					+ classElement.getObjId() + "  " + new Date().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void save(List<ClassElement> classElements) {
		for (ClassElement classElement : classElements) {
			save(classElement);
		}
	}

	private static final File getFile() {
		return new File(DataManage.class.getSimpleName());
	}

	private static final File getFile(ClassPath classPath) {
		return new File(classPath.getClassName());
	}

	public static final List<ClassPath> getClassPaths() {
		return classPaths;
	}

	public static final List<ImagePath> getImagePaths() {
		return imagePaths;
	}
}
