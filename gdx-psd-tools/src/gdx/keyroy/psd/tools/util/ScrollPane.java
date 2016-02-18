package gdx.keyroy.psd.tools.util;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class ScrollPane extends JScrollPane {
	public ScrollPane(final Component component) {
		JPanel panel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension dimension = component.getPreferredSize();
				return new Dimension(dimension.width + 10, dimension.height + 10);
			}
		};
		panel.setLayout(new CenterLayout());
		panel.add(component);
		setViewportView(panel);
	}
}
