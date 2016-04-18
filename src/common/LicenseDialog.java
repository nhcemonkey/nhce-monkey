package common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class LicenseDialog extends JDialog {

	private static final long serialVersionUID = -5814111684839059639L;
	JPanel leftPanel;
	JTextArea licenseArea;
	Font titleFont = new Font("Aria", Font.BOLD, 20);
	Font contentFont = new Font("Aria", Font.BOLD, 14);
	int logoSizeW = 260, logoSizeH = 60;
	private static LicenseDialog instance;

	public static LicenseDialog getInstance() {
		if (instance == null)
			instance = new LicenseDialog();
		return instance;
	}

	private LicenseDialog(Frame owner) {
		super(owner);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			StringBuilder str = new StringBuilder();
			LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
			for (LookAndFeelInfo i : info)
				str.append(i.getClassName() + "\n");
			JOptionPane.showMessageDialog(this, str, "Warning!", JOptionPane.WARNING_MESSAGE);
		}
		setSize(900, 500);
		setIconImage(new ImageIcon(this.getClass().getResource("logos/license.png")).getImage());
		setLocationRelativeTo(owner);
		setTitle("License - 许可说明");
		setLayout(new BorderLayout());
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		leftPanel = new JPanel(new GridLayout(0, 1));
		licenseArea = new JTextArea();
		licenseArea.setEditable(false);
		licenseArea.setFont(contentFont);
		licenseArea.setLineWrap(true);
		licenseArea.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(licenseArea);
		add(scroll);
		scroll = new JScrollPane(leftPanel);
		scroll.setPreferredSize(new Dimension(320, 300));
		add(scroll, BorderLayout.WEST);
		licenseArea.setText("\n本窗口展示了本项目及所使用的开源项目或开放资源的许可信息。\n\n"+
		"请在左侧点击项目或资源的名称以查看其对应许可！\n\n(注：仅展示直接引用的项目或资源)");
		load();
	}

	public void load() {
		addLicense("Mr. Monkey", "gplv3");
		addLicense("Apache HTTP Components", "apachev2");
		addLicense("DJ Native", "lgplv2.1");
		addLicense("JFreeChart", "lgplv3");
		addLicense("jsoup", "mit-jsoup");
		addLicense("XStream", "bsd-xstream");
		addLicense("LanguageTool", "lgplv3");
		addLicense("Hunspell", "3l-hunspell");
		addLicense("SoundTouch", "lgplv2.1");
		addLicense("iFLY讯飞语音云", "iFLY");
		addLicense("迅雷下载开放引擎", "xunlei");
		addLicense("图标集", "icon");
	}

	public void addLicense(String name, String licenseName) {
		final JButton btn = new JButton(name);
		btn.setHorizontalTextPosition(JButton.CENTER);
		btn.setVerticalTextPosition(JButton.BOTTOM);
		btn.setFont(titleFont);
		ImageIcon logoImage = null;
		try {
			URL logo = this.getClass().getResource("logos/" + name + ".png");
			logoImage = new ImageIcon(logo);
		} catch (Exception e) {
		}
		String license = null;
		try {
			DataInputStream input = new DataInputStream(this.getClass().getResourceAsStream(
					"licenses/" + licenseName + ".txt"));
			byte[] b = new byte[1024];
			int l = 0;
			StringBuilder sb = new StringBuilder();
			while ((l = input.read(b, 0, 1024)) > 0) {
				sb.append(new String(b, 0, l));
			}
			input.close();
			license = sb.toString();
		} catch (Exception e) {
		}
		if (logoImage != null) {
			Image img = logoImage.getImage();
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			double ratio = logoSizeW * 1.0 / width;
			double ratio2 = logoSizeH * 1.0 / height;
			if (ratio2 < ratio)
				ratio = ratio2;
			btn.setIcon(new ImageIcon(img.getScaledInstance((int) (width * ratio), (int) (height * ratio),
					Image.SCALE_SMOOTH)));
		}
		if (license != null) {
			final String lic = license;
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					licenseArea.setText(lic);
					licenseArea.setCaretPosition(0);
				}
			});
		}
		leftPanel.add(btn);
	}

	public LicenseDialog() {
		this(null);
	}

	public static void main(String[] args) {
		LicenseDialog ld = new LicenseDialog();
		ld.setVisible(true);
	}

}
