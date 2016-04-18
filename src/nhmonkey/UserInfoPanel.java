package nhmonkey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UserInfoPanel extends JPanel {

	private static final long serialVersionUID = 5999965417053655927L;
	JPanel onlineRecord, studyRecord, infoBoard;
	// JTextArea infoDetail;
	JTextPane infoDetail;
	JTable infoTable;
	JTable recentOnline, monthsOnline;// , detailOnline;
	DefaultTableModel infoModel, recentModel, monthsModel;// ,detailOnlineModel;
	JPanel onlineSummary;
	JButton updateBtn;
	JLabel totalTerm, totalToday, totalNow;
	JLabel totalTermTime, totalTodayTime, totalNowTime;
	JTable studyTable;
	DefaultTableModel studyTableModel;
	ArrayList<String> infoLinks;

	public UserInfoPanel() {
		setLayout(new BorderLayout());
		JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
		add(pane);
		onlineRecord = new JPanel();
		pane.addTab("上网记录", onlineRecord);
		studyRecord = new JPanel();
		pane.addTab("练习记录", studyRecord);
		infoBoard = new JPanel();
		pane.addTab("学校通知", infoBoard);
		infoBoard.setLayout(new BorderLayout());
		// infoDetail = new JTextArea();
		infoDetail = new JTextPane();
		infoDetail.setContentType("text/html");
		infoDetail.setBackground(NHMonkey.bgColor);
		// infoDetail.setLineWrap(true);
		// infoDetail.setAutoscrolls(false);
		// infoDetail.setWrapStyleWord(true);
		infoDetail.setEditable(false);
		infoDetail.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					URL url = e.getURL();
					if (url != null) {// 外部地址
						if (url.getProtocol().equalsIgnoreCase("mailto")) {
							if (JOptionPane.showConfirmDialog(NHMonkey.nm, "是否启动系统默认邮件客户端？", "请选择",
									JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
								VersionPanel.browseWithDesktop(e.getURL().toExternalForm());
						} else
							VersionPanel.browseWithDesktop(e.getURL().toExternalForm());
					} else {// 内部地址(下载链接)
						try {
							NHMonkey.client.download(NHMonkey.host + e.getDescription());
						} catch (Exception e1) {
							NHMonkey.output.error(e1);
						}
					}
				}
			}
		});
		infoTable = new JTable() {
			private static final long serialVersionUID = -3261090591766326664L;

			@Override
			public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
				super.changeSelection(rowIndex, columnIndex, toggle, extend);
				if (!NHMonkey.logged)
					return;
				try {
					String html = NHMonkey.client.get(NHMonkey.host + "/login/" + infoLinks.get(rowIndex));
					if (html != null) {
						Document doc = Jsoup.parse(html);
						// Elements trs = doc.getElementsByClass("sort-table")
						// .first().getElementsByTag("tbody").get(1)
						// .getElementsByTag("tr");
						// StringBuilder str = new
						// StringBuilder(htmlToStr("内容:\n"
						// + trs.get(1).getElementsByTag("td").get(1)
						// .html()));
						// if (trs.size() >= 3)
						// str.append("\n\n附件:(请从浏览器中登录并下载)\n"
						// + trs.get(2).getElementsByTag("td").get(1)
						// .text());
						// infoDetail.setText(str.toString());
						// infoDetail.setCaretPosition(0);
						Element t = doc.getElementsByClass("sort-table").first();
						t.attributes().put("width", "100%");
						infoDetail.setText(t.outerHtml());
						infoDetail.setCaretPosition(0);
					}
				} catch (Exception e) {
					NHMonkey.output.error(e);
				}
			}
		};
		JScrollPane infoTableScroll = new JScrollPane(infoTable);
		infoBoard.add(infoTableScroll, BorderLayout.WEST);
		JScrollPane detailScroll = new JScrollPane(infoDetail);
		detailScroll.setBorder(new TitledBorder(""));
		infoBoard.add(detailScroll);

		infoModel = new NHMonkey.UneditableTableModel();
		infoTable.setModel(infoModel);
		infoModel.addColumn("时间");
		infoModel.addColumn("标题");
		infoTableScroll.setPreferredSize(new Dimension(400, 100));
		infoTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		infoTable.getColumnModel().getColumn(1).setPreferredWidth(260);
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BorderLayout());
		updateBtn = new JButton("<html>更新<br>信息</html>", NHMonkey.getIconRes("refresh.png", 36));
		// updateBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
		add(btnPanel, BorderLayout.EAST);
		updateBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				update(false);
			}
		});
		btnPanel.setBorder(new CompoundBorder(new TitledBorder(""), new EmptyBorder(20, 5, 20, 5)));
		btnPanel.add(updateBtn);
		updateBtn.setBounds(100, 100, 10, 10);
		JPanel rightPanel = new JPanel();
		// onlineRecord.setLayout(new BorderLayout());
		onlineRecord.setLayout(new GridLayout(1, 0));

		// onlineCharts.setLayout(new GridLayout(1, 0));
		rightPanel.setLayout(new BorderLayout());
		recentModel = new NHMonkey.UneditableTableModel();
		monthsModel = new NHMonkey.UneditableTableModel();
		// detailOnlineModel= new NHMonkey.UneditableTableModel();
		onlineRecord.add(new JScrollPane(recentOnline = new JTable()
		// {
		// private static final long serialVersionUID = -3261090591766326664L;
		//
		// @Override
		// public void changeSelection(int rowIndex, int columnIndex, boolean
		// toggle, boolean extend) {
		// super.changeSelection(rowIndex, columnIndex, toggle, extend);
		// // Actions...
		// }
		// }
				));
		onlineRecord.add(rightPanel);
		recentOnline.setModel(recentModel);
		rightPanel.add(new JScrollPane(monthsOnline = new JTable()
		// {
		// private static final long serialVersionUID = -3261090591766326664L;
		//
		// @Override
		// public void changeSelection(int rowIndex, int columnIndex, boolean
		// toggle, boolean extend) {
		// super.changeSelection(rowIndex, columnIndex, toggle, extend);
		// // Actions...
		// }
		// }
				));
		monthsOnline.setModel(monthsModel);
		// onlineRecord.add(new JScrollPane(detailOnline = new JTable()));
		// detailOnline.setModel(detailOnlineModel);
		rightPanel.add(onlineSummary = new JPanel(), BorderLayout.EAST);
		recentModel.addColumn("日期");
		recentModel.addColumn("登陆次数");
		recentModel.addColumn("平均时长");
		recentModel.addColumn("总计");
		monthsModel.addColumn("年份");
		monthsModel.addColumn("月份");
		monthsModel.addColumn("时长");
		TableColumnModel cm = recentOnline.getColumnModel();
		cm.getColumn(0).setPreferredWidth(150);
		onlineSummary.setBorder(new TitledBorder(""));
		onlineSummary.setPreferredSize(new Dimension(100, 20));
		onlineSummary.setLayout(new GridLayout(0, 1));
		totalNow = new JLabel("本次总计", SwingConstants.CENTER);
		// totalNow.setForeground(Color.BLUE);
		totalNow.setFont(new Font(totalNow.getFont().getName(), Font.BOLD, 12));
		onlineSummary.add(totalNow);
		totalNowTime = new JLabel("--:--:--", SwingConstants.CENTER);
		// totalNow.setForeground(Color.BLUE);
		totalNowTime.setFont(new Font(totalNowTime.getFont().getName(), Font.BOLD, 12));
		onlineSummary.add(totalNowTime);
		totalToday = new JLabel("今日总计", SwingConstants.CENTER);
		// totalToday.setForeground(Color.BLUE);
		totalToday.setFont(new Font(totalToday.getFont().getName(), Font.BOLD, 12));
		onlineSummary.add(totalToday);
		totalTodayTime = new JLabel("--:--:--", SwingConstants.CENTER);
		// totalNow.setForeground(Color.BLUE);
		totalTodayTime.setFont(new Font(totalTodayTime.getFont().getName(), Font.BOLD, 12));
		onlineSummary.add(totalTodayTime);
		totalTerm = new JLabel("学期总计", SwingConstants.CENTER);
		totalTerm.setForeground(Color.BLUE);
		totalTerm.setFont(new Font(totalTerm.getFont().getName(), Font.BOLD, 14));
		onlineSummary.add(totalTerm);
		totalTermTime = new JLabel("--:--:--", SwingConstants.CENTER);
		totalTermTime.setForeground(Color.BLUE);
		totalTermTime.setFont(new Font(totalTermTime.getFont().getName(), Font.BOLD, 14));
		onlineSummary.add(totalTermTime);

		studyRecord.setLayout(new BorderLayout());
		studyRecord.add(new JScrollPane(studyTable = new JTable()
		// {
		// private static final long serialVersionUID = -3261090591766326664L;
		//
		// @Override
		// public void changeSelection(int rowIndex, int columnIndex, boolean
		// toggle, boolean extend) {
		// super.changeSelection(rowIndex, columnIndex, toggle, extend);
		// // Actions...
		// }
		// }
				));
		studyTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint());
					int col = ((JTable) e.getSource()).columnAtPoint(e.getPoint());
					if (col >= 2 && col <= 11 && row <= 12) {
						try {
							int sec = row + 1;
							if (sec >= 7)// PartB的Reading不在返回的表中，需要跳过
								sec++;
							NHMonkey.cls.gotoUnit(col - 1)[sec].enter();
						} catch (Exception e1) {
							NHMonkey.output.error(e1);
						}

					}
				}
			}
		});
		studyTableModel = new NHMonkey.UneditableTableModel();
		studyTable.setModel(studyTableModel);
		studyTableModel.addColumn("标题");
		studyTableModel.addColumn("题型");
		for (int i = 0; i < 10; i++)
			studyTableModel.addColumn("Unit " + (i + 1));
		studyTableModel.addColumn("平均");
		studyTable.getColumnModel().getColumn(0).setPreferredWidth(250);
	}

	public void update(final boolean silence) {
		// 使用线程以防止Swing线程阻塞
		new Thread() {
			public void run() {
				try {
					if (!NHMonkey.logged) {
						NHMonkey.output.warn("请先登录！");
						return;
					}

					System.out.println("Updating...");

					// 通知信息
					final String infoHtml = NHMonkey.client.get(NHMonkey.host + "/login/announcements.php");
					System.out.println(infoHtml);
					SwingUtilities.invokeLater(new Thread() {
						@Override
						public void run() {
							Document doc = Jsoup.parse(infoHtml);
							Element table = doc.getElementsByClass("sort-table").first();
							if (table != null) {
								Elements infos = table.getElementsByTag("tr");
								infoLinks = new ArrayList<String>();
								int rows = infoModel.getRowCount();
								for (int i = 0; i < rows; i++)
									infoModel.removeRow(0);
								for (Element info : infos) {
									String str = info.text();
									int index = str.indexOf(':');
									Object[] rowData = { str.substring(0, index), str.substring(index + 1) };
									infoModel.addRow(rowData);
									infoLinks.add(info.getElementsByTag("a").first().attr("href"));
								}
							}
						}
					});

					// 在线时间表与在线时间统计
					final String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					final String onlineTimeHtml = NHMonkey.client.get(NHMonkey.host + "/login/onlinetime.php");
					final String timeCountHtml = NHMonkey.client.get(NHMonkey.host + "/course" + NHMonkey.cls.courseID
							+ "/gradebooklist.php");
					final String latestTimeHtml = NHMonkey.client.get(NHMonkey.host
							+ "/login/onlinedetail.php?DateWhat=" + currentTime);
					System.out.println(onlineTimeHtml);
					SwingUtilities.invokeLater(new Thread() {
						@Override
						public void run() {
							Document onlineTimeDoc = Jsoup.parse(onlineTimeHtml);
							Elements onlineTables = onlineTimeDoc.getElementsByClass("sort-table");
							updateRecentChart(onlineTables.first());
							updateMonthChart(onlineTables.get(1));

							Document timeCountDoc = Jsoup.parse(timeCountHtml);
							Elements table = timeCountDoc.getElementsByTag("table");// Modified
																					// in
																					// 2.1
																					// ----有时此页无可用内容
							if (table.size() > 0) {
								String timeThisTerm = table.get(1).getElementsByTag("tbody").first()
										.getElementsByTag("td").first().text().trim();
								totalTermTime.setText(timeThisTerm + "小时");
							}
							// JOptionPane.showMessageDialog(null, currentTime);
							if (((String) recentModel.getValueAt(0, 0)).startsWith(currentTime))
								totalTodayTime.setText((String) recentModel.getValueAt(0, 3));

							Document latestTimeDoc = Jsoup.parse(latestTimeHtml);
							String latestTime = latestTimeDoc.getElementsByTag("table").get(1)
									.getElementsByTag("tbody").first().getElementsByTag("tr").last()
									.getElementsByTag("td").get(3).text().trim();
							totalNowTime.setText(latestTime);

						}
					});

					// 练习记录表，并且显示UserInfoPanel(如果silence==false时)
					final String recordTableHtml = NHMonkey.client.get(NHMonkey.host + "/book/book"
							+ NHMonkey.cls.bookID + "/recordsummary.php");
					System.out.println(recordTableHtml);
					SwingUtilities.invokeLater(new Thread() {
						@Override
						public void run() {
							Document recordDoc = Jsoup.parse(recordTableHtml);
							Element recordTable = recordDoc.getElementsByClass("sort-table").first();
							updateStudyRecord(recordTable);
							if (!silence)
								NHMonkey.infoPanel.setSelectedIndex(2 - 1);
							NHMonkey.output.append("用户信息已更新");
						}
					});

				} catch (Exception e) {
					final Exception e1 = e;
					SwingUtilities.invokeLater(new Thread() {
						@Override
						public void run() {
							NHMonkey.output.error(e1);
						}
					});
				}
			};
		}.start();

	}

	public void updateRecentChart(Element element) {
		Elements trs = element.getElementsByTag("tbody").first().getElementsByTag("tr");
		int count = recentModel.getRowCount();
		for (int i = 0; i < count; i++)
			recentModel.removeRow(0);
		// JOptionPane.showMessageDialog(null, trs.size());
		for (Element tr : trs) {
			Elements tds = tr.getElementsByTag("td");
			String str = tds.get(0).text();// 可恶的奇葩空白符，通通消灭掉！
			Object[] data = { str.substring(str.indexOf('2')), tds.get(1).text().trim(), tds.get(2).text().trim(),
					tds.get(3).text().trim() };// tds.get(4)-->url
			recentModel.addRow(data);
		}
		Elements foots = element.getElementsByTag("tfoot").first().getElementsByTag("td");
		Object[] data = { foots.get(0).text().trim(), foots.get(1).text().trim(), foots.get(2).text().trim(),
				foots.get(3).text().trim() };
		recentModel.addRow(data);
		Object[] data2 = { "", "", foots.get(5).text().trim(), foots.get(6).text().trim() };
		recentModel.addRow(data2);
	}

	public void updateMonthChart(Element element) {
		Elements trs = element.getElementsByTag("tbody").first().getElementsByTag("tr");
		int count = monthsModel.getRowCount();
		for (int i = 0; i < count; i++)
			monthsModel.removeRow(0);
		for (Element tr : trs) {
			Elements tds = tr.getElementsByTag("td");
			Object[] data = { tds.get(0).text().trim(), tds.get(1).text().trim(), tds.get(2).text().trim() };// tds.get(4)-->url
			monthsModel.addRow(data);
		}
		Elements foots = element.getElementsByTag("tfoot").first().getElementsByTag("td");
		Object[] data = { "", foots.get(0).text().trim(), foots.get(1).text().trim() };
		monthsModel.addRow(data);
	}

	public void updateStudyRecord(Element table) {
		int count = studyTableModel.getRowCount();
		for (int i = 0; i < count; i++)
			studyTableModel.removeRow(0);
		Elements trs = table.getElementsByTag("tbody").first().getElementsByTag("tr");
		char[] parts = { 'A', 'B', 'R' };
		int part = -1;
		for (Element tr : trs) {
			Elements tds = tr.getElementsByTag("td");
			if (tds.size() > 1) {
				Object[] data = new Object[13];
				data[0] = tds.first().text().trim() + " (" + parts[part] + ")";
				for (int i = 1; i < tds.size(); i++)
					data[i] = tds.get(i).text().trim();
				studyTableModel.addRow(data);
			} else
				part++;
		}
		Elements foots = table.getElementsByTag("tfoot").first().getElementsByTag("td");
		Object[] data = new Object[13];
		data[0] = foots.first().text();
		data[1] = "";
		data[12] = "";
		for (int i = 1; i < foots.size(); i++)
			data[i + 1] = foots.get(i).text();

		studyTableModel.addRow(data);
	}
// Deleted in 2.4-1
//	public static String htmlToStr(String htmlStr) {
//		StringBuilder result = new StringBuilder();
//		boolean flag = true;
//		boolean newPara = false;
//		if (htmlStr == null) {
//			return null;
//		}
//		htmlStr = htmlStr.replace("\"", "");
//		char[] a = htmlStr.toCharArray();
//		int length = a.length;
//		for (int i = 0; i < length; i++) {
//			if (a[i] == '<') {
//				flag = false;
//				if (a[i + 1] == 'p' && (a[i + 2] == '>' || a[i + 2] == ' '))
//					newPara = true;
//				else
//					newPara = false;
//				continue;
//			}
//			if (a[i] == '>') {
//				flag = true;
//				continue;
//			}
//			if (flag == true) {
//				if (newPara) {
//					result.append("\n");
//					newPara = false;
//				}
//				result.append(a[i]);
//			}
//		}
//		return result.toString().replace("&nbsp;", " ");
//	}
}
