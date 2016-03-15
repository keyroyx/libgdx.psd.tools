package gdx.keyroy.widgets;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import gdx.keyroy.data.tools.DataManage;
import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.data.tools.models.ResoucePath;
import gdx.keyroy.psd.tools.util.DefaultTreeNode;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.PopmenuListener;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.util.SwingUtil.DropInAdapter;

@SuppressWarnings({ "serial" })
public class ResourceTree extends JPanel {
	private JTree tree;

	/**
	 * Create the panel.
	 */
	public ResourceTree() {
		setLayout(new BorderLayout(0, 0));
		ResourceTreeModel treeModel = new ResourceTreeModel();
		this.tree = new JTree(new DefaultTreeModel(treeModel));
		this.tree.setCellRenderer(treeModel);
		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (tree.getSelectionPath() != null) {
					ResourceTreeModel treeNode = (ResourceTreeModel) tree.getSelectionPath()
							.getLastPathComponent();
					if (treeNode.resourcePath != null) {
						Messager.send(treeNode.resourcePath, MessageKey.SELECTED);
					}
				}
			}
		});

		add(tree, BorderLayout.CENTER);
		initDropIn();
		initPopmenu();
		initMessageListener();
	}

	private final void initMessageListener() {
		// to do
	}

	private final void initPopmenu() {
		//
		SwingUtil.addPopup(tree, new PopmenuListener() {
			@Override
			public void onInitPopmenu(JPopupMenu popupMenu) {
				if (tree.getSelectionPaths() != null) {
					// 删除类
					SwingUtil.addPopup(popupMenu, "menu.del_element", new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							TreePath[] treePaths = tree.getSelectionPaths();
							for (TreePath treePath : treePaths) {
								ResourceTreeModel treeModel = (ResourceTreeModel) treePath
										.getLastPathComponent();
								if (treeModel.resourcePath != null) {
									DataManage.getResourcePaths().remove(treeModel.resourcePath);
								}
							}
							DataManage.save();
							updateTree();
						}
					});
				}
				// 刷新
				SwingUtil.addPopup(popupMenu, "menu.refresh", new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						updateTree();
					}
				});
			}
		});
	}

	private final void updateTree() {
		ResourceTreeModel treeModel = new ResourceTreeModel();
		tree.setModel(new DefaultTreeModel(treeModel));
		tree.setCellRenderer(treeModel);
		//
		// TreePath treePath = new TreePath(treeModel);
		// this.tree.expandPath(treePath);
		// for (int i = 0; i < treeModel.getChildCount(); i++) {
		// TreePath cTreePath =
		// treePath.pathByAddingChild(treeModel.getChildAt(i));
		// this.tree.expandPath(cTreePath);
		// }
		// revalidate();
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

	class ResourceTreeModel extends DefaultTreeNode {
		// 图片元素
		private ResoucePath resourcePath;

		// 默认构造器 , 默认加载所有 类元素,和图片
		public ResourceTreeModel() {
			setUserObject(L.get("label.element_collections"));
			setAllowsChildren(true);
			setIcon(Icons.LAYER_FOLDER);

			// 文件类型缓存
			Hashtable<String, ResourceTreeModel> fileTypes = new Hashtable<String, ResourceTreeModel>();
			// 添加
			for (ResoucePath resoucePath : DataManage.getResourcePaths()) {
				String fileType = resoucePath.getFileType();
				//
				ResourceTreeModel fileTypeTreeModel = getFileTypeTreeModel(fileType, fileTypes);
				//
				fileTypeTreeModel.add(new ResourceTreeModel(resoucePath));
			}

			List<ResourceTreeModel> models = new ArrayList<>(fileTypes.size());
			models.addAll(fileTypes.values());
			models.sort(new Comparator<ResourceTreeModel>() {
				@Override
				public int compare(ResourceTreeModel o1, ResourceTreeModel o2) {
					return o1.getUserObject().toString().compareTo(o2.getUserObject().toString());
				}
			});
			for (ResourceTreeModel treeModel : models) {
				add(treeModel);
			}
		}

		// 文件类型文件夹构造器
		public ResourceTreeModel(String fileType) {
			setUserObject(fileType);
			setAllowsChildren(true);
			setIcon(Icons.LAYER_FOLDER);
		}

		// 图片文件夹构造器
		public ResourceTreeModel(File folder) {
			setUserObject(folder.getName());
			setAllowsChildren(true);
			setIcon(Icons.LAYER_FOLDER);
		}

		ResourceTreeModel(ResoucePath resoucePath) {
			this.resourcePath = resoucePath;
			setIcon(Icons.RESOURCE_FILE);
			if (resoucePath.exist()) {
				setUserObject(resoucePath.getAssetsPath());
			} else {
				setUserObject(resoucePath.getAssetsPath() + "(not exist)");
			}
		}

		protected final ResourceTreeModel getFileTypeTreeModel(String fileType,
				Hashtable<String, ResourceTreeModel> fileTypes) {
			if (fileType == null) {
				fileType = "";
			}
			ResourceTreeModel treeModel = fileTypes.get(fileType);
			if (treeModel == null) {
				treeModel = new ResourceTreeModel(fileType);
				fileTypes.put(fileType, treeModel);
				// add(treeModel);
			}
			return treeModel;

		}

		protected final void updateName(ClassPath classPath) {
			setUserObject(classPath.getClassName() + "[" + classPath.getElements().size() + "]");
		}
	}

}
