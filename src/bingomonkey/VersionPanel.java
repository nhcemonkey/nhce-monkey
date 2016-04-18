package bingomonkey;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bingomonkey.Client.DownloadListener;

import common.LicenseDialog;
import common.LocalMessages;
import common.Main;

public class VersionPanel extends JPanel {

	private static final long serialVersionUID = -3053986875499549697L;
	public static final String NAME = "Mr. Monkey";
	public static final String SUB_NAME = "Bingo Monkey";
	public static final String VERSION = "1.2b";// Often Modified
	public static final String SOFT_HOST = "http://code.google.com/p/bingo-monkey/";
	public static final String SOFT_HOST_DOWNLOAD = "http://nhce-monkey.googlecode.com/files/";
	public static final String SOFT_HOST_UPLOAD = "http://uploads.code.google.com/upload/nhce-monkey";
	public static final String SOFT_HOST_EMAIL = "nhce.monkey@gmail.com";
	public static final String SOFT_HOST_CHECK_EMAIL = "nhce.monkey.sender@gmail.com";
	public static final String SOFT_HOST_PASS = LocalMessages.getString("key");
	public boolean newVerAvailable;
	boolean licLoading = false;
	public static JButton logoBtn;
	public static boolean googleAuthed = false;
	public static Client googleClient = new Client("utf-8");
	public static String updateDownLink, newVersion, newMainVersion; // Added in
																		// 1.2
	public static boolean downloading, downloaded; // Added in 1.2
	public static ReportDialog report;
	static boolean updateGreenVersion;

	public VersionPanel() {
		setLayout(new BorderLayout());
		JPanel logoPanel = new JPanel(new BorderLayout());
		report = new ReportDialog();
		// logoPanel.setBorder(new EmptyBorder(50, 10, 50, 10));
		add(logoPanel, BorderLayout.WEST);
		logoPanel.setPreferredSize(new Dimension(350, 50));
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBorder(new TitledBorder("关于"));
		add(rightPanel);
		JTextPane about = new JTextPane();
		about.setContentType("text/html");
		about.setBackground(BGMonkey.bgColor);
		// about.setLineWrap(true);
		// about.setWrapStyleWord(true);
		about.setEditable(false);
		rightPanel.add(new JScrollPane(about));
		logoBtn = new JButton("<html>" + SUB_NAME + "<br>当前版本：V " + VERSION
				+ "<br><font color=\"blue\">单击检测新版<font></html>", BGMonkey.getIconRes("logo.png", 128));
		JPanel logoBtnWrap = new JPanel(new BorderLayout());
		logoBtnWrap.add(logoBtn);
		JButton reportBtn = new JButton("提交Bug或建议", BGMonkey.getIconRes("report.png", 25));
		reportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				report.setVisible(true);
			}
		});
		JPanel btnPanel = new JPanel(new GridLayout(1, 0));
		btnPanel.add(reportBtn);
		final JButton licBtn = new JButton("查看许可", BGMonkey.getIconRes("license.png", 25));
		licBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						licLoading = true;
						String temp = licBtn.getText();
						licBtn.setText(temp + "(读取中...)");
						LicenseDialog.getInstance().setVisible(true);
						licBtn.setText(temp);
						licLoading = false;
					};
				}.start();
			}
		});
		btnPanel.add(licBtn);
		logoBtnWrap.add(btnPanel, BorderLayout.SOUTH);
		logoPanel.add(logoBtnWrap);
		logoBtn.setFont(new Font(logoBtn.getFont().getName(), Font.BOLD, 20));
		logoBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!newVerAvailable) {// 未检测到新版
					checkUpdate();
				} else {
					autoUpdate();
				}
			}

		});
		// about.setText("【作者】\nMonkey' father\t(e-mail:nhcemonkey@gmail.com)\n\n"
		// + "【声明】\n本程序的编写目的仅仅用于作者个人的技术研究与兴趣扩展，"+
		// "具体说是对Java网络编程、HTML解析等方面的实践探索，请不要滥用它或将它用于不正当的活动中！"+
		// "对由此造成的后果，作者本人概不负责！\n\n" +
		// "【版权】\n为了更好地与大家交流技术，本程序遵循GPL V3协议，完全开放源代码，并提供有限的编译支持。"+
		// "任何人都可以免费获取本程序的源代码及二进制程序。\n\n"+
		// "【项目托管】\n本项目目前托管在Google Code上，地址为："+SOFT_HOST+" ，源代码及程序可以从此处获得。");
		about.setText("<html><h2>作者</h2>Monkey' father&nbsp;&nbsp;(E-mail:<a href=\"mailto://nhcemonkey@gmail.com\">nhcemonkey@gmail.com</a>)"
				+ "<h2>声明</h2>本程序的编写目的仅仅用于作者个人的技术研究与兴趣扩展，"
				+ "具体说是对Java网络编程、HTML解析等方面的实践探索，<font color=\"blue\">请不要滥用它或将它用于不正当的活动中！"
				+ "对由此造成的后果，作者本人概不负责！</font>"
				+ "<h2>版权</h2>为了更好地与大家交流技术，本程序遵循<a href=\"http://www.gnu.org/copyleft/gpl.html\">GPL V3</a>协议，完全开放源代码，并提供有限的编译支持。"
				+ "任何人都可以免费获取本程序的源代码及二进制程序。"
				+ "<h2>项目托管</h2>本项目目前托管在Google Code上，地址为：<A href=\""
				+ SOFT_HOST
				+ "\">"
				+ SOFT_HOST
				+ "</a> ，源代码及程序可以从此处获得。"
				+ "<h2>参与</h2>如果您有兴趣，可以参与到本项目中，协助本项目的发展，具体请联系作者。"
				+ "<h2>反馈</h2>希望您能及时将发现的BUG或自己的意见与建议通过邮件反馈至作者，作者会及时改进程序。</html>");
		about.setCaretPosition(0);
		about.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e.getURL().getProtocol().equalsIgnoreCase("mailto")) {
						if (JOptionPane.showConfirmDialog(BGMonkey.bgm, "是否启动系统默认邮件客户端？", "请选择",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
							browseWithDesktop(e.getURL().toExternalForm());
					} else
						browseWithDesktop(e.getURL().toExternalForm());
				}
			}
		});

	}

	public static String getMainVersion() {

		try {
			final String html = BGMonkey.client.get(nhmonkey.VersionPanel.SOFT_HOST);
			if (html != null && html.length() > 0) {
				Document doc = Jsoup.parse(html);
				Element wiki = doc.getElementById("wikicontent");
				if (wiki != null) {
					Elements temp = wiki.getElementsByTag("h1");
					String version = temp.first().getElementsByTag("a").attr("name").replace('_', ' ');
					version = version.substring(version.indexOf(':') + 1).trim();
					return version;
				}
			}

		} catch (Exception e1) {
			final Exception e = e1;
			SwingUtilities.invokeLater(new Thread() {
				@Override
				public void run() {
					BGMonkey.output.error(e);
				}
			});
		}
		return null;
	}

	// -----------------------------
	public void checkUpdate() {
		// 使用线程防止Swing线程阻塞
		new Thread() {
			public void run() {
				try {
					if (downloading || downloaded)
						return;
					final String html = BGMonkey.client.get(SOFT_HOST);
					SwingUtilities.invokeLater(new Thread() {
						@Override
						public void run() {
							if (html != null && html.length() > 0) {
								Document doc = Jsoup.parse(html);
								Element wiki = doc.getElementById("wikicontent");
								if (wiki != null) {
									Elements temp = wiki.getElementsByTag("h1");
									String version = temp.first().getElementsByTag("a").attr("name").replace('_', ' ');
									version = version.substring(version.indexOf(':') + 1).trim();
									newVersion = version;
									BGMonkey.output.append("最新版本：" + version);
									if (version.compareToIgnoreCase(VERSION) > 0) {// 有新版
										logoBtn.setText("<html>" + SUB_NAME + "<br>当前版本：V " + VERSION
												+ "<br>最新版本：<font color=\"red\">V " + version
												+ "<br>单击开始升级<font></html>");
										newVerAvailable = true;
										BGMonkey.bgm.infoPanel.setSelectedIndex(3);
										// --------------Added in
										// 1.2-------------
										StringBuilder info = new StringBuilder();
										try {
											Elements links = temp.first().nextElementSibling().getElementsByTag("a");
											Properties props = System.getProperties(); // 获得系统属性集
											String osName = props.getProperty("os.name"); // 操作系统名称
											String osArch = props.getProperty("os.arch"); // 操作系统构架
											updateGreenVersion = false;
											if (osName.startsWith("Windows")) {
												if (osArch.equalsIgnoreCase("x86")) {
													if (links.size() > 2 && Main.isGreenVersion()){
														updateDownLink = links.get(3).attr("href");
														updateGreenVersion = true;
													}else
														updateDownLink = links.get(1).attr("href");
												} else if (osArch.endsWith("64"))
													updateDownLink = links.get(2).attr("href");
											}

											Elements h3s = wiki.getElementsByTag("h3");
											Element top = null, bot = null;
											for (Element h3 : h3s) {
												if (h3.text().equals(VERSION)) {
													bot = h3;
												} else if (h3.text().equals(version)) {
													top = h3;
												}
											}
											if (top != null && bot != null) {
												info.append("\n更新内容：\n");
												while (top != bot) {
													if (top.tagName().equalsIgnoreCase("h3"))
														info.append("==" + top.text() + "==");
													else
														info.append(top.text());
													info.append('\n');
													top = top.nextElementSibling();
												}
												info.append("\n");
											}
										} catch (Exception e) {
											info.delete(0, info.capacity());
											BGMonkey.output.error(e);
										}
										// --------------------------------------------
										BGMonkey.output.append("检测到新版本：" + version + ",建议您及时更新至最新版！");
										BGMonkey.speak("我有新版本了，快升级吧！");
										JOptionPane.showMessageDialog(BGMonkey.bgm, "检测到新版本：" + version
												+ ".\n建议您及时更新至最新版！\n" + info + "（点击Monkey图标开始升级！）", "信息",
												JOptionPane.INFORMATION_MESSAGE);
									} else {
										logoBtn.setText("<html>" + SUB_NAME + "<br>当前版本：V " + VERSION
												+ "<br><font color=\"green\">已是最新版本！<font></html>");
										BGMonkey.output.append("已是最新版！");
									}

								}
							}
						}
					});

				} catch (Exception e1) {
					final Exception e = e1;
					SwingUtilities.invokeLater(new Thread() {
						@Override
						public void run() {
							BGMonkey.output.error(e);
						}
					});
				}
			};
		}.start();

	}

	public static void autoUpdate() {

		if (updateDownLink == null || updateDownLink.length() <= 0) {
			BGMonkey.output.info("请自行从网站下载新版程序包，并替换现有程序！");
			browseWithDesktop(SOFT_HOST);
		} else {
			logoBtn.setText("<html>" + SUB_NAME + "<br>当前版本：V " + VERSION + "<br>最新版本：<font color=\"red\">V "
					+ newVersion + "<br>正在初始化下载...<font></html>");
			BGMonkey.client.clearDownloadListener();
			BGMonkey.client.addDownloadListener(new DownloadListener() {
				@Override
				public void downloadStateChanged(int down, int total) {
					if (total > 0)
						logoBtn.setText("<html>" + SUB_NAME + "<br>当前版本：V " + VERSION
								+ "<br>最新版本：<font color=\"red\">V " + newVersion + "<br>正在下载："
								+ (int) (100.0 * down / total) + "%<font></html>");
					else
						logoBtn.setText("<html>" + SUB_NAME + "<br>当前版本：V " + VERSION
								+ "<br>最新版本：<font color=\"red\">V " + newVersion + "<br>正在下载：" + (int) (down / 1000)
								+ "KB<font></html>");
					if (down == total) {
						logoBtn.setText("<html>" + SUB_NAME + "<br>当前版本：V " + VERSION
								+ "<br>最新版本：<font color=\"red\">V " + newVersion + "<br>下载完成！<font></html>");
					}
				}
			});
			new Thread() {
				public void run() {
					try {
						downloading = true;
						String mainVersion = getMainVersion();
						String fileName;
						if (System.getProperty("os.arch").endsWith("64"))
							fileName = "Mr. Monkey " + mainVersion + "(x64)";
						else
							fileName = "Mr. Monkey " + mainVersion ;
						if(updateGreenVersion)
							fileName += " green.exe";
						else
							fileName += ".exe";
						BGMonkey.client.downloadSliently(updateDownLink, System.getProperty("user.dir") + '\\',
								fileName, true);
						downloading = false;
						downloaded = true;
						JOptionPane.showMessageDialog(BGMonkey.bgm, "<html>已下载新版程序<font color=\'blue\'>" + fileName
								+ "</font>，即将退出本程序并运行新版！<br>（如果不需要保留旧版程序，请自行删除。）</html>", "信息",
								JOptionPane.INFORMATION_MESSAGE);
						BGMonkey.bgm.setVisible(false);
						Runtime.getRuntime().exec(fileName);
						System.exit(0);
					} catch (IOException e) {
						downloading = false;
						logoBtn.setText("<html>" + SUB_NAME + "<br>当前版本：V " + VERSION
								+ "<br>最新版本：<font color=\"red\">V " + newVersion + "<br>单击开始升级<font></html>");
						BGMonkey.output.error(e);
					}
				};
			}.start();

		}

	}
	private void logOnGoogle() throws Exception {
		logOnGoogle(true);
	}
	private void logOnGoogle(boolean showHint) throws Exception {
		googleClient.clearInput();
		Document doc = Jsoup.parse(googleClient.get("https://accounts.google.com/ServiceLogin"));
		Elements inputs = doc.getElementById("gaia_loginform").getElementsByTag("input");
		for (Element in : inputs) {
			// Modified in 2.3-1
			if (!in.attr("name").equalsIgnoreCase("Email") && !in.attr("name").equalsIgnoreCase("Passwd"))
				googleClient.addInput(in.attr("name"), in.attr("value"));
		}
		googleClient.addInput("Email", SOFT_HOST_EMAIL);
		googleClient.addInput("Passwd", SOFT_HOST_PASS);
		doc = Jsoup.parse(googleClient.post("https://accounts.google.com/ServiceLoginAuth", true));
		checkLogOnGoogleSuccess(doc.getElementsByTag("a").first().attr("href"));
		googleAuthed = true;
		
		if(showHint)
			BGMonkey.output.append("已登陆Google服务器！");
	}
	/**
	 * Added in 2.6b 检验是否真的登陆成功，对于新的IP，谷歌有附加验证！
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public void checkLogOnGoogleSuccess(String url) throws Exception {
		Document doc = Jsoup.parse(googleClient.get(url));
		Element e = doc.getElementById("emailAnswer");
		if (e == null)// 已经登陆成功
			return;

		// 第一次登陆本账号的IP，需要进行附加验证
		BGMonkey.output.append("首次登陆Google服务器，正在进行初始化操作...");
		Elements inputs = doc.getElementById("challengeform").getElementsByTag("input");
		googleClient.clearInput();
		for (Element in : inputs) {
			if (in.attr("name").equalsIgnoreCase("emailAnswer") )
				googleClient.addInput(in.attr("name"), SOFT_HOST_CHECK_EMAIL);
			else
				googleClient.addInput(in.attr("name"), in.attr("value"));
		}
		doc = Jsoup.parse(googleClient.post(url, true));
		String newUrl = doc.getElementsByTag("a").first().attr("href");
		System.out.println(googleClient.get(newUrl));
	}
	
	public String getIPInfo(){
		try {
			String html =  googleClient.get("http://int.dpool.sina.com.cn/iplookup/iplookup.php","gbk");
			Document doc = Jsoup.parse(html);
			return doc.text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void registerNewUser(){
		new Thread() {
			public void run() {
				try {
					if (!googleAuthed) {
						logOnGoogle(false);
					}
					if (!googleAuthed) {
						return;
					}
					
					Document doc = Jsoup.parse(googleClient.get("http://m.gmail.com"));
					String url = doc.getElementById("fts").attr("href");
					url = url.substring(0, url.lastIndexOf('/') + 1) + doc.getElementById("bnc").attr("href");
					doc = Jsoup.parse(googleClient.get(url));
					Element form = doc.getElementById("cp");
					url = doc.getElementById("fts").attr("href");
					url = url.substring(0, url.lastIndexOf('/') + 1) + form.attr("action");
					Elements inputs = form.getElementsByTag("input");
					googleClient.clearInput();
					for (Element in : inputs) {
						if (in.attr("type").equalsIgnoreCase("hidden"))
							googleClient.addInput(in.attr("name"), in.attr("value"));
					}
					googleClient.addInput("to", "nhce.monkey@gmail.com");
					googleClient.addInput("subject", "New User Registration");
					StringBuilder sb = new StringBuilder();
					sb.append("[Product Name: " + NAME + " - " + SUB_NAME+(Main.isGreenVersion()?" Green":"") + "]\n");
					sb.append("[Product Version: " + VERSION + "]\n");
					sb.append("[Platform: Java " + System.getProperty("java.version") + "]\n");
					sb.append("[System: " + System.getProperty("os.name") + " ");
					sb.append(System.getProperty("os.arch") + " ");
					sb.append(System.getProperty("os.version") + "]\n\n");
					sb.append("This report is submitted automatically for registering new user.");
					InetAddress addr = InetAddress.getLocalHost();
					sb.append("\n\n[Submitted From " + addr.getHostAddress() + " "
							+ addr.getHostName() + " " + System.getProperty("user.name") + "]\n");
					sb.append("[IP Information: "+getIPInfo()+"]");
					googleClient.addInput("body", sb.toString());
					googleClient.addInput("nvp_bu_send", "发送");
					googleClient.post(url);
					//NHMonkey.output.info("提交成功！感谢您的支持！");
				} catch (Exception e) {
					//NHMonkey.output.error(e);
					//NHMonkey.output.error("提交失败！");
				}
			};
		}.start();
	}


	public static void browseWithDesktop(String url) {
		try {
			Desktop desktop = Desktop.getDesktop();
			if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
				URI uri = new URI(url);
				desktop.browse(uri);
			} else {
				BGMonkey.output.warn("不支持系统浏览器调用！");
			}
		} catch (Exception e) {
			BGMonkey.output.error(e);
		}
	}
	public void addReport(String name, String report){
		VersionPanel.report.addReport(name, report);
	}
	public void addReportTitle(String title) {
		JTextField t = VersionPanel.report.title;
		String text = t.getText();
		if (text.length() > 0)
			t.setText(text + "+" + title);
		else
			t.setText(title);
	}

	public class ReportDialog extends JDialog {

		private static final long serialVersionUID = -243414879062549466L;
		JComboBox reportType;
		JTextField title, sign;
		JTextArea content, debugReport;
		JButton debugInfoBtn;
		JButton submitBtn;
		JDialog debugInfoDialog;
		final static String DEBUG_INFO_HEAD = "\n============================ Debug Info ============================\n";
		final static String DEBUG_INFO_END = "\n======================== End Of Debug Info =========================\n";

		public ReportDialog() {
			super(BGMonkey.bgm, true);
			setTitle("提交报告");
			setIconImage(BGMonkey.getIconRes("report.png").getImage());
			setResizable(false);
			setSize(600, 300);
			setLocationRelativeTo(BGMonkey.bgm);
			JPanel topPanel = new JPanel();
			add(topPanel, BorderLayout.NORTH);
			JPanel centerPanel = new JPanel(new BorderLayout());
			// centerPanel.add();
			debugReport = new JTextArea();
			debugReport.setLineWrap(true);
			debugReport.setWrapStyleWord(true);
			debugReport.setEditable(false);
			add(new JScrollPane(content = new JTextArea()));
			JPanel debugPanel = new JPanel(new BorderLayout(10, 0));
			debugPanel.add(new JLabel("调试信息"), BorderLayout.WEST);
			debugInfoBtn = new JButton("查看详细（空）");
			debugInfoBtn.setEnabled(false);
			debugInfoDialog = new JDialog(this);
			debugInfoDialog.setTitle("调试信息");
			debugInfoDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
			debugInfoDialog.setSize(800, 600);
			debugInfoDialog.setLocationRelativeTo(this);
			debugInfoDialog.add(new JScrollPane(debugReport));
			debugPanel.add(debugInfoBtn);
			centerPanel.add(debugPanel, BorderLayout.NORTH);
			topPanel.add(new JLabel("报告类型"));
			topPanel.add(reportType = new JComboBox());
			topPanel.add(new JLabel("简述"));
			topPanel.add(title = new JTextField(15));
			topPanel.add(debugPanel);
			content.setBorder(new TitledBorder("详细描述"));
			content.setLineWrap(true);
			content.setWrapStyleWord(true);
			reportType.addItem("Bug提交");
			reportType.addItem("高人指教");
			reportType.addItem("自由发言");
			JPanel botPanel = new JPanel();
			botPanel.add(new JLabel("尊姓大名（选填）"));
			botPanel.add(sign = new JTextField(10));
			botPanel.add(submitBtn = new JButton("提交"));
			add(botPanel, BorderLayout.SOUTH);
			debugInfoBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					debugInfoDialog.setVisible(true);
				}
			});
			submitBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					submitReport();
					BGMonkey.output.info("正在后台提交中,请稍等片刻！（最长可能需要几分钟时间）");
				}
			});
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent e) {
					BGMonkey.output.info("提交信息采用SSL加密传送，绝对不会泄露您的身份和隐私，请放心提交！");
				}
			});
		}

		public void addReport(String name, String report) {
			String timeStamp = " [" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()) + "]";
			debugReport.append("---------------- " + name+ timeStamp
					+ " ----------------\n");
			debugReport.append(report);
			debugReport.append("\n------------ End Of " + name+timeStamp
					+ " -------------\n");
			debugInfoBtn.setText("查看详情（" + (debugReport.getText().length() * 2) + "Byte）");
			debugInfoBtn.setEnabled(true);
		}
		
		private void submitReport() {
			new Thread() {
				public void run() {
					try {
						if (!googleAuthed) {
							logOnGoogle();
						}
						if (!googleAuthed) {
							BGMonkey.output.error("提交失败！\n登陆Google时未通过身份验证，请重试或改用电子邮件联系作者！谢谢！");
							return;
						}
						Document doc = Jsoup.parse(googleClient.get("http://m.gmail.com"));
						String url = doc.getElementById("fts").attr("href");
						url = url.substring(0, url.lastIndexOf('/') + 1) + doc.getElementById("bnc").attr("href");
						doc = Jsoup.parse(googleClient.get(url));
						Element form = doc.getElementById("cp");
						url = doc.getElementById("fts").attr("href");
						url = url.substring(0, url.lastIndexOf('/') + 1) + form.attr("action");
						Elements inputs = form.getElementsByTag("input");
						googleClient.clearInput();
						for (Element in : inputs) {
							if (in.attr("type").equalsIgnoreCase("hidden"))
								googleClient.addInput(in.attr("name"), in.attr("value"));
						}
						googleClient.addInput("to", "nhce.monkey@gmail.com");
						googleClient.addInput("subject", reportType.getSelectedItem() + "-" + title.getText());
						StringBuilder sb = new StringBuilder();
						sb.append("[Product Name: " + NAME + " - " + SUB_NAME +(Main.isGreenVersion()?" Green":"")+ "]\n");
						sb.append("[Product Version: " + VERSION + "]\n");
						sb.append("[Platform: Java " + System.getProperty("java.version") + "]\n");
						sb.append("[System: " + System.getProperty("os.name") + " ");
						sb.append(System.getProperty("os.arch") + " ");
						sb.append(System.getProperty("os.version") + "]\n\n");
						sb.append(content.getText());
						InetAddress addr = InetAddress.getLocalHost();
						sb.append("\n\n[Submitted By: " + sign.getText() + " From " + addr.getHostAddress() + " "
								+ addr.getHostName() + " " + System.getProperty("user.name") + "]\n");
						sb.append("[IP Information: " + getIPInfo() + "]\n");
						sb.append("\n========================= Programe LogArea =========================\n");
						sb.append(BGMonkey.output.getText());
						sb.append("===================== End Of Programe LogArea ======================\n");
						String text = debugReport.getText();
						if (text.length() > 0) {
							sb.append(DEBUG_INFO_HEAD);
							sb.append(text);
							sb.append(DEBUG_INFO_END);
						}
						googleClient.addInput("body", sb.toString());
						googleClient.addInput("nvp_bu_send", "发送");
						googleClient.post(url);
						BGMonkey.output.info("提交成功！感谢您的支持！");
					} catch (Exception e) {
						BGMonkey.output.error(e);
						BGMonkey.output.error("提交失败！\n可能是登陆Google时未通过身份验证，请重试或改用电子邮件联系作者！谢谢！");
					}
				};
			}.start();

		}
	}

}
