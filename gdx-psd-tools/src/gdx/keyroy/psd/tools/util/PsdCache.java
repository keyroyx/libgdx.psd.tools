package gdx.keyroy.psd.tools.util;

import java.io.File;
import java.util.Hashtable;

import library.psd.Psd;

public class PsdCache {
	private static final Hashtable<String, Psd> HASHTABLE = new Hashtable<>();

	public static final Psd get(File file) {
		return get(file.getAbsolutePath());
	}

	public static final Psd get(String name) {
		Psd psd = HASHTABLE.get(name);
		if (psd == null) {
			try {
				psd = new Psd(new File(name));
				PSDUtil.updateLayerParent(psd);
				HASHTABLE.put(name, psd);
			} catch (Exception e) {
			}
		}

		return psd;
	}
}
