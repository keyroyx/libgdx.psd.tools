package gdx.keyroy.data.tools.models;

import java.io.File;

//类 文件的描述
public class ClassPath {
	// jar 文件的地址
	protected String jarPath;
	// class 的名称
	protected String className;

	public ClassPath() {

	}

	public ClassPath(Class<?> clazz) {
		this.className = clazz.getName();
	}

	public ClassPath(File jarFile, Class<?> clazz) {
		if (jarFile != null) {
			this.jarPath = jarFile.getPath();
		}
		this.className = clazz.getName();
	}

	// 文件的地址
	public String getJarPath() {
		return jarPath;
	}

	// 文件的地址
	public String getClassName() {
		return className;
	}

	public final String getFileName() {
		return new File(className).getName().replace(".jar", "");
	}

}
