package gdx.keyroy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import gdx.keyroy.data.tools.DataManage;
import gdx.keyroy.data.tools.widgets.Lable;
import gdx.keyroy.data.tools.widgets.PanelElementTable;
import gdx.keyroy.data.tools.widgets.PanelFieldsTree;
import gdx.keyroy.psd.tools.models.EditorConfig;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.widgets.DialogProgress;
import gdx.keyroy.widgets.ClassTree;
import gdx.keyroy.widgets.ResourceTree;

public class GameKit {

	protected static float scale = 0.5f;
	private JFrame frame;

	public static final void start(Class<?>... classes) {
		try {
			for (Class<?> clazz : classes) {
				DataManage.addClass(clazz, false);
			}
			init();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						GameKit window = new GameKit();
						SwingUtil.center(window.frame);
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, L.get("error.init_data_failed"), L.get("Error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private static final void init() throws Exception {
		// LOOK AND FEEL
		SwingUtil.initWindowsLookAndFeel();
		// 加载语言
		L.load("/zn");
		// 加载编辑器 数据
		// DataManage.addClass(Monster.class, false);
		DataManage.load();
		// 加载配置信息
		EditorConfig.load();
		scale = 1;
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
						GameKit window = new GameKit();
						SwingUtil.center(window.frame);
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, L.get("error.init_data_failed"), L.get("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Create the application.
	 */
	public GameKit() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle(L.get("window.data_manage"));
		frame.setBounds(0, 0, scale(1280), scale(720));
		frame.setMinimumSize(new Dimension(scale(1280), scale(720)));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel_class_tree = new JPanel();
		panel_class_tree.setPreferredSize(new Dimension(scale(320), scale(320)));
		panel_class_tree.setLayout(new BorderLayout(0, 0));
		// frame.getContentPane().add(panel_class_tree, BorderLayout.WEST);
		panel_class_tree.add(new Lable("label.element_tree"), BorderLayout.NORTH);
		panel_class_tree.add(new JScrollPane(new ClassTree()), BorderLayout.CENTER);
		//
		JPanel panel_resource_tree = new JPanel();
		panel_resource_tree.setPreferredSize(new Dimension(scale(320), scale(320)));
		panel_resource_tree.setLayout(new BorderLayout(0, 0));
		// frame.getContentPane().add(panel_class_tree, BorderLayout.WEST);
		panel_resource_tree.add(new Lable("label.element_collections"), BorderLayout.NORTH);
		panel_resource_tree.add(new JScrollPane(new ResourceTree()), BorderLayout.CENTER);

		JPanel panel_elements = new JPanel();
		frame.getContentPane().add(panel_elements, BorderLayout.CENTER);
		panel_elements.setLayout(new BorderLayout(0, 0));

		panel_elements.add(new PanelElementTable(), BorderLayout.CENTER);

		JPanel panel_field_tree = new JPanel();
		panel_field_tree.setPreferredSize(new Dimension(scale(320), scale(320)));
		frame.getContentPane().add(panel_field_tree, BorderLayout.EAST);
		panel_field_tree.setLayout(new BorderLayout(0, 0));

		JLabel label_field_tree = new Lable("label.field_tree");
		panel_field_tree.add(label_field_tree, BorderLayout.NORTH);

		panel_field_tree.add(new PanelFieldsTree(), BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane, BorderLayout.WEST);

		splitPane.setLeftComponent(panel_class_tree);
		splitPane.setRightComponent(panel_resource_tree);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu menu = new JMenu(L.get("menu.pack"));
		menuBar.add(menu);

		JMenuItem mntmNewMenuItem = new JMenuItem(L.get("menu.export"));
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 打包
				final DialogProgress dialogProgress = new DialogProgress();
				SwingUtil.center(frame, dialogProgress);
				dialogProgress.setVisible(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						DataManage.export();
						dialogProgress.dispose();
					}
				}).start();
			}
		});
		menu.add(mntmNewMenuItem);
	}

	private static final int scale(int input) {
		return (int) (input * scale);
	}

}
