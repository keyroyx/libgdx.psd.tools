package gdx.keyroy.psd.tools.util;

import gdx.keyroy.psd.tools.models.KeyVal;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileUtil {
	public static final List<KeyVal> readIni(File file) {
		try {
			List<KeyVal> list = new ArrayList<KeyVal>();
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
			String text = null;
			while ((text = reader.readLine()) != null) {
				try {
					if (text.startsWith("#")) { // ×¢ÊÍ
						continue;
					} else if (text.trim().length() > 0) {
						KeyVal keyVal = new KeyVal();
						list.add(keyVal);
						String[] args = text.split("=", 2);
						if (args != null && args.length > 1) {
							keyVal.setKey(args[0]);
							keyVal.setVal(args[1]);
						} else {
							keyVal.setKey(text);
						}
					}
				} catch (Exception e) {
				}
			}
			fileInputStream.close();
			reader.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("resource")
	public static final String read(File file) {
		if (file.exists()) {
			try {
				InputStream inputStream = new FileInputStream(file);
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(512);
				byte[] bs = new byte[512];
				int length = -1;
				while ((length = inputStream.read(bs)) != -1) {
					arrayOutputStream.write(bs, 0, length);
				}
				return new String(arrayOutputStream.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static final void save(File file, String text) throws Exception {
		file.createNewFile();
		OutputStream outputStream = new FileOutputStream(file);
		outputStream.write(text.getBytes());
		outputStream.close();
	}

	public static final File selecFile(Component component) {
		javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView
				.getFileSystemView();
		return selecFile(component, fsv.getHomeDirectory().getAbsolutePath());
	}

	public static final File selecFile(Component component, String path) {
		return selecFile(component, path, null);
	}

	public static final File selecFile(Component component, String path, FileFilter fileFilter) {
		JFileChooser chooser = new JFileChooser(path);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(fileFilter);
		chooser.showOpenDialog(component);
		return chooser.getSelectedFile();
	}

	public static final File selecDirectories(Component component) {
		javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView
				.getFileSystemView();
		return selecDirectories(component, fsv.getHomeDirectory().getAbsolutePath());
	}

	public static final File selecDirectories(Component component, String path) {
		return selecDirectories(component, path, null);
	}

	public static final File selecDirectories(Component component, String path, FileFilter fileFilter) {
		JFileChooser chooser = new JFileChooser(path);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(component);
		if (chooser != null) {
			chooser.setFileFilter(fileFilter);
		}
		return chooser.getSelectedFile();
	}

	public static final File getRoot() {
		return new File(System.getProperty("user.dir"));
	}

	public static final void delete(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						delete(files[i]);
					}
				}
			}
			file.delete();
		}
	}
}
