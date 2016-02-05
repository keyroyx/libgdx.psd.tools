package gdx.keyroy.psd.tools.util;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	//
	public static final DropTarget addDropIn(Component component, final DropInAdapter dropInAdapter) {
		return new DropTarget(component, dropInAdapter);
	}

	public static class MouseRightButtonListener extends MouseButtonListener {
		@Override
		public boolean isListenerButton(MouseEvent e) {
			return e.getButton() == MouseEvent.BUTTON3;
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
			System.out.println("mousePressed");
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

}
