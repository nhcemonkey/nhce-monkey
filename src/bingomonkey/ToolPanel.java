package bingomonkey;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class ToolPanel extends JPanel {

	private static final long serialVersionUID = 817994693276024849L;
	private static final String NOPAGE = "<html>请输入需要查询的单词或短句，<br>然后点击需要使用的在线辞典<html>";// Modified
																						// in
																						// 2.1
	// private static final String LOOKUP_ERROR = "查询失败！请选择合适的辞典！";
	static JTextArea input;
	static JWebBrowser browser;
	static CardLayout cardLayout;
	static JPanel rightPanel;
	static ArrayList<DicButton> dics;

	public ToolPanel() {
		setLayout(new BorderLayout());
		input = new JTextArea();
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(new JScrollPane(input));
		add(leftPanel, BorderLayout.WEST);
		JPanel lookUpPanel = new JPanel(new BorderLayout());
		leftPanel.add(lookUpPanel, BorderLayout.SOUTH);
		JButton lookUpAuto = new JButton("自动选择", BGMonkey.getIconRes("dictionary.png"));
		// lookUpAll.setVisible(false);// 暂不启用
		lookUpAuto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lookUp(input.getText());
			}
		});
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					lookUp(input.getText());
					e.consume();
				}
			}
		});
		// lookUpPanel.add(lookUpAuto, BorderLayout.SOUTH);
		JPanel dicsPanel = new JPanel(new GridLayout(1, 0));
		dics = new ArrayList<ToolPanel.DicButton>();
		dics.add(new DicButton(" Google ", "Google.png", "http://translate.google.cn/?hl=zh-cn#auto%7Czh-CN%7C"
				+ DicButton.MASK) {
			private static final long serialVersionUID = 1L;

			@Override
			public void parseHTML(String url) {
				// try {
				// Document doc = Jsoup.parse(BGMonkey.client.get(url));
				// parseHTML(doc, doc.getElementById("gt-form-c"), true);
				// } catch (Exception e) {
				// BGMonkey.output.warn(LOOKUP_ERROR);
				// BGMonkey.output.error(e);
				// }
				super.parseHTML(url);
			}
		});

		dics.add(new DicButton(" 海  词 ", "Dict.png", "http://dict.cn/mini.php?q=" + DicButton.MASK) {
			private static final long serialVersionUID = 1L;

			@Override
			public void parseHTML(String url) {
				super.parseHTML(url);
			}
		});

		for (JButton dic : dics) {
			dicsPanel.add(dic);
		}
		// dicsPanel.add(lookUpAuto);

		lookUpPanel.add(dicsPanel);
		rightPanel = new JPanel(cardLayout = new CardLayout());
		add(rightPanel);
		rightPanel.setBorder(new TitledBorder("查询结果"));
		rightPanel.add(browser = new Browser(), "browser");
		browser.setBarsVisible(false);
		JLabel browserText = new JLabel(NOPAGE, SwingConstants.CENTER);
		// previewText.setAlignmentX(110.5f);
		// previewText.setBackground(Color.WHITE);
		browserText.setForeground(Color.GRAY);
		Font font = browserText.getFont();
		browserText.setFont(new Font(font.getName(), Font.BOLD, 20));
		rightPanel.add(browserText, "text");
		cardLayout.show(rightPanel, "text");
	}

	public static void lookUp(String str) {
		if (str.split(" +").length > 1) {
			input.setText(str);
			dics.get(0).lookUp();// 默认使用Google翻译进行短句查询
		} else {
			boolean isEnglish = true;
			char[] strArr = str.toCharArray();
			for (int i = 0; i < strArr.length; i++)
				if (strArr[i] > 127) {
					isEnglish = false;
					break;
				}
			if (!isEnglish) {
				input.setText(str);
				dics.get(0).lookUp();// 默认使用Google翻译进行非英文查询
			} else {
				input.setText(str);
				dics.get(1).lookUp();// 默认使用海词进行单词查询
			}
		}
		BGMonkey.bgm.infoPanel.setSelectedIndex(1);
	}

	public static class DicButton extends JButton {

		private static final long serialVersionUID = 8413199321090924892L;
		final String URL_BASE;
		String url, keyword;
		final static String MASK = "@@";

		public DicButton(String text, String icon, final String url) {
			super(text, BGMonkey.getIconRes(icon));
			this.URL_BASE = url;
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					lookUp();
				}
			});
		}

		public void lookUp() {

			try {
				String str = input.getText().trim();
				if (str.length() <= 0) {
					view(null);
					BGMonkey.output.warn("请先输入待查询的单词或短句！");
					return;
				}
				keyword = URLEncoder.encode(str, "UTF-8");
				view(URL_BASE.replace(MASK, keyword));
			} catch (Exception e) {
				view(null);
				BGMonkey.output.error(e);
			}
		}

		public void view(final String url) {
			try {
				SwingUtilities.invokeLater(new Thread() {
					@Override
					public void run() {
						if (url == null) {
							cardLayout.show(rightPanel, "text");
						} else {
							DicButton.this.url = url;
							parseHTML(url);
							cardLayout.show(rightPanel, "browser");
						}
					}
				});
			} catch (Exception e) {
				BGMonkey.output.error(e);
			}
		}

		public void parseHTML(final String url) {
			browser.navigate(url);
		}

		public void parseHTML(final Element doc, final Element selected, boolean enableJS) {
			Elements srcs = doc.getElementsByAttributeValueStarting("src", "/");
			for (Element src : srcs) {
				// System.out.println(src);
				src.attributes().put("src", url.substring(0, url.lastIndexOf('/')) + src.attr("src"));
			}
			Element head = doc.getElementsByTag("head").first();
			Elements css = head.getElementsByTag("link");
			css.addAll(head.getElementsByTag("style"));
			StringBuilder html = new StringBuilder();
			for (Element s : css) {
				html.append(s.outerHtml());
			}
			if (enableJS) {
				Elements jss = head.getElementsByAttributeValue("type", "text/javascript");
				for (Element js : jss) {
					html.append(js.outerHtml());
				}
			}
			selected.attributes().put("width", "100%");
			html.append(selected.outerHtml());
			browser.setHTMLContent(html.toString());
		}
	}

	// public static String translate(String input) {
	// try {
	// String url =
	// "http://fanyi.youdao.com/openapi.do?keyfrom=DevGeek&key=1071523943&type=data&doctype=xml&version=1.1&q="
	// + URLEncoder.encode(input, "UTF-8");
	// String xml = BGMonkey.client.get(url);
	// System.out.println(xml);
	// if (xml != null) {
	// Document doc = Jsoup.parse(xml);
	// return doc.text();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return "";
	// }
}
