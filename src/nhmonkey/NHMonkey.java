package nhmonkey;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import nhmonkey.NHMonkey.Classroom.Section;
import nhmonkey.Recorder.ConfigInfo;
import nhmonkey.Recorder.Form;
import nhmonkey.Recorder.Form2;
import nhmonkey.Recorder.FormPart;
import nhmonkey.Recorder.Input;
import nhmonkey.Recorder.UserInfo;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tool.MSCTool;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserListener;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;

import common.Main;
import common.TimeTool;

public class NHMonkey extends JFrame {
	private static final long serialVersionUID = 2498048932285608680L;
	static NHMonkey nm;
	static JTextArea ansDetail;
	static LogArea output;
	static DefaultTableModel ansTableModel;
	static JPanel contentPanel, previewPanel, allInfoPanel, logPanel, userInfoPanel, toolPanel, versionPanel;
	static JTabbedPane infoPanel;
	static ConfigDialog config;
	static String username, password;
	static boolean logged = false;
	static boolean itemReady = false;
	static boolean autoBrowse = false;
	static boolean inTray = false;
	static boolean firstFillAns = true;
	static Thread autoThread;
	static Browser browser;
	static JLabel browserText;
	static JTable ansTable;
	static JButton readAns, writeAns, fillAns, submitAns;
	static ImageIcon submitIcon, submitOKIcon;
	static ActionListener autoBtnActionListener;
	static CardLayout cardLayout;
	static Color bgColor = new Color(214, 217, 223);
	static Client client;
	static String host;
	static ArrayList<Room> rooms;
	static Classroom cls;
	static Section[] sections;
	static Section currentSection;
	final static String NOPAGE = "没有需要显示的页面";
	static Recorder recorder;
	static TrayTool trayTool;
	static Timer checkVersionTimer, userInfoTimer;
	static ActionListener enterClassRoomAction;
	static boolean newUser = true;// Added in 2.2.1
	static boolean working = true;// Added in 2.4
	static WebBrowserListener webLis = null;// Added in 2.5c
	static boolean quizMode = false;// Added in 2.5c
	static Quiz currentQuiz = null;// Added in 2.5c
	static boolean quizWorking = false;
	static ActionListener loginListener;
	WindowAdapter windowAdapter;

	// static boolean submitting;

	public NHMonkey() {
		super(VersionPanel.SUB_NAME + " V" + VersionPanel.VERSION);
		Main.registerMonkey();
		nm = this;
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
		infoPanel.addTab("用户信息", getIconRes("user.png"), userInfoPanel = new UserInfoPanel());
		infoPanel.addTab("辅助工具", getIconRes("tools.png"), toolPanel = new ToolPanel());
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
		ansTable = new JTable() {
			private static final long serialVersionUID = -3261090591766326664L;

			@Override
			public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
				super.changeSelection(rowIndex, columnIndex, toggle, extend);
				// String str = ansTable.getModel().getValueAt(rowIndex,
				// 1).toString();
				// ansDetail.setText(str+"\n"+Translater.translate(str));
				String answer = ansTable.getModel().getValueAt(rowIndex, 1).toString();
				ansDetail.setText(answer);
				ansDetail.setCaretPosition(0);
				if (answer.length() > 1 && !answer.equals("NG"))
					ToolPanel.lookUp(answer);
			}
		};

		ansTableModel = new UneditableTableModel();
		ansTable.setModel(ansTableModel);
		ansTableModel.addColumn("序号");
		ansTableModel.addColumn("参考答案");
		ansTable.getColumnModel().getColumn(0).setPreferredWidth(35);
		ansTable.getColumnModel().getColumn(1).setPreferredWidth(200);

		// JTableHeader header = new JTableHeader();
		// header.add(new JLabel("序号"));
		// header.add(new JLabel("答案"));
		// answerTable.setTableHeader(header);

		// answerTable.setLayout(new GridLayout(0, 2));
		JPanel rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(250, 300));
		rightPanel.setLayout(new BorderLayout());
		JPanel ansPanel = new JPanel();
		ansPanel.setMinimumSize(new Dimension(50, 210));
		ansPanel.setLayout(new BorderLayout());
		ansPanel.add(new JScrollPane(ansTable));
		rightPanel.add(ansPanel);
		ansDetail = new JTextArea();
		ansDetail.setBackground(bgColor);
		ansDetail.setLineWrap(true);
		ansDetail.setWrapStyleWord(true);
		ansDetail.setEditable(false);
		JScrollPane ansDetailScroll = new JScrollPane(ansDetail);
		ansDetailScroll.setBorder(new TitledBorder("详细信息"));
		ansDetailScroll.setPreferredSize(new Dimension(20, 120));
		ansPanel.add(ansDetailScroll, BorderLayout.SOUTH);
		submitIcon = getIconRes("submit.png");
		submitOKIcon = getIconRes("submit_ok.png");
		readAns = new JButton("读取答案", getIconRes("import.png"));
		writeAns = new JButton("导出答案", getIconRes("export.png"));
		fillAns = new JButton("填入答案", getIconRes("fill_form.png"));
		submitAns = new JButton("提交答案", submitIcon);
		// submitAns.setEnabled(false);
		JPanel rightBottomPanel = new JPanel();
		rightBottomPanel.setLayout(new GridLayout(2, 2));
		rightBottomPanel.add(readAns);
		rightBottomPanel.add(writeAns);
		rightBottomPanel.add(fillAns);
		rightBottomPanel.add(submitAns);
		rightPanel.add(rightBottomPanel, BorderLayout.SOUTH);
		contentPanel.add(rightPanel, BorderLayout.EAST);
		JPanel topBigPanel = new JPanel();
		topBigPanel.add(topPanel);
		JButton configBtn = new JButton("设置", getIconRes("config.png", 32));
		configBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				config.showDialog();
			}
		});
		topBigPanel.add(configBtn);
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
		final JLabel unitLabel = new JLabel("单元");
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
		loginListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Modified in 2.3 使用多线程防阻塞
				new Thread() {
					public void run() {
						try {
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

								try {
									// --------Modified in 2.1------
									// output.append("正在尝试以" + username +
									// "登陆...");
									output.append("正在尝试以" + username + "登陆...", true);
									// -----------------------------
									nm.repaint();
									if ((homePage = login(username, password)) != null) {
										rooms = getRooms(homePage);
										if (rooms == null || rooms.size() <= 0) {
											return;
										}
										// JOptionPane.showMessageDialog(null,
										// classes.size());
										itemReady = false;
										classNum.removeAllItems();

										for (Room c : rooms) {
											classNum.addItem(c.getLabel());
										}
										classNum.getModel().setSelectedItem(null);
										classLabel.setVisible(true);
										classNum.setVisible(true);
										itemReady = true;
										// classNum.setPopupVisible(true);
										// enter.setVisible(true);

										unitLabel.setVisible(false);
										unit.setVisible(false);
										sectionLabel.setVisible(false);
										section.setVisible(false);
										auto.setVisible(false);

										if (rooms.size() == 1) {
											classNum.setSelectedIndex(0);
											enterClassRoomAction.actionPerformed(new ActionEvent(this, 0,
													"EnterClassRoom"));
										} else {
											// ------Modified in 2.2.1-------
											// output.info("请选择班级！");
											if (newUser)
												JOptionPane.showMessageDialog(nm,
														"<html>请在右上方选择班级！<br><br>(本提示仅在初次使用时出现)<html>", "提示",
														JOptionPane.INFORMATION_MESSAGE);
											// ----------------------------
										}
									}
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							} else {
								try {
									setSubmitted(false);
									NHMonkey.client.get(host + "/login/logout.php", "GB2312");
									cleanTable();
									preview(null);
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
									userInfoTimer.stop();
									quizWorking = false;
									setSubmitted(false);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						} catch (Exception e) {
							output.error(e);
						}
					};
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

		// Modified in 2.3 --- Change Thread to ActionListener for multi-time
		// usage
		enterClassRoomAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Thread() {
					@Override
					public void run() {
						try {
							int chosen = classNum.getSelectedIndex();
							if (chosen >= 0) {
								Room r = rooms.get(chosen);
								if (r instanceof Classroom) {
									quizMode = false;
									cls = (Classroom) r;
									cls.enter();
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
									if (cls.nameDesc == null)
										user.setText(cls.schoolName + "-" + cls.stuGrade + "-" + cls.stuName);
									else
										user.setText(cls.nameDesc);
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
									((UserInfoPanel) userInfoPanel).update(false);
									// start timer
									userInfoTimer.start();
									// if (!checkVersionTimer.isRunning())
									// checkVersionTimer.start();
									if (newUser)
										JOptionPane.showMessageDialog(nm,
												"<html>请在上方选择单元和小节或启动自动挂机<br><br>(本提示仅在初次使用时出现)<html>", "提示",
												JOptionPane.INFORMATION_MESSAGE);// Added
																					// in
																					// 2.2.1
								} else if (r instanceof Quiz) {
									// output.info("对不起，目前我还不能解析Quiz，望高人相助！");
									// if (r instanceof Quiz)
									// return;
									quizMode = true;
									Quiz qr = (Quiz) r;
									if (qr.state == 0) {
										output.warn("测试还未开始！");
										return;
									}
									if (qr.state == 2) {
										output.warn("测试已经结束！");
										return;
									}
									if (qr.state != 1) {
										output.warn("当前无法进入Quiz！");
										return;
									}
									currentQuiz = qr;
									cls = (Classroom) rooms.get(0);// 为了借用Classroom已有的解析用户信息的能力
									unitLabel.setVisible(false);
									unit.setVisible(false);
									sectionLabel.setVisible(false);
									section.setVisible(false);
									auto.setVisible(false);
									server.setVisible(false);
									serverIP.setVisible(false);
									if (cls.nameDesc == null)
										user.setText(cls.schoolName + "-" + cls.stuGrade + "-" + cls.stuName);
									else
										user.setText(cls.nameDesc);
									userName.setVisible(false);
									passLabel.setVisible(false);
									pass.setVisible(false);
									classLabel.setVisible(false);
									classNum.setVisible(false);
									sectionInfo.setVisible(false);
									login.setText("注销");
									login.setIcon(getIconRes("logout.png"));
									logged = true;
									// ((UserInfoPanel)
									// userInfoPanel).update(false);
									// userInfoTimer.start();
									qr.enter();
								}
								if(cls.nameDesc == null)
									speak(cls.stuName + "同学，" + TimeTool.getTimePeriod() + "好！");
//								else
//									speak(cls.nameDesc + "，" + TimeTool.getTimePeriod() + "好！");
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
				enterClassRoomAction.actionPerformed(new ActionEvent(this, 0, "EnterClassRoom"));
			}
		});

		unit.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!itemReady || e.getStateChange() == ItemEvent.DESELECTED)
					return;
				try {
					sections = cls.gotoUnit(unit.getSelectedIndex() + 1);
					itemReady = false;
					section.removeAllItems();
					for (Section s : sections) {
						section.addItem(s.name + "(" + s.part + ")");
					}
					section.getModel().setSelectedItem(null);
					sectionLabel.setVisible(true);
					section.setVisible(true);
					itemReady = true;
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
						sections[i].enter();
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
					int reply = JOptionPane.showConfirmDialog(nm, "当前挂机设置:\n" + config.getAutoWorkInfo()
							+ "\n\n(如需修改请取消并点击设置按钮)", "请确认挂机设置", JOptionPane.OK_CANCEL_OPTION);
					if (reply != JOptionPane.OK_OPTION)
						return;
					// ---------Added in 2.3----------
					ConfigInfo info = config.getInfo();
					if (info.autoAns && firstFillAns) {
						reply = JOptionPane.showConfirmDialog(nm, "当前自动填写设置：\n\n" + config.getFillAnsInfo()
								+ "\n\n(如需修改请取消并点击设置按钮,本窗口在按下确认后不再出现！)", "请确认自动填写设置", JOptionPane.OK_CANCEL_OPTION);
						if (reply != JOptionPane.OK_OPTION)
							return;
						firstFillAns = false;
					}

					if (info.autoMode == 2 && unit.getSelectedIndex() == -1) {
						output.warn("请先进入某一单元！");
						return;
					} else if (info.autoMode == 3 && (unit.getSelectedIndex() == -1 || unit.getSelectedIndex() == -1)) {
						output.warn("请先进入某一单元的某一小节！");
						return;
					}
					// --------------------------------
					autoBrowse = true;
					try {
						AutoTask task = null;
						switch (info.autoMode) {
						case 1:
							task = new AutoTask();
							break;
						case 2:
							task = new AutoTask(unit.getSelectedIndex());
							break;
						case 3:
							task = new AutoTask(unit.getSelectedIndex(), section.getSelectedIndex());
							break;
						}
						task.delay = info.minTimeGap * 1000;
						task.randomDelay = (info.maxTimeGap - info.minTimeGap) * 1000;
						task.auto_post = info.autoAns;
						task.recycle = info.loop;
						new Thread(task).start();

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

		readAns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser file = new JFileChooser(System.getProperty("user.dir"));
					FileNameExtensionFilter filter = new FileNameExtensionFilter("答案记录(*.rec)", "rec");
					file.setFileFilter(filter);
					if (file.showOpenDialog(nm) == JFileChooser.APPROVE_OPTION) {
						recorder.unpack(file.getSelectedFile());
					}
				} catch (Exception e1) {
					output.error(e1);
				}
			}
		});

		writeAns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser file = new JFileChooser(System.getProperty("user.dir"));
					FileNameExtensionFilter filter = new FileNameExtensionFilter("答案记录(*.rec)", "rec");
					file.setFileFilter(filter);
					if (file.showSaveDialog(nm) == JFileChooser.APPROVE_OPTION) {
						File save = file.getSelectedFile();
						if (!save.getName().endsWith(".rec"))
							save = new File(save.getAbsolutePath() + ".rec");
						recorder.pack(save);
					}
				} catch (Exception e1) {
					output.error(e1);
				}
			}
		});

		fillAns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				fillAnsAction();
			}
		});

		submitAns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				submitAnsAction();
			}
		});
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

		// 更新用户信息，周期1分钟，初始启动延时30秒
		userInfoTimer = new Timer(60000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (config.getInfo().autoRefresh) {
					UserInfoPanel panel = (UserInfoPanel) userInfoPanel;
					panel.update(true);
				}
			}
		});
		userInfoTimer.setRepeats(true);
		checkVersionTimer.start();// 立即启动更新检查

		addWindowListener(windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				// 执行清理
				working = false;
				autoBrowse = false;
				if (userInfoTimer != null && userInfoTimer.isRunning())
					userInfoTimer.stop();
				if (checkVersionTimer != null && checkVersionTimer.isRunning())
					checkVersionTimer.stop();
				speak("白白！");
				Main.commonToolClose();
				nm.setVisible(false);
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						Main.cleanTemp();
						Main.unregisterMonkey();
						Main.queryExit();
					};
				}.start();
			}

			// ---------Added in 2.3---------
			@Override
			public void windowIconified(WindowEvent e) {
				try {
					if (autoBrowse && recorder.readConfigInfo().autoHide) {
						trayTool.hideToTray();
					}
				} catch (Exception e1) {
					output.error(e1);
				}
			}
			// -------------------------------
		});

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

		setVisible(true); // Moved here in 2.4-1
		splitPane.setDividerLocation(0.769 - 0.08); // Moved here in 2.4-1
		if (!newUser)
			pass.requestFocus();
		// else
		// ((VersionPanel)versionPanel).registerNewUser();
		output.append("启动成功！");
		String version = VersionPanel.VERSION;
		// int index = version.lastIndexOf('-');
		// if (index > 0) {
		// String mainVersion = version.substring(0, index);
		// String minVersion = version.substring(index + 1);
		// version = mainVersion + " 第" + minVersion;
		// }
		speak("我是NHCE monkey " + version + "版，亲！");
		// new Thread() {
		// public void run() {
		// try {
		// Thread.sleep(10000);
		// speak("我现在只会讲几句话，但以后会升级的啦！");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// };
		// }.start();
		Main.hideMain();
	}

	public static void speak(String cnText) {
		MSCTool.getInstance().TTFPlay(cnText, 1);
	}

	public boolean tryReLogIn(int timeout) {
		if (logged) {// 注销
			loginListener.actionPerformed(new ActionEvent(nm, 0, "Logout"));
			int waitCount = 0;
			while (logged) {
				if (!working)
					return false;
				waitCount++;
				if (waitCount > timeout)
					return false;
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 登陆
		loginListener.actionPerformed(new ActionEvent(nm, 0, "Login"));
		int waitCount = 0;
		while (!logged) {
			if (!working)
				return false;
			waitCount++;
			if (waitCount > timeout)
				return false;
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
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

	public void submitAnsAction() {
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				try {
					if (!browser.isVisible()) {
						output.warn("请先进入答题页面！");
						return;
					}
					if (!quizMode) {
						// submitting = true;
						NHMonkey.client.clearInput();
						System.out.println(browser.getHTMLContent());
						Document doc = Jsoup.parse(browser.getHTMLContent());
						Element form = doc.getElementsByTag("form").first();
						// Elements inputs = doc.getElementsByTag("input");
						// Elements selects = doc.getElementsByTag("select");
						// for (int i = 0; i < inputs.size(); i++) {
						// client.addInput(inputs.get(i).attr("name"),
						// inputs.get(i).attr("value"));
						// }
						// for (int i = 0; i < selects.size(); i++) {
						// client.addInput(selects.get(i).attr("name"),
						// selects.get(i).attr("value"));
						// }

						Elements es = form.getElementsByAttribute("name");
						for (Element e : es) {
							if (e.attr("type").equalsIgnoreCase("hidden")) {// 隐藏信息
								client.addInput(e.attr("name"), e.attr("value"));
							} else if (e.hasClass("fb")) {// 填空
								client.addInput(e.attr("name"), e.attr("value"));
							} else if (e.tagName().equalsIgnoreCase("select")) {// 下拉选择
								Element selected = e.getElementsByAttribute("selected").first();
								client.addInput(e.attr("name"), selected.attr("value"));
							} else if (e.attributes().get("type").equalsIgnoreCase("radio")) {// 一个单选按钮
								if (e.attributes().hasKey("checked"))
									client.addInput(e.attr("name"), e.attr("value"));
							} else if (e.tagName().equalsIgnoreCase("textarea")) {// 文本域
								client.addInput(e.attr("name"), e.html());
							}
						}
						System.out.println("Post:" + currentSection.post);
						System.out.println(client.getInputs().toString().replace(' ', '\n'));
						client.post(currentSection.post, "GB2312");
						checkAns();
					} else {// In Quiz Mode
						// output.info("请在左侧窗口自行提交！");
						if (ansTableModel.getRowCount() > 0) {// has answer book
							if (firstFillAns) {
								int reply = JOptionPane.showConfirmDialog(nm,
										"即将启动测试托管模式！本模式将托管您的Quiz，自动填写和提交测试！\n当前自动填写设置：\n\n" + config.getFillAnsInfo()
												+ "\n\n(如需修改请取消并点击设置按钮,本窗口在按下确认后不再出现！)", "请确认自动填写设置",
										JOptionPane.OK_CANCEL_OPTION);
								if (reply != JOptionPane.OK_OPTION)
									return;
								firstFillAns = false;
							}

							final int totalMin = Integer.parseInt(currentQuiz.timeInMin);
							final int optionCount = totalMin / 5 + 1;
							int[] timeMinOptions = new int[optionCount];
							String[] timeOptions = new String[optionCount];
							for (int i = 0; i < optionCount; i++) {
								double per = i * 1.0 / (optionCount - 1);
								timeMinOptions[i] = (int) (totalMin * per);
								timeOptions[i] = "总时间的" + (int) (per * 100) + "%---" + timeMinOptions[i] + "分钟";
							}

							Object replyObj = JOptionPane.showInputDialog(nm, "请选择测试花费时间，我会根据此时间自动分配答题和提交的时间:",
									"请选择测试时间", JOptionPane.QUESTION_MESSAGE, getIconRes("fill_form.png", 40),
									timeOptions, timeOptions[0]);
							if (replyObj == null)
								return;
							String reply = (String) replyObj;

							for (int i = 0; i < timeOptions.length; i++) {
								if (reply.equals(timeOptions[i])) {
									currentQuiz.oneKeySubmit(timeMinOptions[i]);
									break;
								}
							}

							infoPanel.setSelectedIndex(0);// Added in 2.1
						} else {
							output.append("很抱歉！暂无本题答案！");
							JOptionPane.showMessageDialog(nm, "很抱歉！暂无本题答案！\n但你能在此正常完成答题，在提交后我会自动将系统返回的答案与所有人分享！");
						}
					}
				} catch (Exception e1) {
					output.error(e1);
				}
			}
		});
	}

	public void fillAnsAction() {
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				if (!browser.isVisible()) {
					output.warn("请先进入答题页面！");
					return;
				}

				if (ansTableModel.getRowCount() > 0) {// has answer book
					if (quizMode) {
						output.info("Quiz模式下暂不支持直接页面注入，请直接点击“提交答案”以进入测试托管模式！");
						return;
					}
					if (firstFillAns) {
						int reply = JOptionPane.showConfirmDialog(nm, "当前自动填写设置：\n\n" + config.getFillAnsInfo()
								+ "\n\n(如需修改请取消并点击设置按钮,本窗口在按下确认后不再出现！)", "请确认自动填写设置", JOptionPane.OK_CANCEL_OPTION);
						if (reply != JOptionPane.OK_OPTION)
							return;
						firstFillAns = false;
					}
					Document doc = Jsoup.parse(browser.getHTMLContent());
					// if (!quizMode) {
					// NOTODO test here!!!
					ArrayList<Input> ans = currentSection.getFakeAnswer();
					// ArrayList<Input> ans = currentSection.answer.inputs;
					int i = 0;
					for (; i < ans.size(); i++) {
						Input a = ans.get(i);
						Elements elements = doc.getElementsByAttributeValue("name", a.name);
						Element e = elements.first();
						if (e.attr("type").equalsIgnoreCase("hidden")) {// 隐藏信息
							continue;
						} else if (e.hasClass("fb")) {// 填空
							String[] strs = a.value.split("/");
							int choice = (int) (Math.random() * (strs.length));
							e.attributes().put("value", strs[choice]);
						} else if (e.tagName().equalsIgnoreCase("select")) {// 下拉选择
							Elements es = e.children();
							if (a.value.endsWith("*")) {// 故意出错标记
								for (Element em : es) {
									em.attributes().remove("selected");
								}
								for (int index = 0; index < es.size(); index++) {
									Element em = es.get(index);
									String value = a.value.substring(0, a.value.length() - 1);
									if (em.text().trim().equalsIgnoreCase(value)) {
										es.get((index + (int) (Math.random() * (es.size() - 1) + 1)) % es.size())
												.attributes().put("selected", "");
										break;
									}
								}
							} else {
								for (Element em : es)
									if (em.text().trim().equalsIgnoreCase(a.value))
										em.attributes().put("selected", "");
									else
										em.attributes().remove("selected");
							}
						} else if (e.attributes().get("type").equalsIgnoreCase("radio")) {// 一个单选按钮
							for (Element radio : elements)
								if (radio.attr("value").equalsIgnoreCase(a.value))
									radio.attributes().put("checked", "");
								else
									radio.attributes().remove("checked");
						} else if (e.tagName().equalsIgnoreCase("textarea")) {// 文本域
							e.html(a.value);
						}
					}
					// } else {// In Quiz Mode

					// ArrayList<Input> ans = currentQuiz.answer.inputs;
					// int i = 0;
					// for (; i < ans.size(); i++) {
					// Input a = ans.get(i);
					// Elements elements =
					// doc.getElementsByAttributeValue("name", a.name);
					// Element e = elements.first();
					// if (e == null)
					// continue;
					// if (e.attr("type").equalsIgnoreCase("hidden")) {// 隐藏信息
					// continue;
					// } else if (e.attr("type").equalsIgnoreCase("text")) {//
					// 填空
					// String[] strs = a.value.split("/");
					// int choice = (int) (Math.random() * (strs.length));
					// e.attributes().put("value", strs[choice]);
					// } else if (e.tagName().equalsIgnoreCase("select")) {//
					// 下拉选择
					// Elements es = e.children();
					// if (a.value.endsWith("*")) {// 故意出错标记
					// for (Element em : es) {
					// em.attributes().remove("selected");
					// }
					// for (int index = 0; index < es.size(); index++) {
					// Element em = es.get(index);
					// String value = a.value.substring(0, a.value.length() -
					// 1);
					// if (em.text().trim().equalsIgnoreCase(value)) {
					// es.get((index + (int) (Math.random() * (es.size() - 1) +
					// 1)) % es.size())
					// .attributes().put("selected", "");
					// break;
					// }
					// }
					// } else {
					// for (Element em : es)
					// if (em.text().trim().equalsIgnoreCase(a.value))
					// em.attributes().put("selected", "");
					// else
					// em.attributes().remove("selected");
					// }
					// } else if
					// (e.attributes().get("type").equalsIgnoreCase("radio"))
					// {// 一个单选按钮
					// for (Element radio : elements)
					// if (radio.attr("value").equalsIgnoreCase(a.value))
					// radio.attributes().put("checked", "");
					// else
					// radio.attributes().remove("checked");
					// } else if (e.tagName().equalsIgnoreCase("textarea")) {//
					// 文本域
					// e.html(a.value);
					// }
					// Elements fset = doc.getElementsByTag("frameset")
					// }

					// }
					preview(doc.html());
					infoPanel.setSelectedIndex(0);// Added in 2.1
				} else {
					output.append("很抱歉！暂无本题答案！");
					JOptionPane.showMessageDialog(nm, "很抱歉！暂无本题答案！\n但你能在此正常完成答题，在提交后我会自动将系统返回的答案与所有人分享！");
				}
			}
		});

	}

	public static ImageIcon getIconRes(String fileName) {
		return getIconRes(fileName, 20);
	}

	public static ImageIcon getIconRes(String fileName, int iconSize) {
		ImageIcon icon = null;
		URL url = nm.getClass().getResource("res/" + fileName);
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
				new NHMonkey();
			}
		});
		NativeInterface.runEventPump();
	}

	public static ArrayList<Room> getRooms(Document indexPage) throws Exception {
		Elements temp = indexPage.getElementsByAttributeValue("width", "73%");
		if (temp == null || temp.size() <= 0) {
			output.warn("没有发现目标元素！");
			confirmBug("获取班级失败", "Index Page With No \"Width 73% Element\" Found", indexPage.toString(),
					"这好像是个严重的问题，您的新视野系统是哪个版本的？亲...");
			return null;
		}
		Elements links = temp.first().getElementsByTag("a");
		if (links == null || links.size() <= 0) {
			// -------Modified in 2.1-------
			// output.warn("没有发现可用班级！请检查网络连接并重新登陆！");
			output.warn("没有发现可用班级！请确保已经选课！");
			confirmBug("获取班级失败", "Index Page With No Hyper-Link Found", indexPage.toString(),
					"这好像是个严重的问题，您的新视野系统是哪个版本的？亲...");
			// -----------------------------
			return null;
		}
		String userInfo = indexPage.getElementsByClass("no").first().getElementsByTag("b").first().text();
		// output.append(userInfo);
		ArrayList<Room> rooms = new ArrayList<Room>();
		String quizURL = null;
		for (int i = 0; i < links.size(); i++) {
			Element e = links.get(i);
			// output.append(e.text()+"\n");
			if (e.text().contains("读写")) {
				String classroom = e.parent().parent().parent().getElementsByTag("tr").first().getElementsByTag("td")
						.get(1).text();
				rooms.add(new Classroom(userInfo, classroom, e.attr("href")));
			} else if (e.text().contains("quiz")) {// Added
				// in
				// 2.5
				quizURL = host + e.attr("href");
				ArrayList<Quiz> qs = Quiz.checkQuiz(quizURL);
				for (Quiz q : qs)
					rooms.add(q);
			}
		}
		// if (quizURL != null) {
		// ArrayList<Quiz> qs = Quiz.checkQuiz(quizURL);
		// for (Quiz q : qs)
		// rooms.add(q);
		// }
		// -----Added in 2.1----
		if (rooms.size() <= 0) {
			output.warn("没有发现可用班级！请确保已经选课！");
			confirmBug("获取班级失败", "Index Page With No Class Found", indexPage.toString(),
					"如果您确认已经选过课，并且可以通过网页登陆，那么估计是解析班级信息出错。\n如果真是这样请提交这个Bug，我会根据调试信息更新处理逻辑！");
			return null;
		}
		// ---------------------
		output.append("搜索到" + rooms.size() + "个班级(或Quiz)");
		return rooms;
	}

	public static Document login(String username, String password) throws Exception {
		setSubmitted(false);
		Document doc = null;
		try {
			client.clearInput();
			client.addInput("username", username);
			client.addInput("password", password);
			client.addInput("Input2", "");
			client.get(host + "/index.php");
			client.post(host + "/index.php");
			String indexHtml = client.get(host + "/login/index_student.php");
			doc = Jsoup.parse(indexHtml);
			if (doc.getElementsByTag("head").first().getElementsByTag("title").text()
					.equals("Welcome to New Horizon College English")) {
				output.append("登录成功！");
				recorder.writeUserInfo(host, username);
			} else {
				output.error("登录失败！请检查密码是否正确或者网络连接是否有效！");
				confirmBug("登录失败", "Login Fail Page", doc == null ? "null" : doc.toString(),
						"如果在网页里使用相同登陆地址、帐号和密码，可以登陆，而在重启本程序后仍登陆不了，那就差不多是Bug了。\n"
								+ "目前登陆流程是依据本校系统设计的，所以可能对于他校存在兼容问题，如果您是这种情况，请按\"是\"提交反馈，以便更新！");
				doc = null;
			}
			// nh.repaint();//need this?
			return doc;
		} catch (Exception e) {
			output.error(e);
			output.error("登录失败！您的网络连接存在问题！");
		}
		return null;
	}

	public static void confirmBug(String title, String name, String debugInfo, String ps) {
		String str = "程序出BUG了么？\n\n";
		if (ps != null && ps.length() > 0) {
			str += ps + "\n\n";
		}
		str += "如果确定是BUG，是否希望立即将此BUG提交至作者？\n(相关调试信息会自动附加在报告中)";
		int reply = JOptionPane.showConfirmDialog(nm, str, "这是BUG么？", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, getIconRes("bug.png", 64));
		if (reply == JOptionPane.YES_OPTION) {
			((VersionPanel) versionPanel).addReport(name, debugInfo);
			((VersionPanel) versionPanel).addReportTitle(title);
			VersionPanel.report.setVisible(true);
		}
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

	public static void setSubmitted(boolean isSubmitted) {
		if (isSubmitted) {
			submitAns.setText("答案已提交");
			submitAns.setIcon(submitOKIcon);
			submitAns.setEnabled(false);
		} else {
			submitAns.setText("提交答案");
			submitAns.setIcon(submitIcon);
			submitAns.setEnabled(true);
		}
	}

	public static void cleanTable() {
		int count = ansTable.getRowCount();
		for (int i = 0; i < count; i++) {
			ansTableModel.removeRow(0);
		}
		ansDetail.setText("");
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

	public void checkAns() {
		// browserText
		// .setText("<html><body>正在提交答案...3秒后自动刷新<br/>请勿进行其他操作以免提交出错！</body></html>");
		itemReady = false;
		// cardLayout.show(previewPanel, "text");

		new Thread() {
			@Override
			public void run() {
				try {
					// Thread.sleep(3000);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							itemReady = true;
							// browserText.setText(NOPAGE);
							try {
								currentSection.enter();
								// submitting = false;
							} catch (Exception e) {
								output.error(e);
							}
						}
					});

				} catch (Exception e) {
					output.append("程序出现错误:" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

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
			JOptionPane.showMessageDialog(nm, str, "信息", JOptionPane.INFORMATION_MESSAGE);
		}

		public void warn(String str) {
			this.append(str);
			speak("警告：" + str);
			JOptionPane.showMessageDialog(nm, str, "警告", JOptionPane.WARNING_MESSAGE);
		}

		public void error(String str) {
			this.append(str);
			speak("错误：" + str);
			JOptionPane.showMessageDialog(nm, str, "错误", JOptionPane.ERROR_MESSAGE);
		}

		public void error(Throwable e) {
			append("程序出现错误:" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	};

	/**
	 * Added in 2.5 for quiz!
	 * 
	 * 
	 */

	static interface Room {
		public String getLabel();

		public void enter() throws Exception;
	}

	static class QuizGroup {
		ArrayList<Quiz> quizList = new ArrayList<Quiz>();
	}

	static class Quiz implements Room {
		String name;
		String timeInMin, timeStart, timeEnd;
		String link, entry;
		String id;
		QuizGroup group;
		Form2 answer, buildAnswer;
		String dateRemains;

		/**
		 * 状态标记： 0-未开始，1-进行中，2-已结束, -1-未知（出错）
		 */
		int state;

		public Quiz(String id, String link, String entry, String name, String timeInMin, String timeStart,
				String timeEnd) {
			super();
			this.id = id;
			this.name = name;
			this.timeInMin = timeInMin;
			this.timeStart = timeStart;
			this.timeEnd = timeEnd;
			this.link = link;
			this.entry = entry;
			checkTime();
		}

		public void oneKeySubmit(final int time) {
			new Thread() {
				public void run() {
					output.append("正在进入测试托管模式...(测试计划完成时间：约" + time + "分钟)");
					Form2 answer = getFakeAnswer();
					if (answer == null) {
						output.error("生成答案失败！");
						return;
					}
					// -----------Test Code---------------------------
					// Form2 answer = new Form2(username, timeInMin, timeStart,
					// timeEnd);
					// for (int i = 0; i < 6; i++)
					// answer.inputs.add(new FormPart());
					// -----------------------------------------------
					output.append("测试答案已准备就绪!");

					int reply = JOptionPane.showConfirmDialog(nm,
							"测试托管过程中请勿关闭程序，否则需要重新开始测试!\n托管时建议不要在本程序内进行其他操作，否则有可能会造成冲突。\n\n确认立即开始测试？", "注意",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if (reply != JOptionPane.OK_OPTION)
						return;
					try {
						quizWorking = true;
						String btnText = submitAns.getText();
						submitAns.setText("Quiz托管中...");
						submitAns.setEnabled(false);
						int totalCount = 0;
						for (int i = 0; i < answer.inputs.size(); i++)
							totalCount += answer.inputs.get(i).inputs.size();
						output.append("本测试共" + answer.inputs.size() + "部分，" + totalCount + "道小题");
						for (int i = 0; i < answer.inputs.size(); i++) {
							if (time > 0) {
								double delayMin = (int) (time * 100.0 / answer.inputs.size()) / 100.0;
								int count = 10000;
								int randShift = (int) (Math.random() * count * 2);
								output.append("正在进入试题第" + (i + 1) + "部分...(驻留时间:" + delayMin + "分钟,随机误差"
										+ (int) ((randShift - count) / 10.0) / 100.0 + "秒)");
								for (double j = 0; j < delayMin * 6; j += 1.0) {
									if (!quizWorking || !working)
										return;
									try {
										if (!(j + 1.0 < delayMin * 6))
											count = randShift;
										if (count > 0)
											Thread.sleep(count);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else
								output.append("正在进入试题第" + (i + 1) + "部分...(无驻留时间)");

							output.append("正在提交第" + (i + 1) + "部分答案...");
							ArrayList<Input> inputs = answer.inputs.get(i).inputs;
							client.clearInput();
							if (i < answer.inputs.size() - 1)
								client.addInput("PartNumber", i + 1 + "");
							else
								client.addInput("PartNumber", "TestEnd");
							if (i == 0)
								client.addInput("Ticking", "Y");
							else
								client.addInput("Ticking", "aaa");
							client.addInput("JavaScriptTimeout", "0");
							for (int j = 0; j < inputs.size(); j++) {
								Input in = inputs.get(j);
								client.addInput(in.name, in.value);
							}

							String origCharset = client.charset;// Fixed in
																// 2.6a2
							client.charset = "gb2312";
							if (i < answer.inputs.size() - 1)
								client.post(host + "/quiz/student/main.php");
							else {
								final Document doc = Jsoup.parse(client.post(host + "/quiz/student/main.php", true));
								doc.getElementsByTag("a").remove();
								Elements imgs = doc.getElementsByTag("img");
								for (Element img : imgs) {
									img.attributes().put("src", host + img.attr("src"));
								}
								SwingUtilities.invokeLater(new Thread() {
									@Override
									public void run() {
										browser.setHTMLContent(doc.outerHtml());
									}
								});
							}
							client.charset = origCharset;
							output.append("本部分提交成功！");
						}
						quizWorking = false;
						submitAns.setText(btnText);
						submitAns.setEnabled(true);
						output.info("所有试题已完成，测试托管已结束！");
					} catch (Exception e) {
						output.error(e);
						output.error("答案提交失败！");
					}
				};
			}.start();

		}

		public static ArrayList<Quiz> checkQuiz(String url) {
			ArrayList<Quiz> qs = new ArrayList<Quiz>();
			try {
				Document doc = null;
				boolean refreshOK = false;
				String cUrl = url;
				refresh: do {
					doc = Jsoup.parse(client.get(cUrl));
					Elements metas = doc.getElementsByTag("meta");
					for (Element m : metas) {
						if (m.attr("http-equiv").equalsIgnoreCase("refresh")) {
							cUrl = m.attr("content");
							cUrl = host + cUrl.substring(cUrl.indexOf("URL=") + 4);
							continue refresh;
						}
					}
					refreshOK = true;
				} while (!refreshOK);
				cUrl = host + doc.getElementsByTag("frame").get(2).attr("src");
				doc = Jsoup.parse(client.get(cUrl, "gb2312"));
				// debugOutput(doc.toString());
				Element table = doc.getElementsByClass("sort-table").first();
				Elements trs = table.getElementsByTag("tr");

				for (int i = 1; i < trs.size(); i++) {
					Elements tds = trs.get(i).getElementsByTag("td");
					String qUrl = host + tds.get(0).getElementsByTag("a").first().attr("href");
					String name = tds.get(3).text().trim();
					String time = tds.get(4).text().trim();
					String start = tds.get(5).text().trim();
					String end = tds.get(6).text().trim();
					String id = qUrl.substring(qUrl.indexOf("QUIZID=") + 7);
					qs.add(new Quiz(id, qUrl, cUrl, name, time, start, end));
				}

				QuizGroup group = new QuizGroup();
				group.quizList = qs;
				for (Quiz q : qs)
					q.group = group;
			} catch (Exception e) {
				output.error(e);
			}
			return qs;
		}

		public void checkTime() {
			state = -1;
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				Date sDate = format.parse(timeStart);
				Date eDate = format.parse(timeEnd);
				Date now = new Date();
				if (sDate.after(now)) {
					state = 0;
					dateRemains = TimeTool.dateDiff(format.format(now), timeStart, format, 2);
				} else if (eDate.before(now)) {
					state = 2;
				} else {
					state = 1;
					dateRemains = TimeTool.dateDiff(format.format(now), timeEnd, format, 2);// fixed
																							// in
																							// 2.6a2
				}
			} catch (Exception e) {
				output.error(e);
			}
		}

		public void checkAnswer() {
			if (!showAnswer()) {
				buildAnswer = new Form2(name, timeInMin, timeStart, timeEnd);
			} else
				buildAnswer = null;
		}

		public boolean showAnswer() {
			try {
				Form2 form = recorder.readRecord2(Form2.generateFileName(name, timeInMin, timeStart, timeEnd));
				if (form != null) {
					ArrayList<FormPart> parts = form.inputs;
					ansTable.removeAll();
					int count = 0;
					for (int i = 0; i < parts.size(); i++) {
						ArrayList<Input> inputs = parts.get(i).inputs;
						for (int j = 0; j < inputs.size(); j++) {
							Input in = inputs.get(j);
							count++;
							ansTableModel.addRow(new Object[] { count, in.value });
						}
					}
					answer = form;
					// ((VersionPanel)versionPanel).shareOnlineRecord(answer);
					return true;
				} else {
					((VersionPanel) versionPanel).checkOnlineRecord(this);
				}
			} catch (Exception e) {
				output.error(e);
			}
			return false;
		}

		public Form2 getFakeAnswer() {
			if (answer == null)
				return null;

			boolean ok = false;
			Form2 fakeForm = null;
			do {
				int fakeTotalCount = 0;
				int totalCount = 0;
				fakeForm = new Form2(answer);
				for (FormPart fp : fakeForm.inputs) {
					ArrayList<Input> fakeAns = fp.inputs;
					ConfigInfo info = config.getInfo();
					int total = fp.inputs.size();
					int count = total;
					int fakeCount = count
							- (int) Math
									.ceil(count
											* ((int) ((info.maxCorrectRate + 1 - info.minCorrectRate) * Math.random() + info.minCorrectRate))
											/ 100.0);
					ArrayList<Integer> fakeList = new ArrayList<Integer>();
					for (int i = 0; i < count; i++)
						fakeList.add(i);
					for (int i = 0; i < count - fakeCount; i++)
						fakeList.remove((int) (Math.random() * fakeList.size()));
					if (fp.inputs.get(0).value.length() > 1) {// 填空题
						int corruptNum = fakeList.size() % 2 + (int) ((fakeList.size() / 2) * Math.random()) * 2;
						for (int k = 0; k < corruptNum; k++) {// 拼写错误
							int outIndex = (int) (Math.random() * fakeList.size());
							Input in = fakeAns.get(outIndex);
							fakeList.remove(outIndex);
							in.setValue(getFakeAnswer(in.value));
						}
						if (fakeList.size() > 0)// 剩余的置换位置
							getFakeAnswer(fakeAns, fakeList);
					} else {// 选择题
						getFakeAnswer(fakeAns, fakeList);
					}
					fakeTotalCount += fakeCount;
					totalCount += count;
				}
				output.append("设计正确率：" + (int) ((totalCount - fakeTotalCount) * 100.0 / totalCount) + "%， 错误个数："
						+ fakeTotalCount);
				int reply = JOptionPane.showConfirmDialog(nm, "<html>设计正确率："
						+ (int) ((totalCount - fakeTotalCount) * 100.0 / totalCount) + "%， 错误个数：" + fakeTotalCount
						+ "<br><br>点\"是\"提交，点\"否\"重新计算，点\"取消\"取消提交。</html>", "提交确认", JOptionPane.YES_NO_CANCEL_OPTION);
				if (reply == JOptionPane.YES_OPTION)
					ok = true;
				else if (reply == JOptionPane.CANCEL_OPTION || reply == JOptionPane.CLOSED_OPTION)
					return null;
			} while (!ok);
			return fakeForm;
		}

		public String getFakeAnswer(String word) {
			if (word.length() <= 1)
				return String.valueOf((char) ((int) (Math.random() * 3) + 'A'));
			StringBuilder str = new StringBuilder(word);
			int index = (int) (Math.random() * (word.length() - 1)) + 1;
			switch ((int) (Math.random() * 4)) {
			case 0:
				str.setCharAt(index, (char) ((int) (Math.random() * 26) + 'a'));
				break;// 错拼
			case 1:
				str.deleteCharAt(index);
				break;// 漏拼
			case 2:
				str.insert(index, (char) ((int) (Math.random() * 26) + 'a'));
				break;// 多拼
			case 3:
				str = new StringBuilder();
				break;// 未填
			}
			return str.toString();
		}

		public void getFakeAnswer(ArrayList<Input> fakeAns, ArrayList<Integer> fakeList) {
			int total = fakeList.size();
			if (fakeAns.get(0).value.length() > 1) {// 非选择题，置换答案
				// debugOutput(fakeAns.toString());
				for (int i = 0; i < total - 1; i++) {
					int index = fakeList.get(i);
					int rand = fakeList.get((int) (Math.random() * (total - i - 1) + i + 1));
					String temp = fakeAns.get(index).value;
					fakeAns.get(index).setValue(fakeAns.get(rand).value);
					fakeAns.get(rand).setValue(temp);
				}
				// debugOutput(fakeAns.toString());
			} else {// 选择题，更改选项，做法很诡异...没办法...为了安全！
				for (int i = 0; i < total; i++) {
					Input in = fakeAns.get(fakeList.get(i));
					String value = in.value;
					if (value.equals("Y") || value.equals("NG"))
						in.setValue("N");
					else if (value.equals("N"))
						in.setValue("Y");
					else if (value.equals("T"))
						in.setValue("F");
					else if (value.equals("F"))
						in.setValue("T");
					else if (value.equals("A"))
						in.setValue("B");
					else if (value.equals("B"))
						in.setValue("A");
					else if (value.equals("C"))
						in.setValue(Math.random() > 0.5 ? "A" : "B");
					else {
						char max = value.charAt(0);
						in.setValue(String.valueOf((char) ((int) (Math.random() * (max - 'A')) + 'A')));
					}
				}
			}
		}

		public void getStandardAnswer(Document doc) {
			try {
				Elements tables = doc.getElementsByTag("table");
				for (int i = 3; i < tables.size() - 2; i++) {
					FormPart fp = buildAnswer.inputs.get(i - 3);
					Elements trs = tables.get(i).getElementsByTag("tr");
					for (int j = 2; j < trs.size() - 1; j++) {
						Elements tds = trs.get(j).getElementsByTag("td");
						fp.inputs.get(j - 2).setValue(tds.last().text().trim());
					}
				}
				recorder.writeRecord2(buildAnswer);
				((VersionPanel) versionPanel).shareOnlineRecord(buildAnswer);// Bug
																				// fixed
																				// in
																				// 2.5d
				output.append("标准答案解析成功！");
			} catch (Exception e) {
				output.error(e);
			}
		}

		public static boolean validateInfo(Quiz qz) {
			try {
				Document doc = Jsoup.parse(client.get(host + "/quiz/student/studentheader.php", "gb2312"));
				// debugOutput(doc.outerHtml());
				String info = doc.getElementsByTag("table").get(1).getElementsByTag("tr").get(2).getElementsByTag("td")
						.get(1).text().trim();
				info = info.substring(info.indexOf("(编号：") + 4, info.lastIndexOf(')'));
				// JOptionPane.showMessageDialog(null, info+":"+qz.name);
				if (info.equalsIgnoreCase(qz.name))
					return true;
			} catch (Exception e) {
				output.error(e);
			}
			return false;
		}

		@Override
		public String getLabel() {
			String stateStr = "";
			if (state == 0)
				stateStr = "<font color=\'red\'>" + dateRemains + "后开始</font>";
			else if (state == 2)
				stateStr = "<font color=\'red\'>已结束</font>";
			else if (state == 1)
				stateStr = "<font color=\'blue\'>正在进行，还剩" + dateRemains + "</font>";
			else
				stateStr = "<font color=\'red\'>未知状态</font>";
			return "<html>[Quiz] " + name + " (" + stateStr + ")</html>";
		}

		@Override
		public void enter() throws Exception {

			// String cUrl = this.entryLink;
			// boolean refreshOK = false;
			// Document doc;
			// refresh: do {
			// doc = Jsoup.parse(client.get(cUrl));
			// //debugOutput(doc.html());
			// Elements metas = doc.getElementsByTag("meta");
			// for (Element m : metas) {
			// if (m.attr("http-equiv").equalsIgnoreCase("refresh")) {
			// cUrl = m.attr("content");
			// cUrl = host + cUrl.substring(cUrl.indexOf("URL=") + 4);
			// continue refresh;
			// }
			// }
			// refreshOK = true;
			// } while (!refreshOK);
			// Elements fs = doc.getElementsByTag("frame");
			// for (Element f : fs)
			// debugOutput((doc = Jsoup.parse(client.get(host + "/" +
			// f.attr("src"), "gb2312"))).html());

			// JOptionPane.showMessageDialog(nm,
			// "<html>目前Quiz部分支持尚在制作和完善中，所以不保证此模块能正常运行，<br>因此建议使用浏览器正常完成Quiz！",
			// "提示",
			// JOptionPane.INFORMATION_MESSAGE);
			browser.copyCookiesFrom(client, host);
			addListeners();
			previewURL(entry);
			setSubmitted(false);
			// doc = Jsoup.parse(client.get(link));
			// debugOutput(doc.toString());
			// debugOutput(link);
			// client.get(link);
			// client.get(host +
			// "/template/loggingajax.php?whichURL=%2Fcourse07%2Fquizindex.php");
			// client.get(host +
			// "/template/loggingajax.php?whichURL=%2Fbook%2Fbook18%2Fjdreadheader.php");
			// client.get(host + "/quiz/student/studentheader.php");
			// client.get(host + "/quiz/student/studentleft.php");
			// client.get(host +
			// "/quiz/student/studentquizmain.php?PartNumber=StartTest");
			// client.get(host + "/quiz/student/main.php?PartNumber=StartTest");
			// client.clearInput();
			// client.addInput("PartNumber", "1");
			// client.addInput("Ticking", "Y");
			// client.addInput("JavaScriptTimeout", "0");
			// client.post(host + "/quiz/student/main.php");
			// doc = Jsoup.parse(client.get(host + "/quiz/student/index.php",
			// "gb2312"));
			// debugOutput(doc.toString());

		}

		public void addListeners() {
			if (webLis != null)
				browser.removeWebBrowserListener(webLis);
			webLis = new WebBrowserAdapter() {
				// boolean zoomed = false;

				@Override
				public void locationChanged(WebBrowserNavigationEvent e) {
					// output.append("NL: " + e.getNewResourceLocation());
					if (e.getNewResourceLocation().equalsIgnoreCase(
							host + "/quiz/student/main.php?PartNumber=StartTest")) {
						output.append("已进入Quiz！");
						new Thread() {
							public void run() {
								if (!validateInfo(Quiz.this)) {
									// output.append("自动切换班级...");
									// JOptionPane.showMessageDialog(null,
									// "切换中");
									for (Quiz q : group.quizList)
										if (validateInfo(q)) {
											currentQuiz = q;
											q.addListeners();
											q.checkAnswer();
											output.append("自动切换班级为：" + q.name);
											return;
										}
									output.error("匹配班级时出现严重错误！");
									return;
								} else {
									checkAnswer();
								}
							};
						}.start();
					} else if (e.getNewResourceLocation().equalsIgnoreCase(host + "/quiz/student/main.php")) {// 仅在手动完成练习时会响应此处
						if (buildAnswer != null && buildAnswer.inputs.size() <= 0) {// 需要解析表单且未完成解析
							analyzeForm();
						}

						Document doc = Jsoup.parse(browser.getHTMLContent());
						if (doc.getElementsByTag("frame").size() <= 0) {// 成绩结果页
							output.append("Quiz已全部完成！");
							if (buildAnswer != null)
								getStandardAnswer(doc);
							setSubmitted(true);
						}
					}
					// else if (e.getNewResourceLocation().contains("ResultID"))
					// {
					//
					// }
					else {
						return;
					}
				}
			};
			browser.addWebBrowserListener(webLis);
		}

		public void analyzeForm() {
			new Thread() {
				String html = "";

				public void run() {
					try {
						int partCount = 0;
						do {
							FormPart fp = new FormPart();
							ArrayList<Input> ins = fp.inputs;
							// Bug fixed in 2.5d!
							SwingUtilities.invokeAndWait(new Thread() {
								@Override
								public void run() {
									html = browser.getHTMLContent();
								}
							});
							Document doc = Jsoup.parse(html);
							partCount++;
							doc = Jsoup.parse(client.get(host + "/quiz/student/main.php?PartNumber=" + partCount));
							// debugOutput(doc.outerHtml());
							Element form = doc.getElementsByTag("form").first();
							if (form == null)
								break;
							Elements inputs = form.getElementsByTag("input");
							String lastName = "";
							for (Element input : inputs) {
								if (!input.attr("type").equalsIgnoreCase("hidden")) {
									String name = input.attr("name");
									if (!name.equals(lastName)) {
										ins.add(new Input(name, ""));
										lastName = name;
									}
								}
							}
							inputs = form.getElementsByTag("select");
							lastName = "";
							for (Element input : inputs) {
								String name = input.attr("name");
								if (!name.equals(lastName)) {
									ins.add(new Input(name, ""));
									lastName = name;
								}
							}
							inputs = form.getElementsByTag("textarea");
							lastName = "";
							for (Element input : inputs) {
								String name = input.attr("name");
								if (!name.equals(lastName)) {
									ins.add(new Input(name, ""));
									lastName = name;
								}
							}
							buildAnswer.inputs.add(fp);
						} while (true);
						recorder.writeRecord2(buildAnswer);
						output.append("Quiz表单分析成功！");
					} catch (Exception e1) {
						output.error(e1);
					}
				};
			}.start();

		}

		@Override
		public String toString() {
			return "Quiz [name=" + name + ", timeInMin=" + timeInMin + ", timeStart=" + timeStart + ", timeEnd="
					+ timeEnd + ", link=" + link + ", entry=" + entry + ", id=" + id + ", group=" + group + ", answer="
					+ answer + ", buildAnswer=" + buildAnswer + ", state=" + state + "]";
		}

	}

	static class Classroom implements Room {
		String schoolName, stuName, stuGrade;
		int id, weekday, classTime, bookID;
		String courseID;
		String link;

		String nameDesc = null;// Added in 2.6b for general usage
		String timeDesc = null;// Added in 2.6b for general usage

		public Classroom(String name, String time, String link) {
			this.link = link;
			bookID = Integer.parseInt(link.substring(link.indexOf("BID=") + 4, link.indexOf("&CID")));
			id = Integer.parseInt(link.substring(link.indexOf("&CID=") + 5, link.indexOf("&Quiz=")));
			try {
				String[] temp = name.split(" +");
				schoolName = temp[0];
				stuName = temp[1];
				temp = time.split((char) 160 + "+");
				stuGrade = temp[1];
				temp = temp[0].split("-");
				if (temp.length <= 1) {// 新版本格式的处理，Added in 2.3-4
					String timeStr = temp[0];
					int index = timeStr.indexOf("周") + 1;
					char weekChar = timeStr.charAt(index);
					String classTimeStr = timeStr.substring(index + 1);
					if (weekChar == '六')
						weekday = 6;
					else if (weekChar == '日')
						weekday = 7;
					else
						weekday = Integer.parseInt(weekChar + "");
					if (classTimeStr.equals("上午12节"))
						classTime = 1;
					else if (classTimeStr.equals("上午34节"))
						classTime = 2;
					else if (classTimeStr.equals("下午12节"))
						classTime = 3;
					else if (classTimeStr.equals("下午34节"))
						classTime = 4;
					else
						classTime = 5;
				} else {
					// id = Integer.parseInt(temp[0]);
					String week = temp[1];
					if (week.equals("Mon")) {
						weekday = 1;
					} else if (week.equals("Tue")) {
						weekday = 2;
					} else if (week.equals("Wed")) {
						weekday = 3;
					} else if (week.equals("Thu")) {
						weekday = 4;
					} else if (week.equals("Fri")) {
						weekday = 5;
					} else if (week.equals("Sat")) {
						weekday = 6;
					} else if (week.equals("Sun")) {
						weekday = 7;
					}
					if (!temp[2].equals("9~12"))
						classTime = Integer.parseInt(temp[2]) / 10 / 2 + 1;
					else
						classTime = 5;
				}
			} catch (Exception e) {
				timeDesc = time;
				nameDesc = name;
			}
		}

		public boolean checkInTime() {
			int clsNow = 0;
			Calendar now = Calendar.getInstance();
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int min = now.get(Calendar.MINUTE);
			if ((hour == 8) || (hour == 9 && min <= 35))
				clsNow = 1;
			else if ((hour == 9 && min >= 55) || (hour == 10) || (hour == 11 && min <= 30))
				clsNow = 2;
			else if ((hour == 14) || (hour == 15 && min <= 35))
				clsNow = 3;
			else if ((hour == 15 && min >= 55) || (hour == 16) || (hour == 17 && min <= 30))
				clsNow = 4;
			else if ((hour == 19) || (hour == 20) || (hour == 22 && min <= 30))
				clsNow = 5;
			if (now.get(Calendar.DAY_OF_WEEK) == (this.weekday) % 7 + 1 && clsNow == this.classTime) {
				return true;
			} else
				return false;
		}

		@Override
		public void enter() throws Exception {
			setSubmitted(false);
			if (timeDesc == null) {
				StringBuilder s = new StringBuilder("星期");
				switch (weekday) {
				case 1:
					s.append("一");
					break;
				case 2:
					s.append("二");
					break;
				case 3:
					s.append("三");
					break;
				case 4:
					s.append("四");
					break;
				case 5:
					s.append("五");
					break;
				case 6:
					s.append("六");
					break;
				case 7:
					s.append("日");
					break;
				}
				s.append(" 第");
				int clsTime = classTime;
				s.append((clsTime * 2 - 1) + "-" + clsTime * 2);
				s.append("节课");
				// -------Modified in 2.1--------
				// output.append("正在进入班级： [" + s.toString() + "] ");
				output.append("正在进入班级： [" + s.toString() + "] ", true);
			} else
				output.append("正在进入班级： [" + timeDesc + "] ", true);

			// ------------------------------
			if (nameDesc == null)
				output.append("用户身份 ：[" + schoolName + "," + stuGrade + "," + stuName + "]");
			else
				output.append("用户身份 ：[" + nameDesc + "]");
			Document doc = Jsoup.parse(client.get(host
					+ Jsoup.parse(client.get(host + this.link)).getElementsByTag("a").first().attr("href")));
			String str = doc.getElementById("MenuProgressDIV").getElementsByTag("div").get(3).attr("onclick");
			str = str.substring(str.indexOf("/course") + 7);
			courseID = str.substring(0, str.indexOf('/'));

			if (timeDesc == null && !checkInTime())
				JOptionPane
						.showMessageDialog(
								nm,
								"<html>当前不在预定上课时间，所挂机时<font color=\"red\" size=20><b>可能</b></font><font color=\"red\">无效！</font><br><br>具体请咨询相关老师！</html>",
								"警告", JOptionPane.WARNING_MESSAGE);

		}

		public Section[] gotoUnit(int unitID) throws ClientProtocolException, IOException {
			// output.append(login.get(host + this.link));
			// ------Modified in 2.1------------
			// output.append("正在进入第" + unitID + "单元...");
			output.append("正在进入第" + unitID + "单元...", true);
			// ---------------------------------
			Document doc = Jsoup.parse(client.get(host + "/book/book" + bookID + "/unit_index.php?UnitID=" + unitID));
			Elements links = null;
			// --------Modified in 2.1-----------
			try {
				links = doc.getElementsByClass("blue_study").first().parent().parent().getElementsByTag("a");
			} catch (Exception e) {
				output.warn("进入单元失败！(注:课程验证码未通过审核时，你仅能进入前两个单元！)");
				confirmBug("进入单元失败", "Enter Unit Fail Page", doc.toString(), "如果您的课程验证码(放在随教材赠的光盘里)已经激活，并且通过网页可以进入这一单元，\n而在本程序里重新登陆后仍然出现这种问题，那就可能是页面信息不兼容的问题了，请将信息反馈给我，我会仔细分析一下。");
				return null;//Added in 2.6b
			}// output.append(links.toString());
				// ----------------------------------
			Section[] ss = new Section[links.size()];
			for (int i = 0; i < ss.length; i++) {
				ss[i] = new Section(links.get(i), unitID, this.bookID);
			}
			char part = 'A';
			int as = 0, bs = 0, rs = 0;
			for (Section s : ss) {
				if (s.name.startsWith("Further Reading"))
					part = 'R';
				s.part = part;
				if (part == 'A')
					as++;
				if (part == 'B')
					bs++;
				if (part == 'R')
					rs++;
				if (s.name.equals("Writing Task"))
					part = 'B';
			}
			Section[] nss = new Section[links.size()];
			for (int i = 0; i < nss.length; i++) {
				if (i < as) {
					int index = i * 2 >= as ? 2 * i - as + (as + 1) % 2 : i * 2;
					nss[i] = ss[index];
				} else if (i < as + bs) {
					int ii = i - as;
					int index = ii * 2 >= bs ? 2 * ii - bs + (bs + 1) % 2 : ii * 2;
					nss[i] = ss[index + as];
				} else {
					int ii = i - as - bs;
					int index = ii * 2 >= rs ? 2 * ii - rs + (rs + 1) % 2 : ii * 2;
					nss[i] = ss[index + as + bs];
				}
			}
			return nss;
		}

	

		@Override
		public String toString() {
			return "Classroom [schoolName=" + schoolName + ", stuName=" + stuName + ", stuGrade=" + stuGrade + ", id="
					+ id + ", weekday=" + weekday + ", classTime=" + classTime + ", bookID=" + bookID + ", courseID="
					+ courseID + ", link=" + link + ", nameDesc=" + nameDesc + ", timeDesc=" + timeDesc + "]";
		}



		static class Section {
			String name;
			String link;
			int bookID;
			int unitID, sectionID, sisterID;
			char part;
			String testID;
			static int index = 0;
			int id;
			String post;
			Form answer;
			boolean answered;

			// int tableNum;// Added in 2.1

			// String title;

			// ArrayList<String[]> select = new ArrayList<String[]>();

			// -----Modified in 2.1-------
			public Section(Element e, int unitID, int bookID) {
				try {
					link = '/' + e.attr("href");
					name = e.text();
					this.unitID = unitID;
					this.bookID = bookID;
					id = index++;
					// if (ansBook != null) {
					// ZipEntry entry = ansBook
					// .getEntry(Form.generateFileName(bookID, bookID, bookID,
					// bookID, testID));
					// if (entry != null) {
					// InputStream in = ansBook.getInputStream(entry);
					// File tempFile = File.createTempFile("nhcetemp", ".xml");
					// tempFile.deleteOnExit();
					// OutputStream out = new FileOutputStream(tempFile);
					// byte[] buf = new byte[1024];
					// int len = 0;
					// while ((len = in.read(buf)) != -1) {
					// out.write(buf, 0, len);
					// }
					// in.close();
					// out.close();
					// answer = recorder.readRecord(tempFile);
					// }
					// }
				} catch (Exception e1) {
					output.error(e1);
				}
			}

			// -----------------------

			public void enter() throws ClientProtocolException, IOException {
				// Document doc = Jsoup.parse((login.get(host + "/book/book"
				// + bookID + link)));
				// -------Modified in 2.1-------
				// output.append("正在进入小节 [" + name + "]...");
				output.append("正在进入第" + unitID + "单元 [" + name + "]...", true);
				// -----------------------------
				currentSection = this;
				Element doc = parseInnerFrame(host + "/book/book" + bookID + link);
				// output.append(doc.toString());
				// title = doc.getElementsByClass("tittle").first().text();
				Element form;
				output.append("正在尝试解析表单...");
				cleanTable();
				if ((form = doc.getElementById("SubmitForm")) != null) {
					// output.append(form.text());
					// Elements input = form.getElementsByTag("input");
					// for(Element e : input)
					// output.append(e.toString()+"\n");
					// PrintWriter out = new PrintWriter("form" + id + ".html");
					// out.print("<html>\n" + form.toString() + "\n</html>\n");
					// out.close();
					parseForm(form);
					Element body = form.parent();
					while (body.parent() != null && !body.tagName().equalsIgnoreCase("body")) {
						body = body.parent();
					}
					preview(parsePage(body).html());

				} else if (name.equals("Detailed Reading")) {// 阅读部分的特殊处理
					// Element e =
					// doc.getElementsByAttributeValue("src","main.htm").first();
					// e = e.getElementsByTag("html").first();
					// Element js = e.getElementsByAttributeValue("src",
					// "NCTx.js").first();
					// js.attributes().put("src", host + "/book/book" + bookID +
					// link.substring(0,link.lastIndexOf('/')+1)+"NCTx.js");
					// preview(e.outerHtml());
					preview(null);
					// -----------Modified in 2.1--------
					// output.append("本节为阅读部分，没有需要提交的表单。");
					if (!inTray)
						output.append("本节为阅读部分，没有需要提交的表单。");// Modified in 2.3
					// ----------------------------------
				} else {
					preview(null);
					output.warn("解析表单失败！请检查网络连接，并重新登陆！");
					confirmBug("解析表单失败", "Parse Form Fail Page", doc.toString(), "如果您用本程序重新登陆后仍然无法解析该小节的表单，可能是解析逻辑出现漏洞，请及时反馈。");
				}
				System.out.println(this);
			}

			/**
			 * Should be invoked after having invoked the "parseForm" to get
			 * proper information
			 * 
			 * @param page
			 */
			public Element parsePage(Element page) {
				// Correct source address of each image
				if (page.getElementById("input_txt") == null) {
					Elements imgs = page.getElementsByTag("img");
					for (int i = 0; i < imgs.size(); i++) {
						Element img = imgs.get(i);
						img.attributes().put("src", host + img.attr("src"));
					}
				} else {// 对写作部分作特殊处理
					Elements imgs = page.getElementsByTag("img");
					for (int i = 0; i < imgs.size(); i++) {
						Element img = imgs.get(i);
						img.attributes().put("src", post.substring(0, post.lastIndexOf('/') + 1) + img.attr("src"));

					}
					Elements bgs = page.getElementsByAttribute("background");
					for (Element bg : bgs)
						bg.attributes().put("background",
								post.substring(0, post.lastIndexOf('/') + 1) + bg.attr("background"));
				}
				// Clean all the hrefs
				Elements hrefs = page.getElementsByTag("a");
				for (Element href : hrefs) {
					href.parent().remove();
				}
				// // Clean all the javascripts
				// Elements jss = page.getElementsByTag("script");
				// for (Element js : jss) {
				// js.remove();
				// }
				if (answered) {
					Element e = page.getElementsByTag("table").first()
							.getElementsContainingOwnText("Click on the 'Answer' button").first();
					// JOptionPane.showMessageDialog(null, e.html());
					e.html(e.html().substring(0, e.html().indexOf("Click"))
							+ "The correct/suggested answer(s) to each question are displayed below.");
				}

				// Add CSS and Js to the page
				String css = "<head><link rel=\"stylesheet\" href=\"" + host + "/book/book" + bookID
						+ "/css.css\" type=\"text/css\">";
				// String js =
				// "<script src=\"http://dict.cn/hc/\" type=\"text/javascript\"></script><script type=\"text/javascript\">dictInit();</script>";
				Document doc = Jsoup.parse("<html>" + css + "</head><body id=\"main\"></body></html>");
				// page.append(js);
				doc.getElementById("main").replaceWith(page);
				System.out.println(page.outerHtml());
				return doc;
			}

			public void parseForm(Element form) {
				post = host + form.attr("action");
				unitID = Integer.parseInt(form.getElementsByAttributeValue("name", "UnitID").attr("value"));
				sectionID = Integer.parseInt(form.getElementsByAttributeValue("name", "SectionID").attr("value"));
				sisterID = Integer.parseInt(form.getElementsByAttributeValue("name", "SisterID").attr("value"));
				testID = form.getElementsByAttributeValue("name", "TestID").attr("value");
				// tableNum = form.getElementsByTag("table").size();
				// Show all the hidden contents(especially the standard
				// answer)
				Elements hiddens;
				if (name.equals("Writing Task")) {// 对于Writing Task的特殊处理
					Elements temp = hiddens = form.nextElementSibling().getElementsByAttributeValueStarting("style",
							"display:none");// Modified in 2.1
					for (int i = 0; i < hiddens.size(); i++) {
						hiddens.get(i).attributes().put("style", "display:block");
					}
					hiddens = hiddens.first().getElementsByAttributeValue("align", "justify");
					if (hiddens.size() <= 0)// 如果没有答案显示，隐藏"Sample Answer"
						temp.first().getElementsByTag("strong").first().attributes().put("style", "display:none");
				} else {// 一般情况的处理
					hiddens = form.getElementsByAttributeValueStarting("style", "display:none");// Modified
																								// in
																								// 2.1
					for (int i = 0; i < hiddens.size(); i++) {
						hiddens.get(i).attributes().put("style", "display:block");
					}
				}
				if (hiddens.size() > 0) {
					answered = true;
					for (int i = 0; i < hiddens.size(); i++) {
						hiddens.get(i).attributes().put("style", "display:block");
					}
					setSubmitted(true);
				} else {
					setSubmitted(false);
				}
				// JPanel panel = new JPanel();
				// panel.setLayout(new BorderLayout());
				// JPanel t = new JPanel();
				// t.setLayout(new GridLayout(0, 1));

				// panel.add(t);
				// panel.add(i,BorderLayout.EAST);
				// answerList.removeAll();
				// Elements inputs = form.getElementsByTag("input");
				// Elements selects = form.getElementsByTag("select");
				// if (answer != null) {
				// int j = 0;
				// for (; j < inputs.size() - 4 && j < answer.inputs.size();
				// j++) {
				// Object[] rowData = { j + 1, answer.inputs.get(j) };
				// ansTableModel.addRow(rowData);
				// }
				// for (; j < selects.size() && j < answer.inputs.size(); j++) {
				// // t.add(new JLabel(e.parent().text()));
				// // Elements op = e.getElementsByTag("option");
				// // String[] s = new String[op.size()];
				// // JComboBox box = new JComboBox();
				// // for (int j = 0; j < s.length; j++) {
				// // Element o = op.get(j);
				// // s[j] = o.attr("value");
				// // box.addItem(s[j]);
				// // }
				// // select.add(s);
				// // answerList.add(box);
				// Object[] rowData = { j + 1, answer.inputs.get(j) };
				// ansTableModel.addRow(rowData);
				// }
				// // displayPanel.removeAll();
				// // displayPanel.add(panel);
				// // contentPanel.setVisible(false);
				// // contentPanel.setVisible(true);
				// }
				if (answered) {
					// Get the names of all the answers in the form
					LinkedHashSet<String> names = new LinkedHashSet<String>();
					Elements es = form.getElementsByAttribute("name");
					for (Element e : es) {
						if (e.attr("type").equalsIgnoreCase("hidden")) {// 隐藏信息
							continue;
						} else if (e.hasClass("fb")) {// 填空
							names.add(e.attr("name"));
						} else if (e.tagName().equalsIgnoreCase("select")) {// 下拉选择
							names.add(e.attr("name"));
						} else if (e.attributes().get("type").equalsIgnoreCase("radio")) {// 一个单选按钮
							names.add(e.attr("name"));
						} else if (e.tagName().equalsIgnoreCase("textarea")) {// 文本域
							names.add(e.attr("name"));
						}
					}

					String[] arr = names.toArray(new String[1]);
					answer = new Form(this);
					for (String a : arr) {
						answer.inputs.add(new Input(a, null));
					}
					getStandardAnswer(hiddens);
				}
				if (answer == null) {
					try {
						// ---------Modified in 2.2---------
						String name = Form.generateFileName(bookID, unitID, sectionID, sisterID, testID);
						answer = recorder.readRecord(name);
						if (answer == null)
							((VersionPanel) versionPanel).checkOnlineRecord(this, name);
						// ---------------------------------
					} catch (Exception e) {
						output.error(e);
					}
				}
				if (answer != null) {// Get answer from the answer
										// object(from web or file)
					int count = 0;
					for (Input in : answer.inputs) {
						Object[] data = { ++count, in.value };
						ansTableModel.addRow(data);
					}
				}
				output.append("表单解析成功！");
			}

			/**
			 * 根据题型提取相应的标准答案，并写入文件
			 * 
			 * @param stdAns
			 */
			public void getStandardAnswer(Elements stdAns) {
				try {
					int count = answer.inputs.size();
					if (name.startsWith("Vocabulary Task")) {
						int index = 0;
						for (int i = 0; i < stdAns.size(); i++) {
							Elements djs = stdAns.get(i).getElementsByClass("djblack");
							for (Element dj : djs) {
								answer.inputs.get(index).setValue(dj.getElementsByTag("td").get(3).text().trim());
								index++;
							}
						}
					}
					if (name.equals("Banked Cloze") || name.equals("Cloze")) {
						Elements trs = stdAns.first().getElementsByTag("tr");
						for (int i = 0; i < count; i++) {
							answer.inputs.get(i).setValue(trs.get(i + 1).getElementsByTag("td").get(3).text().trim());
						}
					} else if (name.startsWith("Translation Task")) {
						for (int i = 0; i < count; i++) {
							String str = stdAns.get(i).text();
							answer.inputs.get(i).setValue(str.substring(str.indexOf(':') + 1).trim());
						}
					} else if (name.equals("Writing Task")) {
						StringBuilder str = new StringBuilder();
						for (int i = 0; i < stdAns.size(); i++) {
							str.append(stdAns.get(i).text().trim());
						}
						answer.inputs.get(0).setValue(str.toString());
					} else if (name.equals("Reading Skills")) {
						if (stdAns.size() <= 1) {// 特殊情况的处理
							Elements trs = stdAns.get(0).getElementsByTag("tr");
							for (int i = 0; i < count; i++) {
								answer.inputs.get(i).setValue(
										trs.get(i + 1).getElementsByTag("td").get(3).text().trim());
							}
						} else
							for (int i = 0; i < count; i++) {
								Elements greens = stdAns.get(i).getElementsByClass("green");
								if (greens.size() <= 1) {// 文本域
									String str = stdAns.get(i).text();
									answer.inputs.get(i).setValue(str.substring(str.indexOf(':') + 1).trim());
								} else {// 单选
									Element td = greens.get(2).getElementsByTag("td").get(4);
									answer.inputs.get(i).setValue(td.text().trim());
								}
							}
					} else if (name.equals("Comprehension Task")) {
						// ----------Added in 2.1-------------
						if (stdAns.size() <= 1) {// 诡异表格的处理
							for (int i = 0; i < count; i++) {
								Element td = stdAns.get(0).getElementsByTag("tr").get(i + 1).getElementsByTag("td")
										.get(3);
								answer.inputs.get(i).setValue(td.text().trim());
							}
						}
						// --------------------------------------
						else {
							for (int i = 0; i < count; i++) {
								Element td = stdAns.get(i).getElementsByClass("green").get(2).getElementsByTag("td")
										.get(4);
								answer.inputs.get(i).setValue(td.text().trim());
							}
						}
					} else if (name.startsWith("Further Reading")) {
						for (int i = 0; i < count; i++) {
							Elements tds = stdAns.get(i).getElementsByClass("green").get(2).getElementsByTag("td");
							if (tds.size() > 1)// 单选
								answer.inputs.get(i).setValue(tds.get(4).text().trim());
							else {// 填空
								String str = tds.get(0).text();
								str = str.substring(str.indexOf(':') + 1);
								answer.inputs.get(i).setValue(str.trim());
							}
						}
					}

					recorder.writeRecord(this.answer);
					((VersionPanel) versionPanel).shareOnlineRecord(this.answer);
				} catch (Exception e) {
					output.append("程序出现错误:" + e.getLocalizedMessage());
					output.warn("解析标准答案失败！");
					confirmBug("解析标准答案失败", "Parse Standard Answer Fail Page",stdAns.toString(), "如果您用本程序重新登陆后仍然无法解析，可能是解析逻辑出现漏洞，请及时反馈。");
					e.printStackTrace();
				}
			}

			public ArrayList<Input> getFakeAnswer() {
				if (answer == null)
					return null;
				// NOTODO complete fake making
				ArrayList<Input> fakeAns = new ArrayList<Input>();
				ConfigInfo info = config.getInfo();
				int total = answer.inputs.size();
				int count;
				if (name.startsWith("Further Reading"))// Further Reading
														// 只处理前7题客观题
					count = 7;
				else
					count = total;
				int fakeCount = count
						- (int) Math
								.ceil(count
										* ((int) ((info.maxCorrectRate + 1 - info.minCorrectRate) * Math.random() + info.minCorrectRate))
										/ 100.0);// Modified in 2.1
				for (int i = 0; i < total; i++) {
					fakeAns.add(new Input(answer.inputs.get(i)));
				}
				ArrayList<Integer> fakeList = new ArrayList<Integer>();
				for (int i = 0; i < count; i++)
					fakeList.add(i);
				for (int i = 0; i < count - fakeCount; i++)
					fakeList.remove((int) (Math.random() * fakeList.size()));
				if (name.startsWith("Vocabulary Task")) {
					if (fakeList.size() % 2 == 1) {// 拼写错误
						int outIndex = (int) (Math.random() * fakeList.size());
						Input in = fakeAns.get(outIndex);
						fakeList.remove(outIndex);
						in.setValue(getFakeAnswer(in.value));
					}
					if (fakeList.size() > 0)// 剩余的置换位置
						getFakeAnswer(fakeAns, fakeList);
				} else if (name.equals("Banked Cloze") || name.equals("Comprehension Task")
						|| name.startsWith("Further Reading")) {// 置换位置
					getFakeAnswer(fakeAns, fakeList);
				} else if (name.equals("Cloze")) {
					getFakeAnswerForCloze(fakeAns, fakeList);
				} else if (name.equals("Reading Skills")) {
					if (fakeAns.get(0).name.startsWith("q")) {// 是客观题
						getFakeAnswer(fakeAns, fakeList);
					}
				} else {// 主观题表示伤不起...
					fakeCount = 0;
				}
				output.append("本题中客观题设计错误:" + fakeCount + "个，正确率:" + (int) ((count - fakeCount) * 100.0 / count) + "%");// Modified
																														// in
																														// 2.1
				return fakeAns;
			}

			public String getFakeAnswer(String word) {
				if (word.length() <= 1)
					return String.valueOf((char) ((int) (Math.random() * 3) + 'A'));
				StringBuilder str = new StringBuilder(word);
				int index = (int) (Math.random() * (word.length() - 1)) + 1;
				switch ((int) (Math.random() * 4)) {
				case 0:
					str.setCharAt(index, (char) ((int) (Math.random() * 26) + 'a'));
					break;// 错拼
				case 1:
					str.deleteCharAt(index);
					break;// 漏拼
				case 2:
					str.insert(index, (char) ((int) (Math.random() * 26) + 'a'));
					break;// 多拼
				case 3:
					str = new StringBuilder();
					break;// 未填
				}
				return str.toString();
			}

			/**
			 * Added in 2.1
			 * 
			 * @param fakeAns
			 * @param fakeList
			 */
			public void getFakeAnswerForCloze(ArrayList<Input> fakeAns, ArrayList<Integer> fakeList) {
				int total = fakeList.size();
				for (int i = 0; i < total - 1; i++) {
					Input in = fakeAns.get(fakeList.get(i));
					in.setValue(in.value + "*");// 标记设计错误，在填写答案时完成替换
				}
			}

			public void getFakeAnswer(ArrayList<Input> fakeAns, ArrayList<Integer> fakeList) {
				int total = fakeList.size();
				if (fakeAns.get(0).value.length() > 1) {// 非选择题，置换答案
					// debugOutput(fakeAns.toString());
					for (int i = 0; i < total - 1; i++) {
						int index = fakeList.get(i);
						int rand = fakeList.get((int) (Math.random() * (total - i - 1) + i + 1));
						String temp = fakeAns.get(index).value;
						fakeAns.get(index).setValue(fakeAns.get(rand).value);
						fakeAns.get(rand).setValue(temp);
					}
					// debugOutput(fakeAns.toString());
				} else {// 选择题，更改选项，做法很诡异...没办法...为了安全！
					for (int i = 0; i < total; i++) {
						Input in = fakeAns.get(fakeList.get(i));
						String value = in.value;
						if (value.equals("Y") || value.equals("NG"))
							in.setValue("N");
						else if (value.equals("N"))
							in.setValue("Y");
						else if (value.equals("T"))
							in.setValue("F");
						else if (value.equals("F"))
							in.setValue("T");
						else if (value.equals("A"))
							in.setValue("B");
						else if (value.equals("B"))
							in.setValue("A");
						else if (value.equals("C"))
							in.setValue(Math.random() > 0.5 ? "A" : "B");
						else {
							char max = value.charAt(0);
							in.setValue(String.valueOf((char) ((int) (Math.random() * (max - 'A')) + 'A')));
						}
					}
				}
			}

			public Element parseInnerFrame(String url) throws ClientProtocolException, IOException {
				// JOptionPane.showConfirmDialog(null, url);
				String get = client.get(url, "GB2312");
				Document doc = Jsoup.parse(get);
				Elements innerFrames = doc.getElementsByTag("frame");
				if (innerFrames != null) {
					for (Element e : innerFrames) {
						String linkAdd = e.attr("src");
						if (linkAdd.equals("about:blank"))
							continue;
						if (linkAdd.charAt(0) != '/')
							linkAdd = '/' + linkAdd;
						if (!linkAdd.startsWith("/book/"))
							linkAdd = "/book/book" + bookID + link.substring(0, link.lastIndexOf('/')) + linkAdd;
						e.appendChild(parseInnerFrame(host + linkAdd));
						// get.append(parseInnerFrame(host + linkAdd));
					}
				}
				// output.append(doc.toString()+"\n\n\n\n\n");
				Elements e = doc.getElementsByTag("html");
				return e.first();
			}

			@Override
			public String toString() {
				return "Section [name=" + name + ", link=" + link + ", bookID=" + bookID + ", unitID=" + unitID
						+ ", sectionID=" + sectionID + ", sisterID=" + sisterID + ", part=" + part + ", testID="
						+ testID + ", id=" + id + ", post=" + post + ", answer=" + answer + ", answered=" + answered
						+ "]";
			}

			// public enum SectionType {
			// VOCABULARY_FILL_BLANK("Fill in the blanks with the words given below. Change the form where necessary.");
			// private String v;
			//
			// SectionType(String value) {
			// v = value;
			// }
			//
			// @Override
			// public String toString() {
			// return v;
			// }
			// }
		}

		@Override
		public String getLabel() {
			if (timeDesc != null) {
				return timeDesc;
			}
			StringBuilder s = new StringBuilder("<html>星期");
			Classroom c = this;
			switch (c.weekday) {
			case 1:
				s.append("一");
				break;
			case 2:
				s.append("二");
				break;
			case 3:
				s.append("三");
				break;
			case 4:
				s.append("四");
				break;
			case 5:
				s.append("五");
				break;
			case 6:
				s.append("六");
				break;
			case 7:
				s.append("日");
				break;
			}
			s.append(" 第");
			int clsTime = c.classTime;
			if (clsTime < 5)
				s.append((clsTime * 2 - 1) + "-" + clsTime * 2);
			else
				s.append("9-12");
			s.append("节课");
			if (c.checkInTime()) {
				s.append("(<font color=\'blue\'>当前正在进行中</font>)");
			} else
				s.append("(<font color=\'red\'>当前未在该时段</font>)");
			s.append("</html>");
			return s.toString();
		}

	}

	class AutoTask implements Runnable {
		int unit, section;
		int delay = 1000;
		int randomDelay = 100;
		int actionType = 0;// 0-All; 1-Unit; 2-Section
		/**
		 * 是否循环挂机
		 */
		boolean recycle = false;
		/**
		 * 是否自动提交答案
		 */
		boolean auto_post = true;

		public AutoTask(int unit, int section) {
			this.unit = unit + 1;
			this.section = section + 1;
			actionType = 2;
			// JOptionPane.showMessageDialog(null, this.unit+"/"+this.section);
		}

		public AutoTask(int unit) {
			this.unit = unit + 1;
			actionType = 1;
		}

		public AutoTask() {
			actionType = 0;
		}

		@Override
		public void run() {
			StringBuilder output = new StringBuilder();
			output.append("自动挂机已启动！范围：");
			switch (actionType) {
			case 0:
				output.append("[所有单元]");
				break;
			case 1:
				output.append("[第" + unit + "单元, 所有小节]");
				break;
			case 2:
				output.append("[第" + unit + "单元, 第" + section + "小节]");
				break;
			}

			NHMonkey.output.append(output.toString());
			final int maxRetry = 5;
			int retryCount = maxRetry;
			int retryDelay = 10;
			while (retryCount > 0) {
				retryCount--;
				if (retryCount + 1 < maxRetry) {// 断线重连
					NHMonkey.output.append(retryDelay + "秒后尝试恢复挂机...");
					try {
						Thread.sleep(retryDelay * 1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					retryDelay *= (maxRetry - retryCount + 1);
					NHMonkey.output.append("正在尝试恢复挂机...");
					NHMonkey.output.append("(1)正在尝试重新登陆...");
					if (!NHMonkey.nm.tryReLogIn(15))
						continue;//Changed in 2.6b
					NHMonkey.output.append("(2)正在尝试重新挂机...");
				}

				try {
					while (true) {
						if (actionType == 0) {// 所有单元
							for (int i = 0; i < 10; i++) {
								sections = cls.gotoUnit(i + 1);
								for (Section s : sections) {
									act(s);
									if (!autoBrowse)
										return;
								}
							}
						} else if (actionType == 1) {// 某一单元
							sections = cls.gotoUnit(unit);
							for (Section s : sections) {
								act(s);
								if (!autoBrowse)
									return;
							}
						} else {// 某一小节...有用么。。。?
							Section s = cls.gotoUnit(unit)[section - 1];
							act(s);
							if (!autoBrowse)
								return;
						}
						if (!recycle) {
							Thread.sleep((long) (delay + Math.random() * randomDelay));
							autoBtnActionListener.actionPerformed(new ActionEvent(this, 0, "Stop"));
							NHMonkey.output.info("自动挂机已结束！");
							return;
						}
					}
				} catch (Throwable e) {
					try {
						Thread.sleep((long) (delay + Math.random() * randomDelay));
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					NHMonkey.output.append("自动挂机过程中发生异常！");
					NHMonkey.output.error(e);
				}
			}
			autoBtnActionListener.actionPerformed(new ActionEvent(this, 0, "Stop"));
			NHMonkey.output.error("自动挂机已由于异常中断且重试" + (maxRetry - retryCount - 1) + "次后仍未能恢复！");
		}

		protected void act(Section s) throws ClientProtocolException, IOException, InterruptedException {
			s.enter();
			Thread.sleep((long) (delay + Math.random() * randomDelay));
			if (!autoBrowse) {
				output.info("自动挂机已被用户中断！");
				return;
			}
			if (auto_post && !s.answered && s.answer != null) {
				fillAnsAction();
				Thread.sleep((long) (delay + Math.random() * randomDelay));
				if (!autoBrowse) {
					output.info("自动挂机已被用户中断！");
					return;
				}
				submitAnsAction();
				Thread.sleep(3000);// 设定延时以保证提交成功
				if (s.name.equals("Writing Task") || s.name.equals("Reading Skills")
						|| s.name.startsWith("Translation Task"))
					Thread.sleep(3000);// 针对大数据流增加延时时间
				// while(NHMonkey.submitting)
				// Thread.sleep(100);
				if (!autoBrowse) {
					output.info("自动挂机已被用户中断！");
					return;
				}
			}
		}
	}

}
