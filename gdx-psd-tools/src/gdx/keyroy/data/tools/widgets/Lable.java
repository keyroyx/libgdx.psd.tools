package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.psd.tools.util.L;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Lable extends JLabel {
	public Lable(String key) {
		super(L.get(key));
		setBorder(new EmptyBorder(8, 8, 8, 8));
	}
}
