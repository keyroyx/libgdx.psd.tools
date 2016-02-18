package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.data.tools.models.ClassElement;
import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.psd.tools.util.GridFollowLayout;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.Messager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

//ÔªËØÃæ°å
@SuppressWarnings("serial")
public class TabClassPathPanel extends JPanel implements ContainerListener {
	protected ClassPath classPath;

	public TabClassPathPanel(ClassPath classPath) {
		this.classPath = classPath;
		setLayout(new BorderLayout(0, 0));
		scrollPane = new JScrollPane();
		add(scrollPane);
		update();
	}

	protected final void update() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridFollowLayout());
		panel.setBackground(Color.WHITE);
		for (ClassElement element : classPath.getElements()) {
			panel.add(new ClassElementComponent(element));
		}
		panel.revalidate();
		scrollPane.setViewportView(panel);
		repaint();
	}

	@Override
	public void componentAdded(ContainerEvent e) {
		Messager.register(ClassPath.class, regionListener);
	}

	@Override
	public void componentRemoved(ContainerEvent e) {
		Messager.unregister(ClassPath.class, regionListener);
	}

	protected final File getClassFolder(Class<?> clazz) {
		File folder = new File(clazz.getName());
		if (folder.exists() == false) {
			folder.mkdirs();
		}
		return folder;
	}

	private MessageListener<ClassPath> regionListener = new MessageListener<ClassPath>() {
		@Override
		public void onMessage(ClassPath t, Object[] params) {
			if (is(MessageKey.UPDATE, params) && t.equals(classPath)) {
				update();
			}
			repaint();
		}
	};
	private JScrollPane scrollPane;

	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}
