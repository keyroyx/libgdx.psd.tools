package gdx.keyroy.psd.tools;

import gdx.keyroy.psd.tools.models.EditorConfig;
import gdx.keyroy.psd.tools.models.EditorData;
import gdx.keyroy.psd.tools.models.PSDData;
import gdx.keyroy.psd.tools.util.FileUtil;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Message;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.PSDUtil;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.widgets.DialogProgress;
import gdx.keyroy.psd.tools.widgets.FrameEditorConfig;
import gdx.keyroy.psd.tools.widgets.FrameHelp;
import gdx.keyroy.psd.tools.widgets.PanelPSDFileList;
import gdx.keyroy.psd.tools.widgets.PanelPSDLayerTree;
import gdx.keyroy.psd.tools.widgets.PanelPSDParamTable;
import gdx.keyroy.psd.tools.widgets.PanelParamFileList;
import gdx.keyroy.psd.tools.widgets.PanelPsdViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;

import library.psd.Psd;

public class PsdTools {
	private File lastFile;
	private JFrame frame;

	private static final void init() throws Exception {
		// LOOK AND FEEL
		SwingUtil.initWindowsLookAndFeel();
		// 加载语言
		L.load("/zn");
		// L.load(new File("zn"));
		// L.print();
		// 加载编辑器 数据
		EditorData.load();
		// 加载配置信息
		EditorConfig.load();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			init();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						PsdTools window = new PsdTools();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, L.get("error.init_data_failed"), L.get("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Create the application.
	 */
	public PsdTools() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle(l("window.title"));
		frame.setBounds(0, 0, 1280, 720);
		frame.setMinimumSize(new Dimension(1280, 720));
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtil.center(frame);
		// 菜单
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu menu_system = new JMenu(l("menu.system"));
		menuBar.add(menu_system);

		JMenuItem menu_open_psd = new JMenuItem(l("menu.open_psd"));
		menu_open_psd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// 加载单独的PSD 文件
				String path = lastFile != null ? lastFile.getPath() : null;
				File file = FileUtil.selecFile(frame, path, new FileFilter() {
					@Override
					public String getDescription() {
						return "psd file";
					}

					@Override
					public boolean accept(File file) {
						return file.isDirectory() || file.getName().toLowerCase().endsWith(".psd");
					}
				});
				EditorData.verify(file);
				lastFile = file;
			}
		});
		menu_system.add(menu_open_psd);

		JMenuItem menu_open_param = new JMenuItem(l("menu.open_param"));
		menu_open_param.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// 加载单独的 INI 文件
				String path = lastFile != null ? lastFile.getPath() : null;
				File file = FileUtil.selecFile(frame, path, new FileFilter() {
					@Override
					public String getDescription() {
						return "ini file";
					}

					@Override
					public boolean accept(File file) {
						return file.isDirectory() || file.getName().toLowerCase().endsWith(".ini");
					}
				});
				EditorData.verify(file);
				lastFile = file;
			}
		});
		menu_system.add(menu_open_param);

		JMenuItem menu_open_folder = new JMenuItem(l("menu.open_folder"));
		menu_open_folder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 加载 文件夹
				String path = lastFile != null ? lastFile.getPath() : null;
				File file = FileUtil.selecDirectories(frame, path, new FileFilter() {
					@Override
					public String getDescription() {
						return "folder";
					}

					@Override
					public boolean accept(File file) {
						return file.isDirectory();
					}
				});
				EditorData.verify(file);
				lastFile = file;
			}
		});
		menu_system.add(menu_open_folder);
		//
		JMenuItem menu_exit = new JMenuItem(l("menu.exit"));
		menu_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu_system.add(menu_exit);
		//
		JMenu menu_export = new JMenu(l("menu.export"));
		menuBar.add(menu_export);
		//
		JMenuItem menu_pack = new JMenuItem(l("menu.pack"));
		menu_pack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final DialogProgress dialogProgress = new DialogProgress();
				SwingUtil.center(frame, dialogProgress);
				dialogProgress.setVisible(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GdxPsdTools.export();
						dialogProgress.dispose();
					}
				}).start();
			}
		});
		menu_export.add(menu_pack);
		//
		JSeparator separator = new JSeparator();
		menu_export.add(separator);
		//
		JMenuItem menu_pack_config = new JMenuItem(l("menu.pack_config"));
		menu_pack_config.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FrameEditorConfig editorConfig = new FrameEditorConfig();
				SwingUtil.center(frame, editorConfig);
				editorConfig.setVisible(true);
			}
		});
		menu_export.add(menu_pack_config);
		//
		JMenu mnMenuabout = new JMenu(l("menu.about"));
		menuBar.add(mnMenuabout);
		//
		JMenuItem menu_help = new JMenuItem(l("menu.help"));
		menu_help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FrameHelp frameHelp = new FrameHelp();
				SwingUtil.center(frame, frameHelp);
				frameHelp.setVisible(true);
			}
		});
		mnMenuabout.add(menu_help);
		//
		JMenuItem menu_source_code = new JMenuItem(l("menu.source_code"));
		mnMenuabout.add(menu_source_code);

		// 基本面板

		JPanel panel_left = new JPanel();
		panel_left.setPreferredSize(new Dimension(320, 320));
		panel_left.setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(panel_left, BorderLayout.WEST);
		//
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(new PanelPSDFileList());
		splitPane.setBottomComponent(new PanelParamFileList());
		panel_left.add(splitPane);
		//

		JPanel panel_right = new JPanel();
		panel_right.setPreferredSize(new Dimension(320, 320));
		frame.getContentPane().add(panel_right, BorderLayout.EAST);
		panel_right.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane_right = new JSplitPane();
		splitPane_right.setResizeWeight(0.5);
		splitPane_right.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_right.setTopComponent(new PanelPSDLayerTree());
		splitPane_right.setBottomComponent(new PanelPSDParamTable());
		panel_right.add(splitPane_right, BorderLayout.CENTER);

		//
		JPanel panel_bottom = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_bottom.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		frame.getContentPane().add(panel_bottom, BorderLayout.SOUTH);
		{//
			final JLabel message_label = new JLabel(" ");
			panel_bottom.add(message_label);
			Message.register(String.class, new MessageListener<String>() {
				@Override
				public void onMessage(String t, Object[] params) {
					message_label.setText(t);
					frame.repaint();
				}
			});
			Message.register(PSDData.class, new MessageListener<PSDData>() {
				@Override
				public void onMessage(PSDData t, Object[] params) {
					Psd psd = t.getCache();
					Rectangle rect = PSDUtil.getMaxSize(psd);
					message_label.setText(t.getFilePath() + "    " + "[width=" + rect.width + ",height="
							+ rect.height + "]");
					frame.repaint();
				}
			});
		}

		//
		JPanel panel_center = new JPanel();
		frame.getContentPane().add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new BorderLayout(0, 0));
		panel_center.add(new PanelPsdViewer(), BorderLayout.CENTER);
	}

	private static final String l(String key) {
		return L.get(key);
	}

}
