package bluemonkey;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;


import org.apache.http.cookie.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tool.MSCTool;

import bluemonkey.Recorder.ConfigInfo;
import bluemonkey.Recorder.UserInfo;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import common.LocalMessages;
import common.Main;
import common.TimeTool;
import common.TranslateTool;
import common.WindowsRegistryTool;

public class BlueMonkey extends JFrame {
	private static final long serialVersionUID = 2498048932285608680L;
	static BlueMonkey bm;
	static JTextArea ansDetail;
	static LogArea output;
	static DefaultTableModel ansTableModel;
	static JPanel contentPanel, previewPanel, allInfoPanel, logPanel, versionPanel;
	static JTabbedPane infoPanel;
	static ConfigDialog config;
	static String username, password;
	static boolean logged = false;
	static boolean itemReady = false;
	static boolean autoBrowse = false;
	static boolean inTray = false;
	static boolean firstFillAns = true;
	static Thread autoThread;
	static JWebBrowser browser;
	static JLabel browserText;
	static JTable ansTable;
	static JButton readAns, writeAns, fillAns, submitAns;
	static ImageIcon submitIcon, submitOKIcon;
	static ActionListener autoBtnActionListener;
	static CardLayout cardLayout;
	static Color bgColor = new Color(214, 217, 223);
	static Client client;
	static String host;
	static final String LGSCRIPT_NAME = "LGScript2.exe";

	final static String NOPAGE = "没有需要显示的页面";
	static Recorder recorder;
	static TrayTool trayTool;
	static Timer checkVersionTimer;
	Thread lgMoniterTimer;
	static Thread enterClassRoomAction;
	static boolean newUser = true;// Added in 2.2.1

	static Document homePageDoc = null;
	static ArrayList<StudyPlan> plans = new ArrayList<StudyPlan>();
	static boolean lgSystemOn = false;
	static boolean checkLG = true, checkLGStop = false;
	static Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance();

	static boolean updateChecked;
	public static boolean working = true;// Added in 1.2
	private static boolean queryingScript = false;
	WindowAdapter windowAdapter;

	// static boolean submitting;

	public BlueMonkey() {
		super(VersionPanel.SUB_NAME + " V" + VersionPanel.VERSION);
		Main.registerMonkey();
		bm = this;
		setMinimumSize(new Dimension(880, 560));
		Main.setLookAndFeel(this);
		recorder = new Recorder();
		setSize(1024, 720);
		setIconImage(getIconRes("logo.png", 48).getImage());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		output = new LogArea();
		output.setBackground(bgColor);
		output.append("正在执行初始化...");
		client = new Client();
		final JScrollPane outputScroll = new JScrollPane();
		outputScroll.setViewportView(output);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		add(splitPane);
		JPanel mainPanel;
		// System.out.println(new ImageIcon("info.png").getIconHeight());
		infoPanel = new JTabbedPane(JTabbedPane.LEFT);
		// infoPanel.addTab("综合信息", getIconRes("info.png"), allInfoPanel = new
		// JPanel());
		infoPanel.addTab("程序日志", getIconRes("log.png"), logPanel = new LogPanel());
		// infoPanel.addTab("用户信息", getIconRes("user.png"), userInfoPanel = new
		// UserInfoPanel());
		// infoPanel.addTab("辅助工具", getIconRes("tools.png"), toolPanel = new
		// ToolPanel());
		infoPanel.addTab("版本信息", getIconRes("version.png"), versionPanel = new VersionPanel());
		infoPanel.setMinimumSize(new Dimension(100, 155));
		// infoPanel.setPreferredSize(new Dimension(100, 135));
		splitPane.add(infoPanel, JSplitPane.BOTTOM);
		splitPane.add(mainPanel = new JPanel(), JSplitPane.TOP);
		output.setEditable(false);
		// outputScroll.setMinimumSize(new Dimension(100, 60));
		mainPanel.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		// displayPanel = new JPanel();
		// displayPanel.setLayout(new BorderLayout());
		browser = new Browser();
		// outputScroll.setMinimumSize(new Dimension(200, 50));
		logPanel.add(outputScroll);
		// browser.setJavascriptEnabled(true);
		// browser.setVisible(false);
		// JScrollPane p = new JScrollPane(browser);
		// p.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		previewPanel = new JPanel();
		previewPanel.setBorder(new TitledBorder(""));
		cardLayout = new CardLayout();
		previewPanel.setLayout(cardLayout);
		previewPanel.add(browser, "browser");
		browserText = new JLabel(NOPAGE, SwingConstants.CENTER);
		// previewText.setAlignmentX(110.5f);
		// previewText.setBackground(Color.WHITE);
		browserText.setForeground(Color.GRAY);
		Font font = browserText.getFont();
		browserText.setFont(new Font(font.getName(), Font.BOLD, 20));
		previewPanel.add(browserText, "text");
		cardLayout.show(previewPanel, "text");
		// preview(null);
		contentPanel.add(previewPanel);
		// input = new JTextArea();
		// input.setLineWrap(true);
		// input.setWrapStyleWord(true);
		// ansTable = new JTable() {
		// private static final long serialVersionUID = -3261090591766326664L;
		//
		// @Override
		// public void changeSelection(int rowIndex, int columnIndex, boolean
		// toggle, boolean extend) {
		// super.changeSelection(rowIndex, columnIndex, toggle, extend);
		// // String str = ansTable.getModel().getValueAt(rowIndex,
		// // 1).toString();
		// // ansDetail.setText(str+"\n"+Translater.translate(str));
		// String answer = ansTable.getModel().getValueAt(rowIndex,
		// 1).toString();
		// ansDetail.setText(answer);
		// ansDetail.setCaretPosition(0);
		// if (answer.length() > 1 && !answer.equals("NG"))
		// ToolPanel.lookUp(answer);
		// }
		// };
		//
		// ansTableModel = new UneditableTableModel();
		// ansTable.setModel(ansTableModel);
		// ansTableModel.addColumn("序号");
		// ansTableModel.addColumn("参考答案");
		// ansTable.getColumnModel().getColumn(0).setPreferredWidth(35);
		// ansTable.getColumnModel().getColumn(1).setPreferredWidth(200);

		// JTableHeader header = new JTableHeader();
		// header.add(new JLabel("序号"));
		// header.add(new JLabel("答案"));
		// answerTable.setTableHeader(header);

		// answerTable.setLayout(new GridLayout(0, 2));
		// JPanel rightPanel = new JPanel();
		// rightPanel.setPreferredSize(new Dimension(250, 300));
		// rightPanel.setLayout(new BorderLayout());
		// JPanel ansPanel = new JPanel();
		// ansPanel.setMinimumSize(new Dimension(50, 210));
		// ansPanel.setLayout(new BorderLayout());
		// ansPanel.add(new JScrollPane(ansTable));
		// rightPanel.add(ansPanel);
		// ansDetail = new JTextArea();
		// ansDetail.setBackground(bgColor);
		// ansDetail.setLineWrap(true);
		// ansDetail.setWrapStyleWord(true);
		// ansDetail.setEditable(false);
		// JScrollPane ansDetailScroll = new JScrollPane(ansDetail);
		// ansDetailScroll.setBorder(new TitledBorder("详细信息"));
		// ansDetailScroll.setPreferredSize(new Dimension(20, 120));
		// ansPanel.add(ansDetailScroll, BorderLayout.SOUTH);
		// submitIcon = getIconRes("submit.png");
		// submitOKIcon = getIconRes("submit_ok.png");
		// readAns = new JButton("读取答案", getIconRes("import.png"));
		// writeAns = new JButton("导出答案", getIconRes("export.png"));
		// fillAns = new JButton("填入答案", getIconRes("fill_form.png"));
		// submitAns = new JButton("提交答案", submitIcon);
		// // submitAns.setEnabled(false);
		// JPanel rightBottomPanel = new JPanel();
		// rightBottomPanel.setLayout(new GridLayout(2, 2));
		// rightBottomPanel.add(readAns);
		// rightBottomPanel.add(writeAns);
		// rightBottomPanel.add(fillAns);
		// rightBottomPanel.add(submitAns);
		// rightPanel.add(rightBottomPanel, BorderLayout.SOUTH);
		// contentPanel.add(rightPanel, BorderLayout.EAST);
		JPanel topBigPanel = new JPanel();
		topBigPanel.add(topPanel);
		// JButton configBtn = new JButton("设置", getIconRes("config.png", 32));
		// configBtn.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// config.showDialog();
		// }
		// });
		// topBigPanel.add(configBtn);
		mainPanel.add(topBigPanel, BorderLayout.NORTH);
		mainPanel.add(contentPanel);
		// topPanel.setLayout(new GridLayout(1,0,0,0));
		// topPanel.setBorder(new TitledBorder(""));
		final JPanel logInfo = new JPanel();
		final JPanel sectionInfo = new JPanel();
		sectionInfo.setVisible(false);
		logInfo.setBorder(new TitledBorder(""));
		sectionInfo.setBorder(new TitledBorder(""));
		topPanel.add(logInfo);
		topPanel.add(sectionInfo);
		final JLabel server = new JLabel("服务器地址");
		logInfo.add(server);
		final JTextField serverIP = new JTextField(host, 12);
		logInfo.add(serverIP);
		final JLabel user = new JLabel("用户名");
		logInfo.add(user);
		final JTextField userName = new JTextField(10);
		logInfo.add(userName);
		final JLabel passLabel = new JLabel("密码");
		logInfo.add(passLabel);
		final JPasswordField pass = new JPasswordField(10);
		logInfo.add(pass);
		final JButton login = new JButton("登陆", getIconRes("login.png"));
		logInfo.add(login);
		final JLabel classLabel = new JLabel("班级");
		classLabel.setVisible(false);
		logInfo.add(classLabel);
		final JComboBox classNum = new JComboBox();
		classNum.setVisible(false);
		logInfo.add(classNum);
		final JLabel unitLabel = new JLabel("学习计划");
		unitLabel.setVisible(false);
		sectionInfo.add(unitLabel);
		final JComboBox unit = new JComboBox();
		unit.setVisible(false);
		sectionInfo.add(unit);
		final JLabel sectionLabel = new JLabel("小节");
		sectionLabel.setVisible(false);
		sectionInfo.add(sectionLabel);
		final JComboBox section = new JComboBox();
		section.setVisible(false);
		sectionInfo.add(section);

		final JButton auto = new JButton("自动挂机");
		auto.setVisible(false);
		sectionInfo.add(auto);
		final ActionListener loginListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Modified in 1.1 使用线程防阻塞
				new Thread() {
					public void run() {
						if (newUser) {
							if (!checkLGInstalled())// Added in 1.1
								return;
							if (!updateChecked)
								checkLGUpdate();
						}
						if (!logged) {
							Document homePage = null;
							host = serverIP.getText();
							if (host.length() <= 0) {
								output.warn("请填写服务器地址！");
								serverIP.requestFocus();
								return;
							}
							if (!host.startsWith("http://") && !host.startsWith("https://"))
								host = "http://" + host;
							username = userName.getText();
							password = new String(pass.getPassword());
							if (username.length() <= 0) {
								output.warn("请填写用户名！");
								userName.requestFocus();
								return;
							}
							if (password.length() <= 0) {
								output.warn("请填写密码！");
								pass.requestFocus();
								return;
							}
							// recorder.writeUserInfo(host, username);
							try {
								// --------Modified in 2.1------
								// output.append("正在尝试以" + username + "登陆...");
								output.append("正在尝试以" + username + "登陆...", true);
								// -----------------------------
								bm.repaint();
								if ((homePage = login(username, password)) != null) {
									itemReady = false;
									unit.removeAllItems();

									unit.getModel().setSelectedItem(null);
									unitLabel.setVisible(true);
									unit.setVisible(true);
									itemReady = true;
									sectionLabel.setVisible(false);
									section.setVisible(false);
									auto.setVisible(true);
									server.setVisible(false);
									serverIP.setVisible(false);
									// user.setText(cls.schoolName + "-" +
									// cls.stuGrade + "-" + cls.stuName);
									// user.setVisible(false);
									userName.setVisible(false);
									passLabel.setVisible(false);
									pass.setVisible(false);
									// sectionInfo.setVisible(false);
									classLabel.setVisible(false);
									classNum.setVisible(false);
									login.setText("注销");
									login.setIcon(getIconRes("logout.png"));
									// sectionInfo.setVisible(true);
									logged = true;
									// ((UserInfoPanel)
									// userInfoPanel).update(false);
									// start timer
									checkLG = true;
									// if (!checkVersionTimer.isRunning())
									// checkVersionTimer.start();
									user.setVisible(true);
									String userInfo = homePage.getElementById("Index_stu_content_left_topInfo")
											.getElementsByTag("div").get(2).text();
									String userName = userInfo.substring(userInfo.indexOf("姓名：") + 3,
											userInfo.indexOf("班级："));
									user.setText(userInfo);
									speak(userName + "，" + TimeTool.getTimePeriod() + "好！");
									Document leftPage = Jsoup.parse(client.get(host
											+ "/StudyPlan/Student/FinishPlan_LeftTree.aspx"));
									// List<Cookie> clist = client.getCookies();
									// for(Cookie c : clist){
									// System.out.println(c);
									// System.out.println(c.getDomain()+"\t"+
									// c.getName()+"="+c.getValue());
									// JWebBrowser.setCookie(c.getDomain(),
									// c.getName()+"="+c.getValue());
									// }
									// previewURL(host+"/StudyPlan/Student/FinishPlan_LeftTree.aspx");
									// debugOutput(leftPage.toString());
									Elements weeks = leftPage.getElementById("TreeView1n1Nodes").getElementsByTag(
											"table");

									for (Element week : weeks) { // Bug fixed in
																	// 2.5r1
										try {
											Element info = week.getElementsByTag("td").get(4);
											// System.out.println(info);
											String url = info.getElementsByTag("a").first().attr("href");
											if (!url.contains("window.location.replace"))
												continue;
											url = url.split("\'")[1];
											plans.add(new StudyPlan(info.text(), host + "/StudyPlan/Student/" + url));
										} catch (Exception e) {
										}
									}
									int planNum = plans.size();
									itemReady = false;
									// for(int i = 0 ; i < planNum; i ++){
									// for(int j = 0 ; j < i - 1; j++){
									// StudyPlan p1 =plans.get(j);
									// StudyPlan p2 =plans.get(j+1);
									// if(p1.compareTo(p2)>1){
									// plans.set(j, p2);
									// plans.set(j+1, p1);
									// }
									// }
									// }
									for (int i = 0; i < planNum / 2; i++) {
										StudyPlan temp = plans.get(i);
										plans.set(i, plans.get(planNum - i - 1));
										plans.set(planNum - i - 1, temp);
									}
									for (int i = 0; i < planNum; i++) {
										unit.addItem(plans.get(planNum - i - 1).name);
									}

									List<Cookie> cs = client.getCookies();
									for (Cookie c : cs) {
										JWebBrowser.setCookie(host,
												c.getName() + "=" + c.getValue() + "; path=" + c.getPath()
														+ "; domain=" + c.getDomain());
									}

									classNum.getModel().setSelectedItem(null);
									// classLabel.setVisible(true);
									// classNum.setVisible(true);
									itemReady = true;
									// classNum.setPopupVisible(true);
									// enter.setVisible(true);

									unitLabel.setVisible(true);
									sectionLabel.setVisible(false);
									section.setVisible(false);
									auto.setVisible(false);
									sectionInfo.setVisible(true);
									unit.setSelectedItem(null);
									// if (classes.size() == 1) {
									// classNum.setSelectedIndex(0);
									// enterClassRoomAction.start();
									// } else {
									// // ------Modified in 2.2.1-------
									// // output.info("请选择班级！");
									// if (newUser)
									// JOptionPane.showMessageDialog(nh,
									// "<html>请在右上方选择班级！<br><br>(本提示仅在初次使用时出现)<html>",
									// "提示", JOptionPane.INFORMATION_MESSAGE);
									// // ----------------------------
									// }
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						} else {
							try {
								// setSubmitted(false);
								// BlueMonkey.client.get(host +
								// "/login/logout.php",
								// "GB2312");
								logoff();
								// cleanTable();
								// preview(null);
								logged = false;
								server.setVisible(true);
								serverIP.setVisible(true);
								user.setVisible(true);
								userName.setVisible(true);
								pass.setVisible(true);
								passLabel.setVisible(true);
								classLabel.setVisible(false);
								classNum.setVisible(false);
								unitLabel.setVisible(false);
								unit.setVisible(false);
								sectionLabel.setVisible(false);
								section.setVisible(false);
								auto.setVisible(false);
								sectionInfo.setVisible(false);
								userName.requestFocus();
								user.setText("用户名");
								login.setText("登陆");
								login.setIcon(getIconRes("login.png"));
								output.append("注销成功！");
								// 停止自动刷新用户信息
								checkLG = false;
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}

					;
				}.start();

			}

		};
		login.addActionListener(loginListener);
		pass.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					// loginListener.actionPerformed(new
					// ActionEvent(e.getSource(), e.getID(), ""));
					login.doClick();
			}
		});
		login.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				// loginListener.actionPerformed(new ActionEvent(e.getSource(),
				// e.getID(), ""));
				login.doClick();
			}
		});

		enterClassRoomAction = new Thread() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Thread() {
					@Override
					public void run() {
						try {
							int chosen = classNum.getSelectedIndex();
							if (chosen >= 0) {

								itemReady = false;
								unit.removeAllItems();
								for (int i = 1; i <= 10; i++)
									unit.addItem(i);
								unit.getModel().setSelectedItem(null);
								unitLabel.setVisible(true);
								unit.setVisible(true);
								itemReady = true;
								sectionLabel.setVisible(false);
								section.setVisible(false);
								auto.setVisible(true);
								server.setVisible(false);
								serverIP.setVisible(false);
								// user.setText(cls.schoolName + "-" +
								// cls.stuGrade + "-" + cls.stuName);
								// user.setVisible(false);
								userName.setVisible(false);
								passLabel.setVisible(false);
								pass.setVisible(false);
								classLabel.setVisible(false);
								classNum.setVisible(false);
								login.setText("注销");
								login.setIcon(getIconRes("logout.png"));
								sectionInfo.setVisible(true);
								logged = true;
								// ((UserInfoPanel)
								// userInfoPanel).update(false);
								// start timer
								// lgMoniterTimer.start();
								// if (!checkVersionTimer.isRunning())
								// checkVersionTimer.start();
								// if (newUser)
								// JOptionPane.showMessageDialog(nh,
								// "<html>请在上方选择单元和小节或启动自动挂机<br><br>(本提示仅在初次使用时出现)<html>",
								// "提示",
								// JOptionPane.INFORMATION_MESSAGE);// Added
								// // in
								// // 2.2.1
							}
						} catch (Exception e1) {
							output.error(e1);
						}

					}
				});
			}
		};
		classNum.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!itemReady || e.getStateChange() == ItemEvent.DESELECTED)
					return;
				enterClassRoomAction.start();
			}
		});

		unit.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!itemReady || e.getStateChange() == ItemEvent.DESELECTED)
					return;
				try {
					StudyPlan plan = plans.get(plans.size() - 1 - unit.getSelectedIndex());
					// ArrayList<Section> sections = plan.getSections();
					// itemReady = false;
					// section.removeAllItems();
					// for (Section s : sections) {
					// section.addItem(s.name + "(" + s.studyRecord + ")");
					// }
					// section.getModel().setSelectedItem(null);
					// sectionLabel.setVisible(true);
					// section.setVisible(true);
					output.append("正在进入" + plan.name + "...");
					previewURL(plan.link);
					// plan.refresh();//deleted in 2.5-1
					itemReady = true;
					if (plan.name.contains("互评作业")) {
						output.info("本计划包含互评作业，但我暂时还不支持这个，请别忘了自行完成相应的内容！");
					}
				} catch (Exception e1) {
					output.error(e1);
				}
			}
		});

		section.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!itemReady || e.getStateChange() == ItemEvent.DESELECTED)
					return;
				int i = section.getSelectedIndex();
				if (i >= 0) {
					try {
						StudyPlan plan = plans.get(unit.getItemCount() - 1 - unit.getSelectedIndex());

						plan.sections.get(i).enter();

					} catch (Exception e1) {
						output.error(e1);
					}
				}
			}
		});

		autoBtnActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!autoBrowse) {
					// NOTODO show and confirm work mode (Need Test!)
					int reply = JOptionPane.showConfirmDialog(bm, "当前挂机设置:\n" + config.getAutoWorkInfo()
							+ "\n\n(如需修改请取消并点击设置按钮)", "请确认挂机设置", JOptionPane.OK_CANCEL_OPTION);
					if (reply != JOptionPane.OK_OPTION)
						return;

					autoBrowse = true;
					try {
						// AutoTask task = null;
						ConfigInfo info = config.getInfo();
						switch (info.autoMode) {
						case 1:
							// task = new AutoTask();
							break;
						case 2:
							// task = new AutoTask(unit.getSelectedIndex());
							break;
						case 3:
							// task = new AutoTask(unit.getSelectedIndex(),
							// section.getSelectedIndex());
							break;
						}
						// task.delay = info.minTimeGap * 1000;
						// task.randomDelay = (info.maxTimeGap -
						// info.minTimeGap) * 1000;
						// task.auto_post = info.autoAns;
						// task.recycle = info.loop;
						// new Thread(task).start();

						auto.setText("停止挂机");
						login.setEnabled(false);
						userName.setEnabled(false);
						pass.setEnabled(false);
						classNum.setEnabled(false);
						unit.setEnabled(false);
						section.setEnabled(false);
						if (info.autoHide) {
							trayTool.hideToTray();
						}
					} catch (Exception e1) {
						autoBrowse = false;
						output.error("建立工作线程失败！");
						output.error(e1);
					}
				} else {
					autoBrowse = false;
					auto.setText("开始挂机");
					login.setEnabled(true);
					userName.setEnabled(true);
					pass.setEnabled(true);
					classNum.setEnabled(true);
					unit.setEnabled(true);
					section.setEnabled(true);
				}
			}
		};
		auto.addActionListener(autoBtnActionListener);

		// fillAns.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent event) {
		//
		// }
		// });
		//
		// submitAns.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent event) {
		//
		// }
		// });
		trayTool = new TrayTool();

		ShortcutManager.getInstance().addShortcutListener(new ShortcutManager.ShortcutListener() {
			public void handle() {
				trayTool.hideToTray();
			}
		}, KeyEvent.VK_F10);

		cleanErrorLogs();
		config = new ConfigDialog();

		// 检查更新，周期10分钟，初始启动延时20秒
		checkVersionTimer = new Timer(600000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (config.getInfo().checkUpdate) {
					VersionPanel panel = (VersionPanel) versionPanel;
					panel.checkUpdate();
				}
			}
		});
		checkVersionTimer.setRepeats(true);
		checkVersionTimer.setInitialDelay(20000);

		// 监控蓝鸽进程
		lgMoniterTimer = new Thread() {
			@Override
			public void run() {
				try {
					while (!checkLGStop) {
						if (checkLG && config.getInfo().autoRefresh) {
							Process process = Runtime.getRuntime().exec("cmd.exe /c tasklist");
							BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String line = "";
							boolean temp = false;
							while ((line = input.readLine()) != null) {
								if (line.startsWith("ZH.exe")) {
									temp = true;
									break;
								}
							}
							if (!lgSystemOn && temp)
								startLGScript();
							lgSystemOn = temp;
							input.close();
						}
						Thread.sleep(1000);
					}
				} catch (Exception e1) {
					output.error(e1);
				}
			}
		};
		lgMoniterTimer.start();
		checkVersionTimer.start();// 立即启动更新检查
		addWindowListener(windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// if(!checkReallyExit()){
				// return;
				// }
				// logoff();
				super.windowClosing(e);
				// 执行清理
				working = false;
				checkLG = false;
				checkLGStop = true;
				autoBrowse = false;
				File lockFile = new File(Recorder.TEMP_DIR + "lock");
				if (lockFile.exists())
					lockFile.delete();
				if (checkVersionTimer != null && checkVersionTimer.isRunning())
					checkVersionTimer.stop();
				speak("Good bye!", false);
				Main.commonToolClose();
				bm.setVisible(false);
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
		bm.enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		// TODO test
		// int reply = JOptionPane.showConfirmDialog(nh, "当前自动填写设置：\n\n" +
		// config.getFillAnsInfo()
		// + "\n\n(如需修改请取消并点击设置按钮,本窗口在按下确认后不再出现！)", "请确认自动填写设置",
		// JOptionPane.OK_CANCEL_OPTION);
		//
		// if (reply < 0 || reply == JOptionPane.CANCEL_OPTION) {
		// System.out.println("canceled!");
		// } else {
		// System.out.println("start!");
		// }

		// serverIP.requestFocus();
		try {
			UserInfo info;
			if ((info = recorder.readUserInfo()) != null) {
				serverIP.setText(info.host);
				userName.setText(info.userID);
				newUser = false;
			}
		} catch (Throwable e1) {
			output.append("尝试解析用户记录失败");
		}

		try {
			if (!new File(Recorder.TEMP_DIR + LGSCRIPT_NAME).exists())
				AttachTool.extractAttach(LGSCRIPT_NAME, Recorder.TEMP_DIR);
			new File(Recorder.TEMP_DIR + "lock").createNewFile();
		} catch (Exception e1) {
			output.error(e1);
		}

		setVisible(true); // Moved here in 1.2-1
		splitPane.setDividerLocation(0.76); // Moved here in 1.2-1
		if (!newUser)
			pass.requestFocus();
//		else
//			((VersionPanel)versionPanel).registerNewUser();
		output.append("启动成功！");
		speak("This is lange monkey " + VersionPanel.VERSION + " speaking", false);
		// new Thread() {
		// public void run() {
		// try {
		// Thread.sleep(10000);
		// speak("I can only say few words now, but new versions will say something more!");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// };
		// }.start();
		Main.hideMain();
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING){
			setVisible(false);
			if( !checkSafelyExit()){
				setVisible(true);
				return; // 直接返回，阻止默认动作，阻止窗口关闭
			}
			super.processWindowEvent(e); // 该语句会执行窗口事件的默认动作(如：隐藏)
		}else
			super.processWindowEvent(e); // 该语句会执行窗口事件的默认动作(如：隐藏)
	}

	public static void speak(String text) {
		speak(text, true);
	}

	public static void speak(String enText, boolean translate) {
		String text = null;
		if (translate) {
			text = LocalMessages.getString(enText);
			if (text == null) {
				try {
					text = TranslateTool.translate(enText);
				} catch (Exception e) {
					text = null;
					output.error(e);
				}
			}
		} else
			text = enText;
		if (text != null)
			MSCTool.getInstance().TTFPlay(text, 3);
	}

	public boolean checkTaskRunning(String taskName) {
		boolean running = false;
		try {
			Process process = Runtime.getRuntime().exec("cmd.exe /c tasklist");
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = input.readLine()) != null) {
				if (line.startsWith(taskName)) {
					running = true;
					break;
				}
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return running;
	}

	private boolean checkSafelyExit() {
		//int reply = -1;
		// int max = 3;
		// for (int count = 0;count < max && reply != JOptionPane.YES_OPTION ;
		// count++) {
		if (!checkTaskRunning("ZH.exe"))
			return true;// break;
		int reply = JOptionPane.showConfirmDialog(bm, "当前正在挂机，如果先退出本程序将丢失正在挂的机时。\n所以强烈建议先关闭蓝鸽窗口以登记当前机时！\n\n是否仍要强行退出？",// 选择\"是\"强行退出;选择\"否\"重新检查.
				"注意", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		return (reply == JOptionPane.YES_OPTION);
		// }
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

	public static void startLGScript() {
		try {
			queryingScript = true;
			new Thread() {
				public void run() {
					while (queryingScript) {
						bm.toFront();
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
						}
					}
				};
			}.start();

			int reply = JOptionPane.showConfirmDialog(bm, "<html>是否启动自动挂机脚本？<br><br>此脚本会控制鼠标完成自动挂机，期间如果:"
					+ "<br><br>1.<font color=\'red\'>主动或被动激活非蓝鸽窗口</font>(例如Win+D,Alt+Tab)，"
					+ "脚本会自动暂停;<br><br>2.恢复激活蓝鸽窗口，脚本会自动继续;<br><br>3.蓝鸽窗口或本程序窗口关闭时,"
					+ "脚本将自动退出;<br><br>4.暂停时间超过4分钟时，主板会发出警告声。<br><br>5.暂停时间超过4分钟15秒时，脚本会尝试强制恢复蓝鸽窗口。<br><br>"+
					"<font color='blue'>特别提示：每个单元的第一节如果挂15分钟，进度大概增长10%，<br>那么后面每节只需挂20秒以上同样可以增长10%！</font></html>", "询问", JOptionPane.YES_NO_OPTION);
			queryingScript = false;
			if (reply == JOptionPane.YES_OPTION) {
				Runtime.getRuntime().exec(Recorder.TEMP_DIR + LGSCRIPT_NAME);
				output.append("挂机脚本已启动！");
			}
		} catch (Exception e) {
			output.error(e);
		}
	}

	public static ImageIcon getIconRes(String fileName) {
		return getIconRes(fileName, 20);
	}

	public static void checkLGUpdate() {
		int reply = JOptionPane.showConfirmDialog(bm,
				"<html>是否需要检测蓝鸽软件的更新？<br>建议<font color=\'red\'>每学期初</font>检查一次！</html>", "询问",
				JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			previewURL("LgWebSetup://http://202.113.3.108/lgsoft/@@JC01");
			preview(null);
			output.append("正在检查蓝鸽软件更新...");
		}
		updateChecked = true;
	}
	
	public static void confirmBug(String name, String debugInfo, String ps){
		String str = "程序出BUG了么？\n\n";
		if(ps != null && ps.length() > 0){
			str += ps+"\n\n";
		}
		str += "如果确定是BUG，是否希望立即将此BUG提交至作者？\n(相关调试信息会自动附加在报告中)";
		int reply = JOptionPane.showConfirmDialog(bm, str,"这是BUG么？",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,getIconRes("bug.png", 64));
		if(reply == JOptionPane.YES_OPTION){
			((VersionPanel)versionPanel).addReport(name,debugInfo);
			VersionPanel.report.setVisible(true);
		}
	}

	// -----------Added in 1.1---------
	public static boolean checkLGInstalled() {
		if (WindowsRegistryTool.readRegistry("HKCR\\LgWebP", "URL Protocol") == null) {
			output.error("你还未安装蓝鸽软件，请到指定站点下载并安装！");
			return false;
		} else
			return true;
	}

	// ----------------------------------
	public static ImageIcon getIconRes(String fileName, int iconSize) {
		ImageIcon icon = null;
		URL url = bm.getClass().getResource("res/" + fileName);
		if (url != null) {
			icon = new ImageIcon(url);
			if (iconSize > 0)
				icon.setImage(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
			return icon;
		}
		// else {
		// try {
		// File file = File.createTempFile("icon", "." + "txt");
		// FileSystemView view = FileSystemView.getFileSystemView();
		// icon = (ImageIcon) view.getSystemIcon(file);
		// // ShellFolder shellFolder = ShellFolder.getShellFolder(file);
		// // icon = new ImageIcon(shellFolder.getIcon(true));
		// //icon.setImage(icon.getImage().getScaledInstance(iconSize, iconSize,
		// Image.SCALE_SMOOTH));
		// file.delete();
		// return icon;
		// } catch (IOException ioe) {
		// ioe.printStackTrace();
		// }
		// }
		return icon;
	}

	public static void main(String[] args) {
		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new BlueMonkey();
			}
		});
		NativeInterface.runEventPump();
	}

	public static void logoff() {
		try {
			preview(null);
			client.clearInput();
			Document indexpage = Jsoup.parse(client.get(host + "/Index.aspx"));
			Elements inputs = indexpage.getElementsByTag("form").first().getElementsByTag("input");
			InetAddress address = InetAddress.getLocalHost();
			for (Element in : inputs) {
				if (in.attr("name").equalsIgnoreCase("__EVENTTARGET"))
					client.addInput("__EVENTTARGET", "linkButton");
				else if (in.attr("name").equalsIgnoreCase("ClientIP"))
					client.addInput("ClientIP", address.getHostAddress());
				else if (in.attr("name").equalsIgnoreCase("ComputerName"))
					client.addInput("ComputerName", address.getHostName());
				else if (in.attr("name").equalsIgnoreCase("TextBox_XhGh"))
					client.addInput("TextBox_XhGh", "11223344");
				else if (in.attr("name").equalsIgnoreCase("TextBox_XhGh_Pwd"))
					client.addInput("TextBox_XhGh_Pwd", "00000000");
				else if (in.attr("name").length() > 0)
					client.addInput(in.attr("name"), in.attr("value"));
			}
			// debugOutput(client.getInputs().toString());
			client.post(host + "/Index.aspx");
		} catch (Exception e) {
		}
	}

	public static Document login(String username, String password) throws Exception {

		try {
			client.clearInput();
			Document indexpage = Jsoup.parse(client.get(host + "/Index.aspx"));
			// debugOutput(indexpage.toString());
			Elements inputs = indexpage.getElementsByTag("form").first().getElementsByTag("input");
			InetAddress address = InetAddress.getLocalHost();
			for (Element in : inputs) {
				if (in.attr("name").equalsIgnoreCase("__EVENTTARGET"))
					client.addInput("__EVENTTARGET", "linkButton");
				else if (in.attr("name").equalsIgnoreCase("ClientIP"))
					client.addInput("ClientIP", address.getHostAddress());
				else if (in.attr("name").equalsIgnoreCase("ComputerName"))
					client.addInput("ComputerName", address.getHostName());
				else if (in.attr("name").equalsIgnoreCase("TextBox_XhGh"))
					client.addInput("TextBox_XhGh", username);
				else if (in.attr("name").equalsIgnoreCase("TextBox_XhGh_Pwd"))
					client.addInput("TextBox_XhGh_Pwd", password);
				else if (in.attr("name").length() > 0)
					client.addInput(in.attr("name"), in.attr("value"));
			}
			// debugOutput(client.getInputs().toString());
			client.post(host + "/Index.aspx");
			String indexHtml = client.get(host + "/IndexStu.aspx");
			// debugOutput(indexHtml);
			homePageDoc = Jsoup.parse(indexHtml);
			if (homePageDoc.getElementById("index_top_stu_cl_06_text_left").text().trim().startsWith("欢迎")) {
				output.append("登录成功！");
				recorder.writeUserInfo(host, username);
			} else {
				homePageDoc = null;
				output.error("登录失败！请检查密码是否正确或者网络连接是否有效！");
			}
			// nh.repaint();//need this?
			return homePageDoc;
		} catch (Exception e) {
			output.error("登录失败！请检查密码是否正确或者网络连接是否有效！");
		}
		return null;
	}

	public static void preview(final String html) {
		try {
			SwingUtilities.invokeLater(new Thread() {
				@Override
				public void run() {
					if (html == null) {
						cardLayout.show(previewPanel, "text");
					} else {
						browser.setHTMLContent(html);
						cardLayout.show(previewPanel, "browser");
					}
				}
			});
		} catch (Exception e) {
			output.error(e);
		}
	}

	public static void previewURL(final String url) {
		try {
			SwingUtilities.invokeLater(new Thread() {
				@Override
				public void run() {
					if (url == null) {
						cardLayout.show(previewPanel, "text");
					} else {
						browser.navigate(url);
						cardLayout.show(previewPanel, "browser");
					}
				}
			});
		} catch (Exception e) {
			output.error(e);
		}
	}

	// ------Added in 2.1-------
	public static void debugOutput(String output) {
		JFrame frame = new JFrame();
		JTextArea area = new JTextArea(output);
		JScrollPane pane = new JScrollPane(area);
		frame.add(pane);
		frame.pack();
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	// ----------------------------

	public static class UneditableTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public static class LogArea extends JTextArea {
		private static final long serialVersionUID = -4837188851065576013L;

		/**
		 * 自动换行，加上时间戳，并且自动滚动日志到最后一行。
		 */
		@Override
		public void append(String str) {
			super.append("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()) + "]  ");
			// final int LINE_LIMIT = 46;
			// final String SPACE = "\t                     ";
			// if (str.length() <= LINE_LIMIT)
			// super.append(str + "\n");
			// else {
			// //System.out.println("long text");
			// super.append(str.substring(0, LINE_LIMIT)+"\n");
			// int line = str.length() / LINE_LIMIT + 1;
			// int i = 1;
			// for (; i < line - 1; i++) {
			// super.append(SPACE);
			// super.append(str.substring(i * LINE_LIMIT, (i + 1) *
			// LINE_LIMIT)+"\n");
			// }
			// super.append(SPACE);
			// super.append(str.substring(i * LINE_LIMIT) + "\n");
			// }
			super.append(str + "\n");
			output.setCaretPosition(output.getText().length());
			// -----------Deleted in 2.1-----------
			// if (inTray) {
			// trayTool.showInfo(str);
			// }
			// -------------------------------------
		}

		// -----------Added in 2.1-----------
		public void append(String str, boolean showInTray) {
			append(str);
			if (showInTray && inTray) {
				trayTool.showInfo(str);
			}
		}

		// -------------------------------------

		public void info(String str) {
			this.append(str);
			speak(str);
			JOptionPane.showMessageDialog(bm, str, "信息", JOptionPane.INFORMATION_MESSAGE);
		}

		public void warn(String str) {
			this.append(str);
			speak("警告: " + str);
			JOptionPane.showMessageDialog(bm, str, "警告", JOptionPane.WARNING_MESSAGE);
		}

		public void error(String str) {
			this.append(str);
			speak("错误: " + str);
			JOptionPane.showMessageDialog(bm, str, "错误", JOptionPane.ERROR_MESSAGE);
		}

		public void error(Throwable e) {
			append("程序出现错误:" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	static class StudyPlan implements Comparable<StudyPlan> {
		String link;
		String name;
		String timeStart, timeEnd;
		double finishRate;
		int finishScore;
		private ArrayList<Section> sections = new ArrayList<Section>();
		Document doc;

		public StudyPlan() {
		}

		public StudyPlan(String name, String link) {
			this.name = name;
			this.link = link;
		}

		public void refresh() {
			new Thread() {
				public void run() {
					try {
						finishRate = -1.0;
						Thread.sleep(3000);
						SwingUtilities.invokeAndWait(new Thread() {
							@Override
							public void run() {
								while (finishRate < 0) {
									Thread.yield();
									doc = Jsoup.parse(browser.getHTMLContent());
									Element e = doc.getElementById("tdJD");
									if (e == null)
										continue;
									String rate = null;
									rate = e.text().trim();
									finishRate = Double.parseDouble(rate.substring(0, rate.indexOf("%")));
								}
							}
						});
						while (finishRate < 0) {
							Thread.sleep(500);
						}

						Elements message = doc.getElementById("LBMessage").getElementsByTag("tr").get(1)
								.getElementsByTag("td").get(3).getElementsByTag("font");
						// System.out.println(message);
						timeStart = message.get(0).text().trim();
						timeEnd = message.get(1).text().trim();
						finishScore = Integer.parseInt(message.get(2).text().trim());
						System.out.println(timeStart + "~" + timeEnd);
						cal1.setTime(new Date());
						cal2.setTime(new SimpleDateFormat("yy-MM-dd").parse(timeStart));
						if (cal1.before(cal2))
							output.warn("本计划还未开始！");
						else {
							cal2.setTime(new SimpleDateFormat("yy-MM-dd").parse(timeEnd));
							if (cal1.after(cal2))
								output.warn("本计划已经结束！");
						}

						// Elements es =
						// doc.getElementById("TabContainer1_p1_gridview1").getElementsByTag("tr");
						// for (int i = 1; i < es.size(); i++) {
						// Elements info = es.get(i).getElementsByTag("td");
						// Section s = new Section();
						// s.name = info.get(1).text().trim();
						// s.type = info.get(2).text().trim();
						// Elements e = info.get(3).getElementsByTag("span");
						// String str = "";
						// for (Element ie : e) {
						// if
						// (!ie.attr("style").equalsIgnoreCase("display: none;"))
						// str += ie.text().trim();
						// }
						// s.studyRecord = str;
						// String js =
						// info.get(4).getElementsByTag("a").first().attr("onclick");
						// js = js.substring(js.indexOf("lgwebp://"));
						// s.link = js.substring(0, js.indexOf('\''));
						// sections.add(s);
						// }
					} catch (Exception e) {
						e.printStackTrace();
					}

				};
			}.start();

		}

		@Override
		public int compareTo(StudyPlan o) {
			return this.link.compareTo(o.link);
		}

		class Section {
			String name, type;
			String studyRecord, link;

			public void enter() {
				previewURL(link);
			}

		}
	}
}
