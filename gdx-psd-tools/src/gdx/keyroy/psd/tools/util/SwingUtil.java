package gdx.keyroy.psd.tools.util;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

public class SwingUtil {

	public static final void initWindowsLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	public static final void center(Frame frame, Component target) {
		Rectangle rectangle = frame.getBounds();
		rectangle.setLocation(frame.getX(), frame.getY());
		target.setLocation((int) (rectangle.getX() + rectangle.getWidth() / 2 - target.getWidth() / 2),
				(int) (rectangle.getY() + rectangle.getHeight() / 2 - target.getHeight() / 2));
	}

	//
	public static final void center(Component target) {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rectangle = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration()
				.getBounds();
		target.setLocation((int) (rectangle.getWidth() / 2 - target.getWidth() / 2),
				(int) (rectangle.getHeight() / 2 - target.getHeight() / 2));
	}

	public static final void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new SwingUtil.MouseRightButtonListener() {
			@Override
			protected void onMouseClicked(MouseEvent e) {
				showMenu(e);
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
				popup.repaint();
			}
		});
	}

	public static final void addPopup(Component component, final PopmenuListener listener) {
		component.addMouseListener(new SwingUtil.MouseRightButtonListener() {
			@Override
			protected void onMouseClicked(MouseEvent e) {
				JPopupMenu popup = new JPopupMenu();
				listener.onInitPopmenu(popup);
				if (popup.getComponentCount() > 0) {
					popup.show(e.getComponent(), e.getX(), e.getY());
					popup.repaint();
				}
			}
		});
	}

	public static final void addPopup(JPopupMenu popupMenu, String label, ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem(L.get(label));
		menuItem.addActionListener(actionListener);
		popupMenu.add(menuItem);
	}

	//
	public static final DropTarget addDropIn(Component component, final DropInAdapter dropInAdapter) {
		return new DropTarget(component, dropInAdapter);
	}

	public static final void addDropOut(Component component, final OnPopOutListener onPopOutListener) {
		addDropOut(component, onPopOutListener, null);
	}

	public static final void addDropOut(Component component, final OnPopOutListener onPopOutListener,
			final DragDropEndListener listener) {
		// http://www.java2s.com/Code/Java/Swing-JFC/TreeDragandDrop.htm
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(component, DnDConstants.ACTION_COPY_OR_MOVE,
				new DragGestureListener() {

					@Override
					public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
						dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, new Transferable() {

							@Override
							public boolean isDataFlavorSupported(DataFlavor flavor) {
								return true;

							}

							@Override
							public DataFlavor[] getTransferDataFlavors() {
								return new DataFlavor[] { DataFlavor.stringFlavor };
							}

							@Override
							public Object getTransferData(DataFlavor flavor)
									throws UnsupportedFlavorException, IOException {
								return onPopOutListener.onPopOut();
							}
						}, new DragSourceAdapter() {
							@Override
							public void dragEnter(DragSourceDragEvent dragSourceDragEvent) {
								DragSourceContext context = dragSourceDragEvent.getDragSourceContext();
								int dropAction = dragSourceDragEvent.getDropAction();
								if ((dropAction & DnDConstants.ACTION_COPY) != 0) {
									context.setCursor(DragSource.DefaultCopyDrop);
								} else if ((dropAction & DnDConstants.ACTION_MOVE) != 0) {
									context.setCursor(DragSource.DefaultMoveDrop);
								} else {
									context.setCursor(DragSource.DefaultCopyNoDrop);
								}
							}

							@Override
							public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent) {
								if (dragSourceDropEvent.getDropSuccess()) {
									if (listener != null) {
										listener.dragDropEnd(dragSourceDropEvent);
									}
								}
							}
						});
					}
				});

	}

	public static class MouseRightButtonListener extends MouseButtonListener {
		@Override
		public boolean isListenerButton(MouseEvent e) {
			return e.getButton() == MouseEvent.BUTTON3;
		}
	}

	public static class MouseLeftButtonListener extends MouseButtonListener {
		@Override
		public boolean isListenerButton(MouseEvent e) {
			return e.getButton() == MouseEvent.BUTTON1;
		}
	}

	private static abstract class MouseButtonListener implements MouseListener, MouseMotionListener {
		private boolean isPressed;

		public abstract boolean isListenerButton(MouseEvent e);

		protected boolean checkMotion() {
			return false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (isPressed) {
				onMouseDragged(e);
			}
		}

		protected void onMouseDragged(MouseEvent e) {

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (checkMotion()) {
				if (isListenerButton(e)) {
					onMouseMoved(e);
				}
			} else {
				onMouseMoved(e);
			}

		}

		protected void onMouseMoved(MouseEvent e) {

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (isListenerButton(e)) {
				onMouseClicked(e);
			}
		}

		protected void onMouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (isListenerButton(e)) {
				isPressed = true;
				onMousePressed(e);
			}
		}

		protected void onMousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isPressed = false;
			if (isListenerButton(e)) {
				onMouseReleased(e);
			}
		}

		protected void onMouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (checkMotion()) {
				if (isListenerButton(e)) {
					onMouseEntered(e);
				}
			} else {
				onMouseEntered(e);
			}

		}

		protected void onMouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (checkMotion()) {
				if (isListenerButton(e)) {
					onMouseExited(e);
				}
			} else {
				onMouseExited(e);
			}

		}

		protected void onMouseExited(MouseEvent e) {

		}

	}

	public static class DropInAdapter extends java.awt.dnd.DropTargetAdapter {
		// http://www.java2s.com/Code/Java/Swing-JFC/TreeDragandDrop.htm
		private Point point;

		@Override
		public void drop(DropTargetDropEvent event) {
			point = event.getLocation();
			if (isDropAccept(event) == false) {
				event.rejectDrop();
				return;
			}

			event.acceptDrop(DnDConstants.ACTION_COPY);
			Transferable transferable = event.getTransferable();
			DataFlavor[] flavors = transferable.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				DataFlavor dataFlavor = flavors[i];
				try {
					if (dataFlavor.equals(DataFlavor.javaFileListFlavor)) {
						List<File> files = new ArrayList<File>();
						List<?> fileList = (List<?>) transferable.getTransferData(dataFlavor);
						Iterator<?> iterator = fileList.iterator();
						while (iterator.hasNext()) {
							File from = (File) iterator.next();
							files.add(from);
						}
						onDropIn(files);
					} else if (dataFlavor.equals(DataFlavor.stringFlavor)) {
						String string = (String) transferable.getTransferData(dataFlavor);
						onDropIn(string);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			event.dropComplete(true);
			onDropComplete();
		}

		public void onDropIn(List<File> files) {
			for (File file : files) {
				onDropIn(file);
			}
		}

		public void onDropIn(File file) {

		}

		public void onDropIn(String string) {

		}

		public boolean isDropAccept(DropTargetDropEvent event) {
			return ((event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0);
		}

		public void onDropComplete() {

		}

		public final Point getPoint() {
			return point;
		}

		public final Point getDropPoint() {
			return point;
		}

	}

	public interface OnPopOutListener {
		public String onPopOut();
	}

	public interface DragDropEndListener {
		public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent);
	}
}
