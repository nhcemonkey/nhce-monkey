package bingomonkey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tool.LanguageTool;
import org.tool.MSCTool;

import bingomonkey.Recorder.UserInfo;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import common.Main;
import common.TimeTool;

public class BGMonkey extends JFrame {

	private static final long serialVersionUID = -5473857141202749453L;
	public static boolean working = true;
	public static BGMonkey bgm;
	public static Color bgColor = new Color(214, 217, 223);;
	public static Client client;
	protected static LogArea output;
	public VersionPanel versionPanel;
	private Timer checkVersionTimer;
	public Recorder recorder;
	private boolean newUser = true, logged = false;
	private JButton loginBtn, submitBtn;
	private JTextField school, classID, name, stdID;
	private JPanel topicPanel, titlePanel, submitHisPanel, remarkPanel;
	private JLabel title;
	private String bgBaseUrl = "http://writing.bingoenglish.com";
	private String classUrl;
	private UserInfo userInfo;
	private JTextArea order;
	private JTabbedPane leftPanel;
	JTabbedPane infoPanel;
	private Element submitForm;
	public Editor editor;
	private LogPanel logPanel;
	public CheckPanel checkPanel;
	public String currentID;
	public JButton scoreBtn;
	public JTextArea remarkArea;
	public ActionListener scoreBtnLis;

	public BGMonkey() {
		super(VersionPanel.SUB_NAME + " V" + VersionPanel.VERSION);
		Main.registerMonkey();
		bgm = this;
		setMinimumSize(new Dimension(980, 660));
		setIconImage(getIconRes("logo.png", 48).getImage());
		Main.setLookAndFeel(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		output = new LogArea();
		output.setBackground(bgColor);
		output.append("正在执行初始化...");
		client = new Client();
		final JScrollPane outputScroll = new JScrollPane();
		outputScroll.setViewportView(output);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		add(splitPane);
		splitPane.add(infoPanel = new JTabbedPane(JTabbedPane.LEFT), JSplitPane.BOTTOM);
		infoPanel.addTab("程序日志", getIconRes("log.png"), logPanel = new LogPanel());
		// infoPanel.addTab("用户信息", getIconRes("user.png"), userInfoPanel = new
		// UserInfoPanel());
		infoPanel.addTab("在线词典", getIconRes("dictionary.png"), new ToolPanel());
		infoPanel.addTab("检查工具", getIconRes("tools.png"), checkPanel = new CheckPanel());
		infoPanel.addTab("版本信息", getIconRes("version.png"), versionPanel = new VersionPanel());
		logPanel.add(outputScroll);
		JPanel mainPanel = new JPanel();
		output.setEditable(false);
		mainPanel.setLayout(new BorderLayout());
		loadMainPanel(mainPanel);
		splitPane.add(mainPanel, JSplitPane.TOP);

		cleanErrorLogs();
		recorder = new Recorder();
		// 检查更新，周期10分钟
		checkVersionTimer = new Timer(600000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// if (config.getInfo().checkUpdate) {
				VersionPanel panel = (VersionPanel) versionPanel;
				panel.checkUpdate();
				// }
			}
		});
		// splitPane.setDividerLocation(0.2);
		checkVersionTimer.setRepeats(true);
		checkVersionTimer.setInitialDelay(20000);
		checkVersionTimer.start();// 立即启动更新检查
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				// 执行清理
				working = false;
				if (checkVersionTimer != null && checkVersionTimer.isRunning()) {
					checkVersionTimer.stop();
				}
				speak("白白！");
				Main.commonToolClose();
				bgm.setVisible(false);
				new Thread(){public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Main.cleanTemp();
					Main.unregisterMonkey();
					Main.queryExit();
				};}.start();
			}
		});
		try {
			if ((userInfo = recorder.readUserInfo()) != null) {
				school.setText(userInfo.school);
				classID.setText(userInfo.classID);
				name.setText(userInfo.name);
				stdID.setText(userInfo.stdID);
				classUrl = userInfo.classUrl;
				newUser = false;
			}
		} catch (Throwable e1) {
			output.append("尝试解析用户记录失败");
		}
		setLocationRelativeTo(null);

		setVisible(true);
		splitPane.setDividerLocation(0.7);
		output.append("启动成功！");
		if (!newUser) // Modified in 1.0-2
			stdID.requestFocus();
//		else
//			versionPanel.registerNewUser();
		String version = VersionPanel.VERSION;
		// int index = version.lastIndexOf('-');
		// if(index > 0){
		// String mainVersion = version.substring(0,index);
		// String minVersion = version.substring(index+1);
		// version = mainVersion+"第"+minVersion;
		// }
		speak("我是bingo monkey " + version + "版，亲！");
		// new Thread() {
		// public void run() {
		// try {
		// Thread.sleep(3000);
		// System.out.println(WeatherTool.getInstance().getWeatherInfo());
		// // speak("我现在只会讲几句话，但以后会升级的啦！");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// };
		// }.start();
		Main.hideMain();
	}

	public static void speak(String cnText) {
		MSCTool.getInstance().TTFPlay(cnText, 0);
	}

	private void loadMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topicPanel = new JPanel();
		topicPanel.setLayout(new GridLayout(0, 1));
		JScrollPane scroll = new JScrollPane(topicPanel);
		scroll.setPreferredSize(new Dimension(310, 100));
		JPanel leftWrapPanel = new JPanel(new BorderLayout());
		leftPanel = new JTabbedPane();
		leftWrapPanel.add(leftPanel);
		// leftPanel.setBorder(new TitledBorder(""));
		leftPanel.addTab("题目列表", scroll);

		// JButton backBtn = new JButton("返回列表");
		// leftWrapPanel.add(backBtn, BorderLayout.SOUTH);

		leftPanel.addTab("题目要求", new JScrollPane(order = new JTextArea()));
		leftPanel.addTab("提交记录", new JScrollPane(submitHisPanel = new JPanel()));
		submitHisPanel.setPreferredSize(new Dimension(280, 1000));
		leftPanel.addTab("批改结果", new JScrollPane(remarkPanel = new JPanel()));
		remarkPanel.setLayout(new BorderLayout());
		remarkPanel.setPreferredSize(new Dimension(310, 100));
		scoreBtn = new JButton();
		scoreBtn.setVisible(false);
		remarkArea = new JTextArea();
		remarkArea.setVisible(false);
		JPanel panel = new JPanel();
		panel.add(scoreBtn);
		remarkArea.setLineWrap(true);
		remarkArea.setWrapStyleWord(true);
		scoreBtn.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 72));
		remarkArea.setFont(new Font("Aria", Font.PLAIN, 14));
		remarkPanel.removeAll();
		remarkArea.setEditable(false);
		remarkPanel.add(panel, BorderLayout.NORTH);
		remarkPanel.add(new JScrollPane(remarkArea));
		// remarkPanel.add(new JLabel("本功能尚未实现！"));// NOTODO 实现此功能！
		order.setEditable(false);
		order.setBackground(bgColor);
		order.setLineWrap(true);
		order.setWrapStyleWord(true);
		order.setFont(new Font("Aria", Font.PLAIN, 14));
		// leftPanel.setMinimumSize(new Dimension(300, 100));
		mainPanel.add(leftWrapPanel, BorderLayout.WEST);
		topPanel.setBorder(new TitledBorder(""));

		mainPanel.add(topPanel, BorderLayout.NORTH);
		editor = new Editor();
		JPanel editorPane = new JPanel();
		// Font font=new Font( "宋体 ",Font.PLAIN,18) ;
		// JTextArea editor = new JTextArea();
		// editor.setFont(font);
		editorPane.setLayout(new BorderLayout());
		editorPane.add(editor);
		mainPanel.add(editorPane);
		// editor.setMinimumSize(new Dimension(400, 332));
		topPanel.add(new JLabel("学校"));
		topPanel.add(school = new JTextField(10));
		topPanel.add(new JLabel("班级"));
		topPanel.add(classID = new JTextField(10));
		topPanel.add(new JLabel("姓名"));
		topPanel.add(name = new JTextField(10));
		topPanel.add(new JLabel("学号"));
		topPanel.add(stdID = new JTextField(10));
		topPanel.add(loginBtn = new JButton("登陆"));
		titlePanel = new JPanel();
		titlePanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		editorPane.setMinimumSize(new Dimension(200, 100));
		title = new JLabel("请先登录并选择作文题目！");
		Font font = title.getFont().deriveFont(Font.BOLD, 18f);
		if (font != null)
			title.setFont(font);
		titlePanel.add(title);
		titlePanel.add(submitBtn = new JButton("提交"));
		JButton emergencyBtn = new JButton("紧急提交");
		titlePanel.add(emergencyBtn);
		JButton checkBtn = new JButton("<html><font color=\'blue\'>检查拼写和语法</font><html>");
		titlePanel.add(checkBtn);

		submitBtn.setEnabled(false);
		editorPane.add(titlePanel, BorderLayout.NORTH);

		LanguageTool.buildPanel(editor.area, checkPanel.grammarCheckPane, checkPanel.checkBtn);
		// backBtn.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// leftPanel.setSelectedIndex(0);
		// }
		// });
		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!logged)
					login();
				else {
					logoff();
				}
			}
		});
		checkBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BGMonkey.bgm.infoPanel.setSelectedIndex(2);
				new Thread() {
					public void run() {
						BGMonkey.bgm.checkPanel.checkBtn.doClick();
					};
				}.start();
			}
		});

		stdID.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					// loginListener.actionPerformed(new
					// ActionEvent(e.getSource(), e.getID(), ""));
					loginBtn.doClick();
			}
		});
		submitBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				submit();
			}
		});
		emergencyBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(bgm,
						"如果您发现在提交截止时间前已来不及认真写作文了，我给您一提示：\n\n您可以将题目中的语句复制粘贴几遍，然后直接提交。幸运的话，70分是稳的！\n\n但我不对此做任何保障哦！\n\n由于这还是存在一定风险的，所以就暂时不开发自动紧急提交功能了，呵呵...",
						"Tip", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	public static void confirmBug(String name, String debugInfo, String ps){
		String str = "程序出BUG了么？\n\n";
		if(ps != null && ps.length() > 0){
			str += ps+"\n\n";
		}
		str += "如果确定是BUG，是否希望立即将此BUG提交至作者？\n(相关调试信息会自动附加在报告中)";
		int reply = JOptionPane.showConfirmDialog(bgm, str,"这是BUG么？",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,getIconRes("bug.png", 64));
		if(reply == JOptionPane.YES_OPTION){
			bgm.versionPanel.addReport(name,debugInfo);
			VersionPanel.report.setVisible(true);
		}
	}
	public void logoff() {
		school.setEnabled(true);
		classID.setEnabled(true);
		name.setEnabled(true);
		stdID.setEnabled(true);
		loginBtn.setEnabled(true);
		loginBtn.setText("登陆");
		leftPanel.setSelectedIndex(0);
		topicPanel.removeAll();
		submitHisPanel.removeAll();
		order.setText("");
		scoreBtn.setVisible(false);
		remarkArea.setVisible(false);
		title.setText("请先登录并选择作文题目！");
		submitBtn.setEnabled(false);
		logged = false;
	}

	public void login() {

		school.setEnabled(false);
		classID.setEnabled(false);
		name.setEnabled(false);
		stdID.setEnabled(false);
		loginBtn.setEnabled(false);
		loginBtn.setText("登陆中...");
		title.setText("请先登录并选择作文题目！");
		submitBtn.setEnabled(false);
		new Thread() {
			public void run() {
				try {
					boolean pass = false;
					if (!newUser && classUrl != null && classUrl.length() > 0) {
						if (userInfo.school.equals(school.getText()) && userInfo.classID.equals(classID.getText())
								&& userInfo.name.equals(name.getText()) && userInfo.stdID.equals(stdID.getText()))
							pass = true;
					}
					if (!pass) {
						if (school.getText().length() <= 0 || classID.getText().length() <= 0
								|| name.getText().length() <= 0 || stdID.getText().length() <= 0) {
							output.warn("请将个人信息填写完整！");
							classUrl = null;
							logoff();
							return;
						}

						output.append("正在登陆中...");
						Document doc = Jsoup.parse(client.get(bgBaseUrl + "/www/index.php/student/home"));
						Elements schoolList = doc.getElementsByClass("school_item");
						String schoolID = null;
						for (Element e : schoolList) {
							if (e.text().trim().equals(school.getText())) {
								schoolID = e.getElementsByTag("a").first().attr("href");
								schoolID = schoolID.substring(schoolID.lastIndexOf('/') + 1, schoolID.length());
								break;
							}
						}
						if (schoolID == null) {
							output.error("未找到对应学校！");
							classUrl = null;
							logoff();
							return;
						}

						client.clearInput();
						client.addInput("teacher_name", classID.getText());
						client.addInput("school_id", schoolID);
						doc = Jsoup.parse(client.post(bgBaseUrl + "/www/index.php/student/school/search", true));
						Elements found = doc.getElementsByClass("cutline");
						for (int i = 1; i < found.size(); i++) {
							if (found.get(i).text().equals(classID.getText())) {
								classUrl = found.get(i).getElementsByTag("a").first().attr("href");
								break;
							}
						}
						if (classUrl == null) {
							output.error("未找到对应班级！");
							classUrl = null;
							logoff();
							return;
						}

						int reply = JOptionPane.showConfirmDialog(
								bgm,
								"<html>请仔细确认你输入的姓名与学号是否正确:<br><h1>姓名：" + name.getText() + "</h1><h1>学号："
										+ stdID.getText() + "</h1><br>(冰果系统并不能检查此种错误！)</html>", "警告！",
								JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if (reply != JOptionPane.YES_OPTION) {
							logoff();
							return;
						}
					} else
						output.append("正在登陆中...");

					Document doc = Jsoup.parse(client.get(bgBaseUrl + classUrl));
					Elements topics = doc.getElementsByClass("cutline");
					setTopics(topics);
					recorder.writeUserInfo(school.getText(), classID.getText(), name.getText(), stdID.getText(),
							classUrl);
					leftPanel.setSelectedIndex(0);
					loginBtn.setEnabled(true);
					logged = true;
					loginBtn.setText("切换用户");
					output.append("登陆成功!");
					output.append("用户身份:" + school.getText() + "-" + classID.getText() + "-" + name.getText() + "-"
							+ stdID.getText());
					speak(name.getText() + "同学，" + TimeTool.getTimePeriod() + "好！");
				} catch (Exception e) {
					output.error("登录失败！请检查网络连接是否通畅！");
					classUrl = null;
					logoff();
				}
			};
		}.start();

	}

	public void setTopics(Elements topics) {
		topicPanel.removeAll();
		for (final Element t : topics) {
			String text = t.text();
			String title = text.substring(text.indexOf("标题") + 3, text.indexOf("Directions:")).trim();
			int maxLength = 35;
			if (title.length() > maxLength)
				title = title.substring(0, maxLength - 3) + "...";
			JButton btn = new JButton();
			if (text.contains("可以提交")) {
				btn.setText("<html><font color=\'blue\'><b>" + title + "</b></font></html>");
				btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						selectTopic(bgBaseUrl + t.getElementsByTag("a").first().attr("href"), true);
					}
				});
			} else if (text.contains("已经批改")) {
				btn.setText("<html>" + title + "</html>");
				btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						selectTopic(bgBaseUrl + t.getElementsByTag("a").first().attr("href"), false);
					}
				});
			} else {
				btn.setText("<html><i>" + title + "</i></html>");
				btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						output.info("正在批改！");
					}
				});
			}
			btn.setPreferredSize(new Dimension(250, 30));
			topicPanel.add(btn);
			leftPanel.setSelectedIndex(0);
		}
		title.setText("请选择作文题目！");
	}

	public void selectTopic(final String topicUrl, final boolean enableSubmit) {
		new Thread() {
			public void run() {
				try {
					output.append("正在读取题目信息...");
					scoreBtn.setVisible(false);
					remarkArea.setVisible(false);
					submitHisPanel.removeAll();
					order.setText("");
					Document doc = Jsoup.parse(client.get(topicUrl));
					Element e = doc.getElementsByClass("wrapper-left").first();
					String[] temp = topicUrl.split("/");
					String topicID = temp[temp.length - 1];
					currentID = topicID;
					int wordMin = 0;
					order.setText("编号：\n\n" + topicID + "\n\n");
					for (int i = 0; i < 2; i++) {
						Elements em = e.child(i).children();
						for (Element el : em) {
							String text = el.text();
							order.append(text + "\n\n");
							int index = text.indexOf("字数要求: ");
							if (index >= 0)
								wordMin = Integer.parseInt(text.substring(index + 5).trim());
						}
					}
					order.setCaretPosition(0);
					String text = e.text();
					String t = text.substring(text.indexOf("标题") + 3, text.indexOf("Directions:")).trim();
					title.setText(t);
					submitForm = doc.getElementsByTag("form").first();
					submitBtn.setEnabled(enableSubmit);
					leftPanel.setSelectedIndex(1);
					output.append("已选题目:" + t);
					editor.wordMin = wordMin;
					checkSubmitHistory(topicID);
				} catch (Exception e) {
					output.error(e);
				}

			};
		}.start();
	}

	public static void debugOutput(String output) {
		JFrame frame = new JFrame();
		JTextArea area = new JTextArea(output);
		JScrollPane pane = new JScrollPane(area);
		frame.add(pane);
		frame.pack();
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void checkSubmitHistory(final String topicID) {
		new Thread() {
			public void run() {
				try {
					output.append("正在检查提交记录...");
					submitHisPanel.removeAll();
					client.clearInput();
					client.addInput("wid", topicID);
					client.addInput("stu_no", stdID.getText());
					client.addInput("submit", "查询");
					Document doc = Jsoup.parse(client.post(bgBaseUrl + "/www/index.php/student/history/search", true));
					Elements es = doc.getElementsByClass("cutline");
					int findRemark = -1;
					for (int index = es.size() - 1; index >= 0; index--) {
						Element e = es.get(index);
						String text = e.text();
						final String time = text.substring(text.indexOf("提交时间："), text.indexOf("姓名："));

						// final StringBuilder s = new StringBuilder();
						// String[] temp = e.html().split("<br />");
						// for (int i = 5; i < temp.length - 1; i++)
						// s.append(temp[i] + "\n");
						// final String content = s.toString().replace("&nbsp;",
						// " ");
						final String content = htmlToStr(e.html());
						String writing = "";
						int scoreIndex = 0;
						if ((scoreIndex = content.indexOf("得分：")) > 0) {// 已批改
							writing = content.substring(content.indexOf("正文：") + 4, content.indexOf("得分"));
							if (index == es.size() - 1) {// 最新的记录
								int remarkIndex = content.indexOf("评语：");
								String score = content.substring(scoreIndex + 3, remarkIndex).trim();
								String remark = content.substring(remarkIndex);
								scoreBtn.setText(score);
								remarkArea.setText(remark);
								scoreBtn.setVisible(true);
								remarkArea.setVisible(true);
								leftPanel.setSelectedIndex(3);
								findRemark = index;
							}
						} else {// 未批改
							writing = content.substring(content.indexOf("正文：") + 4, content.indexOf("作文尚未批改"));
						}
						// debugOutput(content);
						final int lineLimit = 52;
						String tmp = writing;
						if (tmp.length() > lineLimit)
							tmp = writing.substring(0, lineLimit);
						JButton btn = new JButton("<html><h2>" + time + "</h2><br>" + tmp + "...</html>");
						// if (index == 0)
						// editor.setContent(content);
						// btn.setPreferredSize(new Dimension(300, 20));
						submitHisPanel.add(btn);
						ActionListener actLis = new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								JDialog dialog = new JDialog(BGMonkey.bgm);
								dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
								JTextArea area = new JTextArea();
								dialog.add(new JScrollPane(area));
								dialog.setTitle("提交记录 - " + time);
								dialog.setSize(600, 400);
								dialog.setLocationRelativeTo(BGMonkey.bgm);
								area.setFont(editor.area.getFont());
								area.setEditable(false);
								area.setLineWrap(true);
								area.setWrapStyleWord(true);
								area.setText(content);
								area.setCaretPosition(0);
								dialog.setVisible(true);
							}
						};
						btn.addActionListener(actLis);
						if (index == findRemark) {
							if (scoreBtnLis != null)
								scoreBtn.removeActionListener(scoreBtnLis);
							scoreBtn.addActionListener(actLis);
							scoreBtnLis = actLis;
						}
					}
					if (es.size() > 0) {
						output.append("发现" + es.size() + "次提交记录！");
						if (findRemark < 0)
							leftPanel.setSelectedIndex(2);
					} else
						output.append("未发现提交记录。");
				} catch (Exception e) {
					output.error(e);
				}
			};
		}.start();
	}

	public void showGrade() {

	}

	public void submit() {
		try {
			if (!editor.wordEnough) {
				int reply = JOptionPane.showConfirmDialog(bgm, "当前字数低于要求的最少字数，是否继续提交？", "询问",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (reply != JOptionPane.YES_OPTION) {
					return;
				}
			}
			checkPanel.checkBtn.doClick();
			if (checkPanel.grammarCheckWrong != 0 || checkPanel.spellCheckWrong != 0) {
				int reply = JOptionPane.showConfirmDialog(bgm,
						"<html>以上警告<font color=\'blue\' size=16><b>仅供参考</b></font>，是否忽略警告并继续提交？</html>", "询问",
						JOptionPane.YES_NO_OPTION);
				if (reply != JOptionPane.YES_OPTION) {
					return;
				}
			}
			if (submitForm == null) {
				output.error("表单未读取！");
				return;
			}
			client.clearInput();
			Elements inputs = submitForm.getElementsByTag("input");
			for (Element in : inputs) {
				if (in.attr("type").equalsIgnoreCase("hidden") || in.attr("name").equalsIgnoreCase("TITLE"))
					client.addInput(in.attr("name"), in.attr("value"));
			}
			client.addInput("STU_NO", stdID.getText());
			client.addInput("STU_NAME", name.getText());
			client.addInput("SCHOOL_NAME", school.getText());
			client.addInput("CLASSES", classID.getText());
			client.addInput("CONTENT", editor.getContent());
			client.post(bgBaseUrl + "/www/index.php/student/writing/submit");
			output.info("提交成功！");
			checkSubmitHistory(currentID);
		} catch (Exception e) {
			output.error("提交失败！");
		}
	}

	public static void main(String[] args) {
		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new BGMonkey();
			}
		});
		NativeInterface.runEventPump();
	}

	private void cleanErrorLogs() {
		File[] files = new File(System.getProperty("user.dir")).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.equals("error.log") || (name.endsWith(".log") && name.contains("hs_err_pid")))
					return true;
				else
					return false;
			}
		});
		for (File file : files)
			file.delete();
	}

	public static ImageIcon getIconRes(String fileName) {
		return getIconRes(fileName, 20);
	}

	public static ImageIcon getIconRes(String fileName, int iconSize) {
		ImageIcon icon = null;
		URL url = bgm.getClass().getResource("res/" + fileName);
		if (url != null) {
			icon = new ImageIcon(url);
			if (iconSize > 0)
				icon.setImage(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
			return icon;
		}

		return icon;
	}

	public static class LogArea extends JTextArea {
		private static final long serialVersionUID = -4837188851065576013L;

		/**
		 * 自动换行，加上时间戳，并且自动滚动日志到最后一行。
		 */
		@Override
		public void append(String str) {
			super.append("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()) + "]  ");
			super.append(str + "\n");
			output.setCaretPosition(output.getText().length());
		}

		public void info(String str) {
			this.append(str);
			speak(str);
			JOptionPane.showMessageDialog(bgm, str, "信息", JOptionPane.INFORMATION_MESSAGE);
		}

		public void warn(String str) {
			this.append(str);
			speak("警告：" + str);
			JOptionPane.showMessageDialog(bgm, str, "警告", JOptionPane.WARNING_MESSAGE);
		}

		public void error(String str) {
			this.append(str);
			speak("错误：" + str);
			JOptionPane.showMessageDialog(bgm, str, "错误", JOptionPane.ERROR_MESSAGE);
		}

		public void error(Throwable e) {
			append("程序出现错误:" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public static String htmlToStr(String htmlStr) {
		StringBuilder result = new StringBuilder();
		boolean flag = true;
		boolean newPara = false;
		if (htmlStr == null) {
			return null;
		}
		htmlStr = htmlStr.replace("\"", "");
		char[] a = htmlStr.toCharArray();
		int length = a.length;
		for (int i = 0; i < length; i++) {
			if (a[i] == '<') {
				flag = false;
				if (a[i + 1] == 'p' && (a[i + 2] == '>' || a[i + 2] == ' '))
					newPara = true;
				else if (a[i + 1] == 'b' && a[i + 2] == 'r' && a[i + 3] == ' ')
					newPara = true;
				else
					newPara = false;
				continue;
			}
			if (a[i] == '>') {
				flag = true;
				continue;
			}
			if (flag == true) {
				if (newPara) {
					result.append("\n");
					newPara = false;
				}
				result.append(a[i]);
			}
		}
		return result.toString().replace("&nbsp;", " ");
	}

}
