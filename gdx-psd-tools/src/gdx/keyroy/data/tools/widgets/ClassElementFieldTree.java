package gdx.keyroy.data.tools.widgets;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.badlogic.gdx.utils.Array;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import gdx.keyroy.data.tools.DataManage;
import gdx.keyroy.data.tools.models.ClassElement;
import gdx.keyroy.data.tools.models.FieldAn;
import gdx.keyroy.data.tools.models.ResoucePath;
import gdx.keyroy.psd.tools.util.DefaultTreeNode;
import gdx.keyroy.psd.tools.util.FieldParser;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.PopmenuListener;
import gdx.keyroy.psd.tools.util.PsdCache;
import gdx.keyroy.psd.tools.util.ReflectTools;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.util.TextureUnpacker.Region;
import library.psd.Layer;
import library.psd.Psd;
import library.psd.util.LayerFilter;

@SuppressWarnings("serial")
public class ClassElementFieldTree extends JPanel {
	private EmptyBorder border = new EmptyBorder(4, 4, 4, 4);
	private JTree tree;
	private JTextField textField_class;
	private JTextField textField_field;
	private ClassElement classElement;

	public ClassElementFieldTree(ClassElement classElement) {
		setLayout(new BorderLayout(0, 0));
		this.classElement = classElement;
		FieldTreeModel treeModel = new FieldTreeModel(classElement.getObject());

		this.tree = new JTree(new DefaultTreeModel(treeModel));
		this.tree.setCellRenderer(new FieldTreeModel());
		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (tree.getSelectionPath() != null) {
					FieldTreeModel treeNode = (FieldTreeModel) tree.getSelectionPath().getLastPathComponent();
					updateEditPanel(treeNode);
				}
			}
		});

		// 展开树结构
		TreePath treePath = new TreePath(treeModel);
		this.tree.expandPath(treePath);
		treePath = treePath.pathByAddingChild(treeModel.getChildAt(1));
		this.tree.expandPath(treePath);
		//
		add(tree, BorderLayout.CENTER);
		//
		initEditPanel();
	}

	// 更新编辑面板元素
	protected final void updateEditPanel(FieldTreeModel treeNode) {
		textField_field.setText(null);
		textField_field.setEditable(false);
		if (treeNode.field != null) {
			try {
				Object val = treeNode.field.get(treeNode.source);
				if (val != null) {
					textField_class.setText(val.getClass().getName());
					if (ReflectTools.isBaseType(val.getClass())) {
						textField_field.setText(String.valueOf(val));
					}
				} else {
					textField_class.setText(treeNode.field.getType().getName());
				}

				if (isEditable(treeNode, treeNode.field.getType())) {
					textField_field.setEditable(true);
					setTextListener(new FieldFocusListener(treeNode));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (treeNode.source != null) {
			textField_class.setText(treeNode.source.getClass().getName());
		}
		revalidate();
	}

	// 初始化编辑面板
	protected final void initEditPanel() {
		//
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.GROWING_BUTTON_COLSPEC, },
				new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		JLabel label_class = new JLabel(L.get("label.class_name"));
		panel.add(label_class, "1, 1, fill, fill");
		label_class.setBorder(border);

		textField_class = new JTextField();
		textField_class.setEditable(false);
		textField_class.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(textField_class, "2, 1, fill, fill");

		JLabel label_field = new JLabel(L.get("label.field_value"));
		panel.add(label_field, "1, 2, fill, fill");
		label_field.setBorder(border);

		textField_field = new JTextField();
		textField_field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					tree.requestFocus();
				}
			}
		});
		textField_field.setEditable(false);
		panel.add(textField_field, "2, 2, fill, fill");
		SwingUtil.addPopup(textField_field, new PopmenuListener() {
			Hashtable<String, JMenu> folders = new Hashtable<String, JMenu>();

			@Override
			public void onInitPopmenu(JPopupMenu popupMenu) {
				if (textField_field.isEditable()) {
					// 文件类型缓存
					folders.clear();
					List<JMenu> menus = new ArrayList<JMenu>();
					for (ResoucePath resoucePath : DataManage.getResourcePaths()) {
						String fileType = resoucePath.getFileType();
						if (fileType == null) {
							fileType = "";
						}
						if (folders.containsKey(fileType) == false) {
							JMenu menu = new JMenu(fileType);
							menus.add(menu);
							folders.put(fileType, menu);
						}
					}
					// 排序 , 添加
					menus.sort(new Comparator<JMenu>() {
						@Override
						public int compare(JMenu o1, JMenu o2) {
							return o1.getText().compareTo(o2.getText());
						}
					});
					for (JMenu menu : menus) {
						popupMenu.add(menu);
					}
					//

					for (final ResoucePath resoucePath : DataManage.getResourcePaths()) {
						if (resoucePath.getFile().exists()) {
							AbstractButton button = null;
							if (resoucePath.isAtlas()) {
								JMenu menu = new JMenu(resoucePath.getFileName());
								Array<Region> regins = resoucePath.getUnpacker().getRegions();
								for (final Region region : regins) {
									JMenuItem menuItem = new JMenuItem(region.name);
									menuItem.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											textField_field.setText(region.name + "@"
													+ resoucePath.getAssetsPath().replace("\\", "/"));
										}
									});
									menu.add(menuItem);
								}
								button = menu;
							} else if (resoucePath.isPSD()) {
								final JMenu menu = new JMenu(resoucePath.getFileName());
								final Psd psd = PsdCache.get(resoucePath.getFile());
								psd.getLayer(new LayerFilter() {
									@Override
									public boolean accept(final Layer layer) {
										JMenuItem menuItem = new JMenuItem(layer.getName());
										menuItem.addActionListener(new ActionListener() {
											@Override
											public void actionPerformed(ActionEvent e) {
												textField_field.setText(layer.getName() + "@"
														+ resoucePath.getAssetsPath().replace("\\", "/"));
											}
										});
										menu.add(menuItem);
										return false;
									}
								});
								button = menu;
							} else {
								JMenuItem menuItem = new JMenuItem(resoucePath.getFileName());
								menuItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										textField_field
												.setText(resoucePath.getAssetsPath().replace("\\", "/"));
									}
								});
								button = menuItem;
							}

							String fileType = resoucePath.getFileType();
							if (fileType == null) {
								fileType = "";
							}
							String rPath = resoucePath.getAssetsPath().replace(File.separator, "/");
							String[] paths = rPath.split("/");
							if (paths.length > 1) {
								String path = fileType;
								for (int i = 0; i < paths.length - 1; i++) {
									String newPath = path + "/" + paths[i];
									if (folders.containsKey(newPath) == false) {
										JMenu menu = new JMenu(paths[i]);
										folders.put(newPath, menu);
										folders.get(path).add(menu);
									}
									path = newPath;
								}
								JMenu menu = folders.get(path);
								menu.add(button);
							} else {
								JMenu menu = folders.get(fileType);
								menu.add(button);
							}

						}
					}
				}
			}
		});
	}

	protected final boolean isEditable(FieldTreeModel treeNode, Class<?> type) {
		if (ReflectTools.isBaseType(type)) {
			FieldAn an = treeNode.field.getAnnotation(FieldAn.class);
			if (an == null || an.lock() == false) {
				return true;
			}
		}
		return false;
	}

	// 设置文字监听
	protected final void setTextListener(FieldFocusListener fieldFocusListener) {
		FocusListener[] focusListeners = textField_field.getFocusListeners();
		if (focusListeners != null) {
			for (FocusListener focusListener : focusListeners) {
				textField_field.removeFocusListener(focusListener);
			}
		}
		textField_field.addFocusListener(fieldFocusListener);
	}

	protected final void updateTree() {
		tree.setCellRenderer(new FieldTreeModel());
		repaint();
	}

	class FieldFocusListener implements FocusListener {
		final FieldTreeModel treeNode;

		public FieldFocusListener(FieldTreeModel treeNode) {
			this.treeNode = treeNode;
		}

		@Override
		public void focusLost(FocusEvent e) {
			JTextField textField = (JTextField) e.getComponent();
			String text = textField.getText();
			FieldParser parser = FieldParser.get(treeNode.field);
			if (parser != null) {
				try {
					treeNode.field.set(treeNode.source, parser.parser(text));
					treeNode.updateLabel();
					DataManage.save(classElement);
					updateTree();
				} catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(tree, L.get("error.parser_data_failed"), L.get("Error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
		}
	}

	class FieldTreeModel extends DefaultTreeNode {
		private Object source;
		private Field field;

		private FieldTreeModel() {
		}

		public FieldTreeModel(Object source) {
			this.source = source;
			setAllowsChildren(true);
			setUserObject(source.getClass().getSimpleName() + "[" + classElement.getObjId() + "]");
			setIcon(Icons.CLASS_FILE);
			this.source = source;
			//
			List<Field> fields = ReflectTools.getFields(source.getClass());
			for (Field field : fields) {
				if (ReflectTools.isStatic(field)) { // 静态数据不能编辑

				} else {
					add(new FieldTreeModel(source, field));
				}
			}
		}

		public FieldTreeModel(Object source, Field field) {
			try {
				this.source = source;
				this.field = field;
				field.setAccessible(true);
				// 图标

				Class<?> type = field.getType();
				FieldAn an = field.getAnnotation(FieldAn.class);
				if (an != null && an.lock()) {
					setIcon(Icons.FIELD_TYPE_LOCK);
				} else if (ReflectTools.isBaseType(type)) {
					if (type.equals(String.class)) {
						setIcon(Icons.FIELD_TYPE_STRING);
					} else if (type.equals(Integer.class) || type.equals(int.class)) {
						setIcon(Icons.FIELD_TYPE_INT);
					} else {
						setIcon(Icons.FIELD_TYPE_DEF);
					}
				} else if (ReflectTools.isArray(type) || List.class.isAssignableFrom(type)) {
					setIcon(Icons.FIELD_TYPE_ARRAY);
				} else {
					setIcon(Icons.FIELD_TYPE_OBJ);
				}
				updateLabel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("rawtypes")
		protected final void updateLabel() {
			try {
				Class<?> type = field.getType();
				String label = field.getName();
				// 数值
				Object val = field.get(source);
				if (val == null) { // 空值
				} else {
					type = val.getClass();
					if (ReflectTools.isBaseType(type)) {
						label += " : " + val;
					} else if (ReflectTools.isArray(type)) {
						setAllowsChildren(true);
						Object[] objects = (Object[]) source;
						for (Object object : objects) {
							add(new FieldTreeModel(object));
						}
					} else if (List.class.isAssignableFrom(type)) {
						setAllowsChildren(true);
						List list = (List) source;
						for (Object object : list) {
							add(new FieldTreeModel(object));
						}
					} else if (Map.class.isInstance(source)) {
					} else {
						setAllowsChildren(true);
						List<Field> fields = ReflectTools.getFields(type);
						for (Field cField : fields) {
							if (ReflectTools.isStatic(cField)) { // 静态数据不能编辑

							} else {
								add(new FieldTreeModel(val, cField));
							}
						}
					}
				}
				setUserObject(label);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
