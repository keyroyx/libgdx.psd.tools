package gdx.keyroy.widgets;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import gdx.keyroy.data.tools.DataManage;
import gdx.keyroy.data.tools.models.ClassElement;
import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.psd.tools.util.DefaultTreeNode;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.PopmenuListener;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.util.SwingUtil.DropInAdapter;

// 类管理的面板
@SuppressWarnings("serial")
public class ClassTree extends JPanel {
	private JTree tree;

	/**
	 * Create the panel.
	 */
	public ClassTree() {
		setLayout(new BorderLayout(0, 0));
		ClassTreeModel treeModel = new ClassTreeModel();
		this.tree = new JTree(new DefaultTreeModel(treeModel));
		this.tree.setCellRenderer(treeModel);
		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (tree.getSelectionPath() != null) {
					ClassTreeModel treeNode = (ClassTreeModel) tree.getSelectionPath().getLastPathComponent();
					if (treeNode.classPath != null) {
						Messager.send(treeNode.classPath, MessageKey.SELECTED);
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
					update((ClassTreeModel) tree.getModel().getRoot(), t);
				}
			}

			protected final void update(ClassTreeModel treeModel, ClassPath t) {
				if (t.equals(treeModel.classPath)) {
					treeModel.updateName(t);
				} else if (treeModel.getAllowsChildren()) {
					for (int i = 0; i < treeModel.getChildCount(); i++) {
						ClassTreeModel cTreeModel = (ClassTreeModel) treeModel.getChildAt(i);
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
					final ClassTreeModel treeNode = (ClassTreeModel) tree.getSelectionPath()
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
				String id = (String) JOptionPane.showInputDialog(tree,
						L.get("text.input_element_id") + " ：\n", L.get("dialog.new_element"),
						JOptionPane.PLAIN_MESSAGE, Icons.CLASS_FILE, null, "" + (objectId + 1));
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
		ClassTreeModel treeModel = new ClassTreeModel();
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

	class ClassTreeModel extends DefaultTreeNode {
		// Class 元素
		private ClassPath classPath;

		// 默认构造器 , 默认加载所有 类元素,和图片
		public ClassTreeModel() {
			setUserObject(L.get("label.element_class"));
			setAllowsChildren(true);
			setIcon(Icons.LAYER_FOLDER);

			for (ClassPath classPath : DataManage.getClassPaths()) {
				add(new ClassTreeModel(classPath));
			}

		}

		ClassTreeModel(ClassPath classPath) {
			this.classPath = classPath;
			setIcon(Icons.CLASS_FILE);
			setUserObject(classPath.getClassName() + "[" + classPath.getElements().size() + "]");
		}

		protected final void updateName(ClassPath classPath) {
			setUserObject(classPath.getClassName() + "[" + classPath.getElements().size() + "]");
		}
	}

}
