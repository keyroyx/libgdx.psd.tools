package gdx.keyroy.psd.tools.widgets;

import gdx.keyroy.psd.tools.util.Message;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.MessageListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class DialogProgress extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel h1;
	private JLabel h2;
	private JLabel h3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogProgress dialog = new DialogProgress();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogProgress() {
		setUndecorated(true);
		setBackground(new Color(1, 1, 1, 0.75f));
		setAlwaysOnTop(true);
		setBounds(100, 100, 480, 88);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(false);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		h1 = new JLabel("Title");
		h1.setFont(new Font("ËÎÌו", Font.PLAIN, 22));
		h1.setBounds(10, 10, 460, 15);
		contentPanel.add(h1);

		h2 = new JLabel("message label");
		h2.setBounds(10, 35, 460, 15);
		contentPanel.add(h2);

		h3 = new JLabel("hint");
		h3.setForeground(Color.LIGHT_GRAY);
		h3.setBounds(10, 60, 460, 15);
		contentPanel.add(h3);

		//
		Message.register(String.class, messageListener);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				windowClosed(e);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				Message.unregister(String.class, messageListener);
			}
		});
	}

	private MessageListener<String> messageListener = new MessageListener<String>() {
		@Override
		public void onMessage(String t, Object[] params) {
			if (is(MessageKey.H1, params)) {
				getH1().setText(t);
			} else if (is(MessageKey.H2, params)) {
				getH2().setText(t);
			} else {
				getH3().setText(t);
			}
			repaint();
		}
	};

	public JLabel getH1() {
		return h1;
	}

	public JLabel getH2() {
		return h2;
	}

	public JLabel getH3() {
		return h3;
	}
}
