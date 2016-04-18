package bingomonkey;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

import org.tool.LanguageTool;

import bingomonkey.Editor.Word;

public class CheckPanel extends JPanel {
	private static final long serialVersionUID = -5677557816301191800L;

	JPanel bottomPanel;
	JPanel mainPanel;
	JButton checkBtn;
	JTextPane spellCheckPane, grammarCheckPane;
	public int spellCheckWrong = 0, grammarCheckWrong = 0;

	public CheckPanel() {
		setLayout(new BorderLayout());

		bottomPanel = new JPanel();
		mainPanel = new JPanel(new GridLayout(1, 0));
		checkBtn = new JButton("Check");
		spellCheckPane = new JTextPane();
		spellCheckPane.setContentType("text/html");
		spellCheckPane.setEditable(false);
		grammarCheckPane = new JTextPane();
		grammarCheckPane.setContentType("text/html");
		grammarCheckPane.setEditable(false);

		JScrollPane scroll = new JScrollPane(spellCheckPane);
		scroll.setBorder(new TitledBorder("Spell Check"));
		;
		mainPanel.add(scroll);
		scroll = new JScrollPane(grammarCheckPane);
		scroll.setBorder(new TitledBorder("Grammar Check"));
		;
		mainPanel.add(scroll);
		bottomPanel.add(checkBtn);
		add(mainPanel);
		add(bottomPanel, BorderLayout.SOUTH);

		// grammarCheckPane.getDocument().addDocumentListener(new
		// DocumentListener() {
		//
		// @Override
		// public void removeUpdate(DocumentEvent e) {
		// }
		//
		// @Override
		// public void insertUpdate(DocumentEvent e) {
		// if (grammarCheckPane.getText().contains("</b>")) {
		// BGMonkey.bgm.infoPanel.setSelectedIndex(2);
		// System.err.println("Grammar Wrong!");
		// }
		// }
		//
		// @Override
		// public void changedUpdate(DocumentEvent e) {
		// }
		// });

		checkBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				grammarCheckWrong = LanguageTool.getInstance().checkTextAndDisplayResults();
				spellCheckWrong = checkSpell();
				if (spellCheckWrong > 0 && grammarCheckWrong > 0) {
					BGMonkey.bgm.infoPanel.setSelectedIndex(2);
					BGMonkey.output.warn("发现有" + spellCheckWrong + "个疑似拼写错误和" + grammarCheckWrong + "个疑似语法错误！");
				} else if (spellCheckWrong <= 0 && grammarCheckWrong > 0) {
					BGMonkey.bgm.infoPanel.setSelectedIndex(2);
					BGMonkey.output.warn("发现有" + grammarCheckWrong + "个疑似语法错误！");
				} else if (spellCheckWrong > 0) {
					BGMonkey.bgm.infoPanel.setSelectedIndex(2);
					BGMonkey.output.warn("发现有" + spellCheckWrong + "个疑似拼写错误！");
				}
			}
		});
	}

	public int checkSpell() {
		StringBuilder sb = new StringBuilder();
		int count = 1;
		boolean empty = true;
		boolean supported = true;
		try {
			for (Word word : BGMonkey.bgm.editor.getWords()) {
				empty = false;
				List<String> result = HunspellMain.checkWord(word.getWord());
				if (result != null && result.size() > 0) {
					sb.append(count + ".<b>可疑单词</b>：<font color=\'red\'>" + word.getWord()
							+ "</font><br>&nbsp;&nbsp;&nbsp;&nbsp;<b>候选单词</b>：");
					for (String sgw : result)
						sb.append(sgw + "&nbsp;&nbsp;&nbsp;&nbsp;");
					sb.append("<br><br>");
					count++;
				}
			}
		} catch (Throwable e1) {
			supported = false;
			e1.printStackTrace();
		}
		if (!supported)
			sb.append("<html><font color=\'red\'>暂不支持您的操作系统！</font></html>");
		else if (empty)
			sb.append(LanguageTool.HTML_GREY_FONT_START + "没有输入任何文本！" + LanguageTool.HTML_FONT_END);
		else if (count <= 1)
			sb.append(LanguageTool.HTML_GREY_FONT_START + "没有检测出可疑单词！" + LanguageTool.HTML_FONT_END);

		spellCheckPane.setText(sb.toString());
		spellCheckPane.setCaretPosition(0);
		return count - 1;
	}

}
