package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.data.tools.DataManage;
import gdx.keyroy.data.tools.models.ClassElement;
import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.data.tools.models.ResoucePath;
import gdx.keyroy.psd.tools.util.DefaultTreeNode;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.PopmenuListener;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.util.SwingUtil.DropInAdapter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

// 类管理的面板
@SuppressWarnings("serial")
public class PanelElementTree extends JPanel {
	private JTree tree;

	/**
	 * Create the panel.
	 */
	public PanelElementTree() {
		setLayout(new BorderLayout(0, 0));
		ElementTreeModel treeModel = new ElementTreeModel();
		this.tree = new JTree(new DefaultTreeModel(treeModel));
		this.tree.setCellRenderer(treeModel);
		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (tree.getSelectionPath() != null) {
					ElementTreeModel treeNode = (ElementTreeModel) tree.getSelectionPath()
							.getLastPathComponent();
					if (treeNode.classPath != null) {
						Messager.send(treeNode.classPath, MessageKey.SELECTED);
					} else if (treeNode.resourcePath != null) {
						Messager.send(treeNode.resourcePath, MessageKey.SELECTED);
					}
				}
			}
		});

		// 展开树结构
		TreePath treePath = new TreePath(treeModel);
		this.tree.expandPath(treePath);
		for (int i = 0; i < treeModel.getChildCount(); i++) {
			TreePath cTreePath = treePath.pathByAddingChild(treeModel.getChildAt(i));
			this.tree.expandPath(cTreePath);
		}
		//
		add(tree, BorderLayout.CENTER);
		initDropIn();
		initPopmenu();
		initMessageListener();
	}

	private final void initMessageListener() {
		Messager.register(ClassPath.class, new MessageListener<ClassPath>() {
			@Override
			public void onMessage(ClassPath t, Object[] params) {
				if (isKey(MessageKey.UPDATE, params)) {
					update((ElementTreeModel) tree.getModel().getRoot(), t);
				}
			}

			protected final void update(ElementTreeModel treeModel, ClassPath t) {
				if (t.equals(treeModel.classPath)) {
					treeModel.updateName(t);
				} else if (treeModel.getAllowsChildren()) {
					for (int i = 0; i < treeModel.getChildCount(); i++) {
						ElementTreeModel cTreeModel = (ElementTreeModel) treeModel.getChildAt(i);
						update(cTreeModel, t);
					}
				}
			}
		});
	}

	private final void initPopmenu() {
		//
		SwingUtil.addPopup(tree, new PopmenuListener() {
			@Override
			public void onInitPopmenu(JPopupMenu popupMenu) {
				if (tree.getSelectionPath() != null) {
					final ElementTreeModel treeNode = (ElementTreeModel) tree.getSelectionPath()
							.getLastPathComponent();
					if (treeNode.classPath != null) { // 类对象
						// 新建对象
						SwingUtil.addPopup(popupMenu, "menu.new_element", new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								ClassPath classPath = treeNode.classPath;
								ClassElement classElement = addClassElement(classPath);
								if (classElement != null) {
									DataManage.save(classElement);
									treeNode.updateName(classPath);
									revalidate();
									Messager.send(classPath, MessageKey.UPDATE);
								}
							}
						});
						SwingUtil.addPopup(popupMenu, "menu.new_element_list", new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								ClassPath classPath = treeNode.classPath;
								String count = (String) JOptionPane.showInputDialog(tree,
										L.get("text.input_element_count") + " ：\n",
										L.get("dialog.new_element"), JOptionPane.PLAIN_MESSAGE,
										Icons.CLASS_FILE, null, "" + (1));
								try {
									int number = Integer.parseInt(count);
									List<ClassElement> elements = addClassElement(classPath, number);
									DataManage.save(elements);
									treeNode.updateName(classPath);
									revalidate();
									Messager.send(classPath, MessageKey.UPDATE);
								} catch (Exception e2) {
									JOptionPane.showMessageDialog(tree, "text.only_support_int_value",
											"dialog.error_on_int", JOptionPane.ERROR_MESSAGE);
								}

							}
						});
					} else if (treeNode.resourcePath != null) { // 图片对象
						// 删除类
						SwingUtil.addPopup(popupMenu, "menu.del_element", new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								TreePath[] treePaths = tree.getSelectionPaths();
								for (TreePath treePath : treePaths) {
									ElementTreeModel treeModel = (ElementTreeModel) treePath
											.getLastPathComponent();
									DataManage.getImagePaths().remove(treeModel.resourcePath);
								}
								DataManage.save();
								updateTree();
							}
						});
					} else if (treeNode.folder != null) { // 图片文件夹
						SwingUtil.addPopup(popupMenu, "menu.open_folder", new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									java.awt.Desktop.getDesktop().open(treeNode.folder);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						});
					}
				}
			}

			private final ClassElement addClassElement(ClassPath classPath) {
				int objectId = 0;
				List<ClassElement> elements = classPath.getElements();
				for (ClassElement classElement : elements) {
					try {
						int id = Integer.parseInt(classElement.getObjId());
						objectId = Math.max(id, objectId);
					} catch (Exception e2) {
					}
				}
				String id = (String) JOptionPane.showInputDialog(tree, L.get("text.input_element_id")
						+ " ：\n", L.get("dialog.new_element"), JOptionPane.PLAIN_MESSAGE, Icons.CLASS_FILE,
						null, "" + (objectId + 1));
				if (id != null) {
					try {
						ClassElement classElement = new ClassElement();
						classElement.setObjId(id);
						classElement.setObject(classPath.getClazz().newInstance());
						classPath.addElement(classElement);
						return classElement;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				return null;
			}

			private final List<ClassElement> addClassElement(ClassPath classPath, int count) {
				int objectId = 0;
				List<ClassElement> elements = classPath.getElements();
				for (ClassElement classElement : elements) {
					try {
						int id = Integer.parseInt(classElement.getObjId());
						objectId = Math.max(id, objectId);
					} catch (Exception e2) {
					}
				}
				for (int i = 0; i < count; i++) {
					try {
						String id = "" + (objectId += 1);
						ClassElement classElement = new ClassElement();
						classElement.setObjId(id);
						classElement.setObject(classPath.getClazz().newInstance());
						classPath.addElement(classElement);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				return elements;
			}
		});
	}

	private final void updateTree() {
		ElementTreeModel treeModel = new ElementTreeModel();
		tree.setModel(new DefaultTreeModel(treeModel));
		tree.setCellRenderer(treeModel);
		//
		TreePath treePath = new TreePath(treeModel);
		this.tree.expandPath(treePath);
		for (int i = 0; i < treeModel.getChildCount(); i++) {
			TreePath cTreePath = treePath.pathByAddingChild(treeModel.getChildAt(i));
			this.tree.expandPath(cTreePath);
		}
		revalidate();
	}

	private final void initDropIn() {
		SwingUtil.addDropIn(this, new DropInAdapter() {
			@Override
			public void onDropIn(File file) {
				try {
					DataManage.addResource(file, false);
				} catch (Exception e) {
				}
			}

			@Override
			public void onDropIn(List<File> files) {
				super.onDropIn(files);
				updateTree();
				DataManage.save();
			}
		});
	}

	private static final Hashtable<String, ElementTreeModel> folders = new Hashtable<String, PanelElementTree.ElementTreeModel>();

	class ElementTreeModel extends DefaultTreeNode {
		// Class 元素
		private ClassPath classPath;
		// 图片元素
		private ResoucePath resourcePath;
		// 资源文件夹
		private File folder;

		// 默认构造器 , 默认加载所有 类元素,和图片
		public ElementTreeModel() {
			setUserObject(L.get("label.element_collections"));
			setAllowsChildren(true);
			setIcon(Icons.LAYER_FOLDER);
			folders.clear();
			add(new ElementTreeModel(DataManage.getClassPaths(), ClassPath.class));
			add(new ElementTreeModel(DataManage.getImagePaths(), ResoucePath.class));
		}

		// 图片文件夹构造器
		public ElementTreeModel(File imageFolder) {
			this.folder = imageFolder;
			setUserObject(imageFolder.getName());
			setAllowsChildren(true);
			setIcon(Icons.LAYER_FOLDER);
		}

		// 元素数组
		public ElementTreeModel(List<?> list, Class<?> clazz) {
			setAllowsChildren(true);
			setIcon(Icons.LAYER_FOLDER);
			if (clazz.equals(ClassPath.class)) {
				setUserObject(L.get("label.element_class"));
			} else if (clazz.equals(ResoucePath.class)) {
				setUserObject(L.get("label.element_resource"));
			}

			for (Object object : list) {
				if (object instanceof ClassPath) {
					add(new ElementTreeModel((ClassPath) object));
				} else if (object instanceof ResoucePath) {
					ResoucePath imagePath = (ResoucePath) object;
					if (imagePath.getFolder() != null) {
						ElementTreeModel folderTreeModel = getFolderTreeModel(imagePath.getFolder());
						folderTreeModel.add(new ElementTreeModel(imagePath));
					} else {
						add(new ElementTreeModel((ResoucePath) object));
					}
				} else {
					throw new IllegalArgumentException("unsupport class : " + clazz.getName());
				}
			}
		}

		ElementTreeModel(ClassPath classPath) {
			this.classPath = classPath;
			setIcon(Icons.CLASS_FILE);
			setUserObject(classPath.getClassName() + "[" + classPath.getElements().size() + "]");
		}

		ElementTreeModel(ResoucePath imagePath) {
			this.resourcePath = imagePath;
			if (imagePath.exist()) {
				setUserObject(imagePath.getAssetsPath());
			} else {
				setUserObject(imagePath.getAssetsPath() + "(not exist)");
			}
			if (imagePath.isAtlas()) {
				setIcon(Icons.IMAGE_ATLAS_FILE);
			} else {
				setIcon(Icons.RESOURCE_FILE);
			}

		}

		protected final ElementTreeModel getFolderTreeModel(String path) {
			ElementTreeModel treeModel = folders.get(path);
			if (treeModel == null) {
				treeModel = new ElementTreeModel(new File(path));
				folders.put(path, treeModel);
				add(treeModel);
			}
			return treeModel;
		}

		protected final void updateName(ClassPath classPath) {
			setUserObject(classPath.getClassName() + "[" + classPath.getElements().size() + "]");
		}
	}

}
