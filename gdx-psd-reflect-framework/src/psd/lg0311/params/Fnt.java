package psd.lg0311.params;

public class Fnt {
	// 文件名称 , 这个不是全名 , 比如 aaa.fnt , 写作 aaa
	public String na;
	// 文件全路径
	public String path;

	public final String getPath() {
		if (path != null) {
			return path;
		} else if (na != null) {
			return "fonts/" + na;
		}
		return null;
	}
}
