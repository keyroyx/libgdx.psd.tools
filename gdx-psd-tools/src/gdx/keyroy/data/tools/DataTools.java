package gdx.keyroy.data.tools;

import gdx.keyroy.data.tools.util.DataManage;
import gdx.keyroy.data.tools.widgets.Lable;
import gdx.keyroy.data.tools.widgets.PanelClassPathList;
import gdx.keyroy.data.tools.widgets.PanelElementList;
import gdx.keyroy.data.tools.widgets.PanelFieldsTree;
import gdx.keyroy.data.tools.widgets.PanelImagePathList;
import gdx.keyroy.psd.tools.models.EditorConfig;
import gdx.keyroy.psd.tools.models.PSDData;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.SwingUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class DataTools {

	private JFrame frmLabledatamanage;

	private static final void init() throws Exception {
		// LOOK AND FEEL
		SwingUtil.initWindowsLookAndFeel();
		// 加载语言
		L.load("/zn");
		// 加载编辑器 数据
		DataManage.load();
		DataManage.addClass(PSDData.class, false);
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
						DataTools window = new DataTools();
						window.frmLabledatamanage.setVisible(true);
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
	public DataTools() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLabledatamanage = new JFrame();
		frmLabledatamanage.setTitle(L.get("window.data_manage"));
		frmLabledatamanage.setBounds(0, 0, 1280, 720);
		frmLabledatamanage.setMinimumSize(new Dimension(1280, 720));
		frmLabledatamanage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmLabledatamanage.setJMenuBar(menuBar);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setPreferredSize(new Dimension(320, 320));
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmLabledatamanage.getContentPane().add(splitPane, BorderLayout.WEST);

		JPanel panel_class_tree = new JPanel();
		splitPane.setLeftComponent(panel_class_tree);
		panel_class_tree.setLayout(new BorderLayout(0, 0));

		JLabel lable_class_tree = new Lable("lable.class_tree");
		panel_class_tree.add(lable_class_tree, BorderLayout.NORTH);

		panel_class_tree.add(new PanelClassPathList(), BorderLayout.CENTER);

		JPanel panel_image_list = new JPanel();
		splitPane.setRightComponent(panel_image_list);
		panel_image_list.setLayout(new BorderLayout(0, 0));

		JLabel label_image_list = new Lable("label.image_list");
		panel_image_list.add(label_image_list, BorderLayout.NORTH);

		panel_image_list.add(new PanelImagePathList(), BorderLayout.CENTER);

		JPanel panel_elements = new JPanel();
		frmLabledatamanage.getContentPane().add(panel_elements, BorderLayout.CENTER);
		panel_elements.setLayout(new BorderLayout(0, 0));

		JLabel lable_class_name = new Lable("label.class_name");
		panel_elements.add(lable_class_name, BorderLayout.NORTH);

		panel_elements.add(new PanelElementList(), BorderLayout.CENTER);

		JPanel panel_field_tree = new JPanel();
		panel_field_tree.setPreferredSize(new Dimension(320, 320));
		frmLabledatamanage.getContentPane().add(panel_field_tree, BorderLayout.EAST);
		panel_field_tree.setLayout(new BorderLayout(0, 0));

		JLabel lblLablefieldtree = new Lable("lable.field_tree");
		panel_field_tree.add(lblLablefieldtree, BorderLayout.NORTH);

		panel_field_tree.add(new PanelFieldsTree(), BorderLayout.CENTER);
	}

}
