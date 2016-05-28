package psd.lg0311.params;

import java.util.List;

import com.badlogic.gdx.utils.Array;

public class Sound {
	public String na;
	public List<String> nas;
	public String path;
	public List<String> paths;

	public final String getPath() {
		if (path != null) {
			return path;
		} else if (na != null) {
			return "sounds/" + na;
		}
		return null;
	}

	public final void getPath(Array<String> list) {
		if (na != null) {
			list.add("sounds/" + na);
		}
		if (path != null) {
			list.add(path);
		}

		if (nas != null) {
			for (String na : nas) {
				list.add("sounds/" + na);
			}
		}

		if (paths != null) {
			for (String path : paths) {
				list.add(path);
			}
		}
	}
}
