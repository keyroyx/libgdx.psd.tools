package com.keyroy.gdx.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.keyroy.util.json.Json;

import gdx.keyroy.psd.tools.util.FileUtil;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.PSDUtil;
import gdx.keyroy.psd.tools.util.PSDUtil.LayerBoundary;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.util.SwingUtil.DropInAdapter;
import gdx.keyroy.psd.tools.widgets.DialogProgress;
import library.psd.Layer;
import library.psd.LayersContainer;
import library.psd.Psd;
import library.psd.parser.object.PsdText;
import psd.Element;
import psd.Folder;
import psd.Pic;
import psd.PsdFile;
import psd.Text;

public class GdxPsdTools {

	private JFrame frmGdxpsdtools;
	private JCheckBox cleanFolder;
	private JCheckBox saveImage;
	private JCheckBox saveAtlas;
	private JCheckBox formatLayerName;
	private JLabel filePanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingUtil.initWindowsLookAndFeel();
					GdxPsdTools window = new GdxPsdTools();
					window.frmGdxpsdtools.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GdxPsdTools() {
		Config.load();
		initialize();
		initCheckBox();
		initFileListener();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGdxpsdtools = new JFrame();
		frmGdxpsdtools.setTitle("GdxPsdTools");
		frmGdxpsdtools.setBounds(100, 100, 480, 320);
		frmGdxpsdtools.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		frmGdxpsdtools.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		filePanel = new JLabel("\u62D6\u62FD psd/xlsx \u6587\u4EF6\u5230\u8FD9\u4E2A\u9762\u677F");
		filePanel.setBackground(Color.WHITE);
		filePanel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(filePanel);

		JPanel configPanel = new JPanel();
		frmGdxpsdtools.getContentPane().add(configPanel, BorderLayout.NORTH);
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));

		cleanFolder = new JCheckBox("\u6E05\u7A7A\u65E7\u6587\u4EF6");
		configPanel.add(cleanFolder);

		configPanel.add(new JLabel(" "));
		saveImage = new JCheckBox("\u6253\u5305\u56FE\u7247");
		configPanel.add(saveImage);

		configPanel.add(new JLabel(" "));

		saveAtlas = new JCheckBox("\u6253\u5305\u6210 Atlas \u56FE\u7247\u96C6");
		configPanel.add(saveAtlas);

		configPanel.add(new JLabel(" "));

		formatLayerName = new JCheckBox("\u683C\u5F0F\u5316PSD\u56FE\u5C42\u540D\u79F0");
		configPanel.add(formatLayerName);
	}

	private final void initCheckBox() {
		cleanFolder.setSelected(Config.cleanFolder);
		saveImage.setSelected(Config.saveImage);
		saveAtlas.setSelected(Config.saveAtlas);
		formatLayerName.setSelected(Config.formatLayerName);
		//
		//
		cleanFolder.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Config.cleanFolder = cleanFolder.isSelected();
				Config.save();
			}
		});
		//
		saveImage.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Config.saveImage = saveImage.isSelected();
				Config.save();
			}
		});
		//
		saveAtlas.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Config.saveAtlas = saveAtlas.isSelected();
				Config.save();
			}
		});
		//
		formatLayerName.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Config.formatLayerName = formatLayerName.isSelected();
				Config.save();
			}
		});
	}

	private final void initFileListener() {
		SwingUtil.addDropIn(filePanel, new DropInAdapter() {
			@Override
			public void onDropIn(final List<File> files) {
				//
				// 打包
				final DialogProgress dialogProgress = new DialogProgress();
				SwingUtil.center(frmGdxpsdtools, dialogProgress);
				new Thread(new Runnable() {
					@Override
					public void run() {
						dialogProgress.setVisible(true);
						for (File file : files) {
							try {
								final File folder = new File(file.getParentFile(), "GdxPsdToolsExports");
								if (Config.cleanFolder) {// 清空文件夹
									FileUtil.delete(folder);
								}
								if (folder.exists() == false) { // 创建文件夹
									folder.mkdirs();
								}
								onDropIn(folder, file);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						dialogProgress.dispose();
					}
				}).start();
			}

			public void onDropIn(File folder, File file) throws Exception {
				if (file.getName().endsWith(".psd")) { // 打包 PSD
					export(folder, new Psd(file));
				} else if (file.getName().endsWith(".xlsx")) {
					exportXlsx(folder, file);
				}
			}
		});
	}

	public static final void exportXlsx(File folder, File file) throws Exception {
		List<JsonPack> arrays = XlsxParser.parser(file);
		for (JsonPack jsonPack : arrays) {
			File jsonFile = writeJson(folder, jsonPack);
			jsonFile = writeJson(folder, jsonPack);
			System.out.println("write josn : " + jsonFile.getName());
		}
	}

	private static final File writeJson(File jsonFolder, JsonPack jsonPack) throws Exception {
		File jsonFile = new File(jsonFolder, jsonPack.getName() + ".json");
		String json = jsonPack.getJsonArray().toString();
		//
		FileWriter fileWriter = new FileWriter(jsonFile);
		fileWriter.write(json);
		fileWriter.flush();
		fileWriter.close();

		return jsonFile;
	}

	public static final void export(File folder, Psd psd) throws Exception {
		PSDUtil.updateLayerParent(psd);
		//
		if (Config.saveImage) { // 保存图片
			// 收集图片图层
			List<Layer> layers = new ArrayList<Layer>();
			filterImage(psd, layers);
			if (Config.saveAtlas) { // 保存为图片集
				final Settings settings = new Settings();
				settings.pot = false;
				settings.rotation = true;
				settings.maxWidth = 2048;
				settings.maxHeight = 2048;
				TexturePacker packer = new TexturePacker(settings);
				for (Layer layer : layers) {
					packer.addImage(layer.getImage(), getImageName(layer));
				}
				String imagePath = psd.getName().replace(".psd", ".atlas");
				Messager.send("saving image : " + imagePath);
				packer.pack(folder, imagePath);
			} else {
				for (Layer layer : layers) {
					ImageIO.write(layer.getImage(), "png", new File(folder, getImageName(layer)));
				}
			}
		}
		// 保存 PSD 数据
		PsdFile psdFile = translate(psd);
		FileUtil.save(new File(folder, psd.getName().replace(".psd", ".json")), new Json(psdFile).toString());
	}

	private static final void filterImage(LayersContainer container, List<Layer> out) {
		for (int i = 0; i < container.getLayersCount(); i++) {
			Layer layer = container.getLayer(i);
			if (layer.isFolder() || layer.isTextLayer()) {
			} else if (layer.getImage() != null) {
				out.add(layer);
			}
			filterImage(layer, out);
		}
	}

	private static final String getImageName(Layer layer) {
		String name = layer.getName();
		// 这里需要去掉图片中的参数
		if (name.indexOf("@") != -1) {
			String[] sp = name.split("@", 2);
			name = sp[0];
		}
		if (name == null) {
			name = "_";
		}
		return name + ".png";
	}

	public static final PsdFile translate(Psd psd) {
		PSDUtil.updatePsdLayerPosition(psd);
		Rectangle rect = PSDUtil.getMaxSize(psd);
		PsdFile psdFile = new PsdFile();
		psdFile.width = psd.getWidth();
		psdFile.height = psd.getHeight();
		psdFile.maxWidth = rect.width;
		psdFile.maxHeight = rect.height;
		psdFile.psdName = psd.getName();
		// 参数
		addChild(psd, psd, psdFile);
		return psdFile;
	}

	public static final void addChild(Psd psd, LayersContainer container, Folder folder) {

		for (int i = 0; i < container.getLayersCount(); i++) {
			Layer layer = container.getLayer(i);
			Element actor = null;
			if (layer.isFolder()) { // 这是一个文件夹
				actor = new Folder();
			} else if (layer.isTextLayer()) { // 这是一个文本对象
				Text text = new Text();
				PsdText psdText = layer.getPsdText();
				text.text = psdText.value;
				text.a = psdText.a;
				text.r = psdText.r;
				text.g = psdText.g;
				text.b = psdText.b;
				text.fontSize = psdText.fontSize;
				actor = text;
			} else if (layer.getImage() != null) { // 这是一个图片
				actor = new Pic();
				((Pic) actor).textureName = getImageName(layer);
			}

			if (actor != null) { // 坐标
				actor.layerName = layer.getName();
				actor.isVisible = layer.isVisible();
				if (actor instanceof PsdFile) {
				} else {
					LayerBoundary boundary = LayerBoundary.getLayerBoundary(layer);
					if (boundary != null) {
						actor.x = boundary.getX();
						actor.y = boundary.getY();
						actor.width = boundary.getWidth();
						actor.height = boundary.getHeight();
					} else {
						actor.x = layer.getX();
						actor.y = layer.getY();
						actor.width = layer.getWidth();
						actor.height = layer.getHeight();
					}
					// libgdx 坐标系 , Y 相反
					actor.y = folder.height - actor.y - actor.height;
				}
				folder.childs.add(actor);

				//
				if (actor instanceof Folder) {
					addChild(psd, layer, (Folder) actor);
				}
			}
		}

	}

}
