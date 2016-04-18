package common;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import nhmonkey.NHMonkey;

import org.tool.MSC;
import org.tool.MSCTool;
import org.tool.SoundTouch;
import org.tool.ThunderService;

import bingomonkey.BGMonkey;
import bluemonkey.BlueMonkey;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import com.exe4j.Controller;
import common.animation.EffectPane;
import common.animation.GhostEffectAnimator;

public class Main extends JFrame {

	private static final long serialVersionUID = -2256800884151677429L;
	public String[] args;
	/**
	 * 谨慎直接使用！！！
	 */
	public static Main instance;
	final int ICON_SIZE = 24;
	final int BIGICON_SIZE = 64;
	String[] preFileNames = new String[] { "72480_0_5.wav", "20302980_1_5.wav", "-1034676098_3_5.wav",
			"-1954435949_1_5.wav", "-2137068113_3_5.wav" };
	private static int monkeyCount = 0;
	//private JLabel headLabel;
	//private boolean loading = false;

	public static synchronized Main getInstance() {
		if (instance == null)
			instance = new Main(null);
		return instance;
	}

	public static synchronized void registerMonkey() {
		monkeyCount++;
	}

	public static synchronized void hideMain() {
		if (instance != null)
			instance.setVisible(false);
	}

	public static synchronized void unregisterMonkey() {
		monkeyCount--;
	}

	public static synchronized void queryExit() {
		if (monkeyCount <= 0 && (Main.instance == null || !Main.instance.isVisible()))
			System.exit(0);
	}

	// public static Recorder recorder = new Recorder();
	public static Client client = new Client();

	public static synchronized void setLookAndFeel(JFrame frame) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			StringBuilder str = new StringBuilder();
			LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
			for (LookAndFeelInfo i : info)
				str.append(i.getClassName() + "\n");
			JOptionPane.showMessageDialog(frame, str, "Warning!", JOptionPane.WARNING_MESSAGE);
		}
	}

	public static boolean isGreenVersion() {
		String str = LocalMessages.getString("green");
		if (str != null && str.equals("true"))
			return true;
		else
			return false;
	}

	private void writeToSplash(String str) {
		try {
			Controller.writeMessage(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Main(String[] args) {
		super("Mr. Monkey 系列 V" + nhmonkey.VersionPanel.VERSION);
		writeToSplash("Loading LookAndFeel...");
		setLookAndFeel(this);

		writeToSplash("Loading Layout...");
		instance = this;
		setIconImage(getLogo("Mr. Monkey.png", 48).getImage());
		this.args = args;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 300);
		setResizable(false);
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new GridLayout(1, 0)), topPanel = new JPanel(), bottomPanel = new JPanel(
				new BorderLayout());
		final JButton btn1, btn2, btn3;
		mainPanel.add(btn1 = new JButton("NHCE Monkey", getLogo("nhce.png", 130)));
		btn1.setHorizontalTextPosition(SwingConstants.CENTER);
		btn1.setVerticalTextPosition(SwingConstants.BOTTOM);
		mainPanel.add(btn2 = new JButton("Lange Monkey", getLogo("lg.png", 130)));
		btn2.setHorizontalTextPosition(SwingConstants.CENTER);
		btn2.setVerticalTextPosition(SwingConstants.BOTTOM);
		mainPanel.add(btn3 = new JButton("Bingo Monkey", getLogo("bg.png", 130)));
		btn3.setHorizontalTextPosition(SwingConstants.CENTER);
		btn3.setVerticalTextPosition(SwingConstants.BOTTOM);

		topPanel.add(new JLabel("请点击你需要的猴子..."));
		JPanel mmPanel = new JPanel();
		JPanel signPanel = new JPanel();
		signPanel.add(new JLabel("Copyright © nhce.monkey@gmail.com"));
		bottomPanel.add(signPanel, BorderLayout.SOUTH);
		bottomPanel.add(mmPanel);
		// JComboBox cBox = MSCTool.getInstance().getComboBox();
		// JSlider slider = MSCTool.getInstance().getSlider();
		// mmPanel.add(new
		// JLabel("<html><font color=\'blue\' size =5><b>萌属性:</b></font></html>"));
		final JToggleButton speakBtn = MSCTool.getInstance().getSwitch();
		final Icon soundIcon = getLogo("sound.png", ICON_SIZE);
		final Icon noSoundIcon = getLogo("no_sound.png", ICON_SIZE);
		// speakBtn.setIcon(soundIcon);

		// System.out.println("sound:"+soundIcon);
		// System.out.println("no_sound:"+noSoundIcon);

		speakBtn.setIconTextGap(0);
		speakBtn.setToolTipText("语音开关");
		mmPanel.add(speakBtn);
		JButton advBtn = new JButton();
		advBtn.setToolTipText("增强功能状态");
		mmPanel.add(advBtn);
		JButton greenBtn = new JButton();
		greenBtn.setToolTipText("版本信息");
		mmPanel.add(greenBtn);
		JButton homeBtn = new JButton(getLogo("home.png", ICON_SIZE));
		homeBtn.setToolTipText("项目主页");
		mmPanel.add(homeBtn);
		JButton cleanBtn = new JButton(getLogo("clean.png", ICON_SIZE));
		cleanBtn.setToolTipText("清理缓存");
		mmPanel.add(cleanBtn);
		JButton newVersionBtn = new JButton(getLogo("newVersion.png", ICON_SIZE));
		newVersionBtn.setToolTipText("更新日志");
		mmPanel.add(newVersionBtn);
		JButton srcBtn = new JButton(getLogo("java.png", ICON_SIZE));
		srcBtn.setToolTipText("浏览源码");
		mmPanel.add(srcBtn);

		final JDialog releaseNoteDialog = new JDialog(this);
		releaseNoteDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		releaseNoteDialog.setSize(600, 400);
		releaseNoteDialog.setLocationRelativeTo(this);
		releaseNoteDialog.setTitle("更新日志");
		releaseNoteDialog.setLayout(new BorderLayout());
		JPanel leftPanel = new JPanel();
		leftPanel.add(new JLabel(getLogo("newVersion.png", BIGICON_SIZE)));
		releaseNoteDialog.add(leftPanel, BorderLayout.WEST);
		JTextArea releaseNoteArea = new JTextArea();
		releaseNoteArea.setEditable(false);
		releaseNoteDialog.add(new JScrollPane(releaseNoteArea));

		writeToSplash("Loading EventHandlers...");
		btn1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				if (!loading)
//					new Thread() {
//						public void run() {
//							loading = true;
//							String text = headLabel.getText();
//							headLabel.setText("<html><font color='blue'>正在加载，请稍候...</font></html>");
//							new NHMonkey();
//							loading = false;
//							headLabel.setText(text);
//						};
//					}.start();
				hideMain();
				new NHMonkey();
			}
		});
		btn1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				NHMonkey.speak("你好！");
			}
		});
		btn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				if (!loading)
//					new Thread() {
//						public void run() {
//							loading = true;
//							String text = headLabel.getText();
//							headLabel.setText("<html><font color='blue'>正在加载，请稍候...</font></html>");
//							new BlueMonkey();
//							loading = false;
//							headLabel.setText(text);
//						};
//					}.start();
				hideMain();
				new BlueMonkey();
			}
		});
		btn2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				BlueMonkey.speak("Hello!", false);
			}
		});
		btn3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				if (!loading)
//					new Thread() {
//						public void run() {
//							loading = true;
//							String text = headLabel.getText();
//							headLabel.setText("<html><font color='blue'>正在加载，请稍候...</font></html>");
//							new BGMonkey();
//							loading = false;
//							headLabel.setText(text);
//						};
//					}.start();
				hideMain();
				new BGMonkey();
			}
		});
		btn3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				BGMonkey.speak("Hi!");
			}
		});
		speakBtn.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					MSCTool.getInstance().enable = true;
					BlueMonkey.speak("Yes sir!", false);
					speakBtn.setIcon(soundIcon);
				} else {
					NHMonkey.speak("我不说了！");
					speakBtn.setIcon(noSoundIcon);
				}
			}
		});
		srcBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CodeViewer.getInstance().setVisible(true);
			}
		});
		homeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(Main.this, "即将使用系统默认浏览器打开本项目主页！", "信息", JOptionPane.INFORMATION_MESSAGE,
						getLogo("home.png", BIGICON_SIZE));
				nhmonkey.VersionPanel.browseWithDesktop(nhmonkey.VersionPanel.SOFT_HOST);
			}
		});
		cleanBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(Main.this,
						"是否清除Monkey的所有缓存文件？\n\n清理缓存可以删除用户的使用记录、配置信息\n和一些缓存的数据文件，保证使用后不留踪迹！", "清理缓存",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, getLogo("clean.png", BIGICON_SIZE));
				if (reply != JOptionPane.OK_OPTION)
					return;
				cleanMonkeyCache();
			}
		});
		newVersionBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				releaseNoteDialog.setVisible(true);
			}
		});
		if (isGreenVersion()) {
			greenBtn.setIcon(getLogo("green.png", ICON_SIZE));
			greenBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(Main.this,
							"这是Monkey绿色版！\n\n由于绿色版自带最新JRE，所以无需安装Java亦可使用！不过程序体积较大。\n\n如果您一般在安装好Java的计算机上使用Monkey,\n可以到官网("
									+ nhmonkey.VersionPanel.SOFT_HOST + ")下载Monkey标准版，\n标准版体积相对小得多，方便携带！", "信息",
							JOptionPane.INFORMATION_MESSAGE, getLogo("green.png", BIGICON_SIZE));
				}
			});
		} else {
			greenBtn.setIcon(getLogo("not_green.png", ICON_SIZE));
			greenBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(Main.this,
							"这是Monkey标准版！\n\n标准版需要您的计算机提前安装好1.6以上版本的Java,否则程序无法运行。\n\n如果想要在不安装Java的计算机上(目前仅限Win32系统)使用Monkey,\n请到官网("
									+ nhmonkey.VersionPanel.SOFT_HOST + ")下载Monkey绿色版！\n当然，由于绿色版自带JRE，所以体积稍大。", "信息",
							JOptionPane.INFORMATION_MESSAGE, getLogo("not_green.png", BIGICON_SIZE));
				}
			});
		}

//		Controller.registerStartupListener(new StartupListener() {
//
//			@Override
//			public void startupPerformed(final String args) {
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						Main.getInstance().setVisible(true);
//						Main.getInstance().setExtendedState(NORMAL);
//					}
//				});
//			}
//		});

		writeToSplash("Loading Resources...");
		try {
			TranslateTool.initHistory();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		if (MSCTool.getInstance().enable) {
			speakBtn.setIcon(soundIcon);
		} else {
			speakBtn.setIcon(noSoundIcon);
		}
		try {// 预置声效，以优化初始化体验

			for (String name : preFileNames) {
				File testTTFFile = new File(MSCTool.tempTTSDir + name);
				if (!testTTFFile.exists()) {
					extractAttach(name, MSCTool.tempTTSDir);
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		releaseNoteArea.setText(getReleaseNote());

		writeToSplash("Checking Advanced Functions...");
		if (!checkAdvanceAvailable()) {
			advBtn.setIcon(getLogo("star_black.png", ICON_SIZE));
			advBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(Main.this,
							"当前增强功能未能启用，可能是以下原因导致的：\n\n" + "1.增强功能暂不支持您的操作系统\n2.多个Monkey程序在同时运行，仅有第一个Monkey能调用增强功能\n3.防火墙或其他安全软件的拦截\n\n"
									+ "受影响的增强功能有：[语音实时合成] [语音实时转换] [拼写检查] [调用迅雷下载引擎]", "增强功能状态",
							JOptionPane.INFORMATION_MESSAGE, getLogo("star_black.png", BIGICON_SIZE));
				}
			});
		} else {
			advBtn.setIcon(getLogo("star.png", ICON_SIZE));
			advBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(Main.this, "当前增强功能处于开启状态！\n\n"
							+ "增强功能包括：[语音实时合成] [语音实时转换] [拼写检查] [调用迅雷下载引擎]", "增强功能状态", JOptionPane.INFORMATION_MESSAGE,
							getLogo("star.png", BIGICON_SIZE));
				}
			});
		}

		// new Thread(){public void run() {
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// checkMultiInstance();
		// };}.start();

		// mmPanel.add(new JLabel("卖萌属性:"));
		// mmPanel.add(cBox);
		// mmPanel.add(new JLabel("卖萌速度:"));
		// mmPanel.add(slider);
		// bottomPanel.add(new JLabel("Copyright © nhce.monkey@gmail.com"));

		writeToSplash("Loading Window Fx...");
		EffectPane contentPane = new EffectPane();
		contentPane.setAnimator(new GhostEffectAnimator());
		contentPane.setLayout(new BorderLayout());
		contentPane.add(topPanel, BorderLayout.NORTH);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		contentPane.add(mainPanel);
		add(contentPane);
		setVisible(true);

		// noX64SupportHint();// NOTODO 32位版此函数注释掉！！！！
	}

	/**
	 * changed in 2.6b
	 * @return
	 */
	public boolean checkAdvanceAvailable() {
//		if (checkMultiInstance())
//			return false;
//		Properties props = System.getProperties(); // 获得系统属性集
//		String osName = props.getProperty("os.name"); // 操作系统名称
//		String osArch = props.getProperty("os.arch"); // 操作系统构架
//		if (osName.startsWith("Windows")) {
//			if (osArch.equalsIgnoreCase("x86"))
//				return true;
//		}
//		return false;
		return MSC.loadSuccess && SoundTouch.loadSuccess && ThunderService.loadSuccess;
	}

	private String getReleaseNote() {
		try {
			DataInputStream input = new DataInputStream(this.getClass().getResourceAsStream("src/ReleaseNote.txt"));
			byte[] b = new byte[1024];
			int l = 0;
			StringBuilder sb = new StringBuilder();
			while ((l = input.read(b, 0, 1024)) > 0) {
				sb.append(new String(b, 0, l));
			}
			input.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private void cleanMonkeyCache() {
		File dir1 = new File(nhmonkey.Recorder.TEMP_DIR);
		File dir2 = new File(bluemonkey.Recorder.TEMP_DIR);
		File dir3 = new File(bingomonkey.Recorder.TEMP_DIR);
		File dir4 = new File(MSCTool.tempTTSDir);

		deleteDir(dir1);
		deleteDir(dir2);
		deleteDir(dir3);

		// 需要保证极少数的几个文件一直存在，没办法，否则很容易出现Crash
		File[] files = dir4.listFiles();
		if (files != null)
			F: for (File file : files) {
				if (file.isDirectory())
					continue;
				for (String name : preFileNames)
					if (file.getName().equals(name))
						continue F;
				try {
					file.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		JOptionPane.showMessageDialog(this, "缓存清除成功！");
	}

	public void deleteDir(File dir) {
		File[] files = dir.listFiles();
		if (files != null)
			for (File file : files) {
				if (file.isDirectory())
					deleteDir(file);
				else
					try {
						file.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		try {
			dir.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkMultiInstance() {
		try {
			Process process = Runtime.getRuntime().exec("cmd.exe /c tasklist");
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			int count = 0;
			while ((line = input.readLine()) != null) {
				if (line.startsWith("Mr. Monkey")) {
					count++;
				}
			}
			if (count > 1) {
				// JOptionPane.showMessageDialog(this,
				// "发现已有Monkey在运行！\n在多Monkey运行时，只有第一个Monkey能调用动态链接库，\n因此本Monkey无力调用附加功能！\n"
				// +
				// "涉及的功能包括：语音实时合成、语音实时转换、迅雷下载引擎、单词拼写检查。\n不过基本功能还是可以正常使用的,呵呵...");
				return true;
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static synchronized void commonToolClose() {
		try {
			TranslateTool.storeHistory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void noX64SupportHint() {
		if (!System.getProperty("os.arch").equals("x86")) {
			JOptionPane.showMessageDialog(this, "<html><h2>敬告：本程序从2.5版起<font color=\'red\'>不再对64位提供完整支持！</font></h2>\n"
					+ "主要原因是部分插件需要调用本地代码，导致跨平台调试难度增大，作者本人由于没有充足时间，所以只能暂时放弃。\n"
					+ "不再提供支持不代表不再发布新版，而是对于新发布的64位程序包，不再保证能正常运行，可能绝大部分功能是可用的，也可能根本无法启动。\n"
					+ "如有贤士愿意弥补这一不足，敬请到Google下载本项目源码来参与项目维护，并请告之原作者！"
					+ "\n\n注：2.5版以前的基本功能理论上是可以使用的，但可能会与新版功能发生冲突，所以64位用户如遇问题，请使用32位版程序或使用2.5版以前的64位版本！");
		}
	}

	public ImageIcon getLogo(String fileName, int iconSize) {
		ImageIcon icon = null;
		URL url = this.getClass().getResource("logos/" + fileName);
		if (url != null) {
			icon = new ImageIcon(url);
			if (iconSize > 0)
				icon.setImage(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
			return icon;
		}
		return icon;
	}

	public void extractAttach(String file, String dir) throws Exception {
		URL url = this.getClass().getResource("attach/" + file);
		InputStream input = url.openStream();
		File dist = new File(dir);
		if (!dist.exists())
			dist.mkdirs();
		FileOutputStream output = new FileOutputStream(dist.getCanonicalFile() + "/" + file);
		int l = 0;
		byte[] tmp = new byte[2048];
		while ((l = input.read(tmp)) != -1) {
			output.write(tmp, 0, l);
		}
		System.out.println("[Attachment] " + file + " extracted to " + dir);
		input.close();
		output.close();
	}

	public static synchronized void hideTemp() {
		try {
			File cacheFile = new File("eaeb1592ce728dd28d4f99cae237aba4");
			if (cacheFile.exists() && !cacheFile.isHidden()) {
				Runtime.getRuntime().exec("attrib +H \"" + cacheFile.getAbsolutePath() + "\"");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void cleanTemp() {
		try {
			File cacheFile = new File("eaeb1592ce728dd28d4f99cae237aba4");
			if (cacheFile.exists()) {
				File[] files = cacheFile.listFiles();
				for (File f : files)
					f.delete();
				cacheFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		NativeInterface.open();
		final String[] args_c = args;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main(args_c);
			}
		});
		NativeInterface.runEventPump();
	}
}
