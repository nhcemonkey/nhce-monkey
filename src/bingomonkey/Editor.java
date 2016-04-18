package bingomonkey;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Editor extends JPanel {

	private static final long serialVersionUID = -6146999146266171423L;
	JTextArea area;
	JPanel statePanel;
	JLabel wordCount;
	int wordMin = 0;
	boolean wordEnough = false;
	public final static String TEMP_SOTRE_DIR = Recorder.TEMP_DIR + "doc"
			+ File.separator;
	File tempStoreDir;

	public Editor() {
		super(new BorderLayout());
		area = new JTextArea();
		add(new JScrollPane(area));
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		Font font = new Font("Microsoft Yahei", Font.PLAIN, 20);
		if (font != null)
			area.setFont(font);
		add(statePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0)), BorderLayout.SOUTH);
		statePanel.add(wordCount = new JLabel("Word Count: 未统计"));
		statePanel.add(new JLabel("<html>(提示：选定文本后，按<font color=\'blue\'>Alt键</font>可查询词典)</html>"));
		area.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				clean();
				char ch = e.getKeyChar();
				if (Character.isLetter(ch)) {
					wordCount(area.getText() + ch);
				} else
					wordCount(area.getText());
				if (ch == ' ' || ch == '\n')
					safeStore();
			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ALT) {
					String target = area.getSelectedText();
					if (target != null) {
						ToolPanel.lookUp(target);
					}
					e.consume();
				}
			}
		});
		tempStoreDir = new File(TEMP_SOTRE_DIR);
		if (!tempStoreDir.exists())
			tempStoreDir.mkdirs();
		recoverSafeStore();
	}

	public void recoverSafeStore() {
		try {
			File[] list = tempStoreDir.listFiles();
			if (list.length > 0) {
				File doc = list[list.length - 1];
				DataInputStream output = new DataInputStream(new FileInputStream(doc));
				area.setText(output.readUTF());
				wordCount(area.getText());
			}
		} catch (Exception e) {
			BGMonkey.output.error(e);
		}
	}

	public void safeStore() {
		try {
			File[] list = tempStoreDir.listFiles();
			if (list.length > 20)
				list[0].delete();
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			File doc = new File(TEMP_SOTRE_DIR + timestamp + ".tmp");
			doc.createNewFile();
			DataOutputStream output = new DataOutputStream(new FileOutputStream(doc));
			output.writeUTF(area.getText());
		} catch (Exception e) {
			BGMonkey.output.error(e);
		}
	}

	public void wordCount(String text) {
		int count = (text + ' ').split("\\b\\w+\\b").length - 1;
		if (wordMin <= 0)
			wordCount.setText("Word Count: " + count);
		else if (count < wordMin) {
			wordCount.setText("<html>Word Count: <font color=\'red\'>" + count + "/" + wordMin + "</font></html>");
			wordEnough = false;
		} else {
			wordCount.setText("<html>Word Count: <font color=\'blue\'>" + count + "/" + wordMin + "</font></html>");
			wordEnough = true;
		}
	}

	public String getContent() {
		clean();
		return area.getText();
	}

	public ArrayList<Word> getWords() {
		String content = BGMonkey.bgm.editor.getContent();
		ArrayList<Word> words = new ArrayList<Word>();
		if (content.length() <= 0)
			return words;
		StringTokenizer tokenizer = new StringTokenizer(content);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			int head = 0;
			int tail = token.length();
			if (!Character.isLetter(token.charAt(tail - 1)))
				tail -= 1;
			if (!Character.isLetter(token.charAt(head)))
				head += 1;
			if (head >= 0 && tail >= head) {
				Word word = new Word(token.substring(head, tail));
				int index = words.indexOf(word);
				if (index == -1) {
					word.increase();
					words.add(word);
				} else
					words.get(index).increase();
			}
		}
		return words;
	}

	public void clean() {// only for english!
		int pos = area.getCaretPosition();
		String content = area.getText();
		content = content.replace('，', ',');
		content = content.replace('。', '.');
		content = content.replace('：', ':');
		content = content.replace('？', '?');
		content = content.replace('；', ';');
		content = content.replace('‘', '\'');
		content = content.replace('’', '\'');
		content = content.replace('“', '\"');
		content = content.replace('”', '\"');
		content = content.replace('（', '(');
		content = content.replace('）', ')');
		content = content.replace('！', '!');
		area.setText(content);
		area.setCaretPosition(pos);
	}

	public void setContent(String text) {
		if (area.getText().length() > 0) {
			int reply = JOptionPane.showConfirmDialog(BGMonkey.bgm, "是否覆盖编辑器中现有内容以显示新的内容？", "注意",
					JOptionPane.YES_NO_OPTION);
			if (reply != JOptionPane.YES_OPTION)
				return;
		}
		area.setText(text);
		area.setCaretPosition(0);
		wordCount(area.getText());
	}

	public class Word implements Comparable<Word> {
		private String word;
		private int occurrence;

		public Word(String w) {
			assert w != null;
			word = w;
		}

		public String getWord() {
			return this.word;
		}

		public int getOccurrence() {
			return this.occurrence;
		}

		public void increase() {
			occurrence++;
		}

		public boolean equals(Object another) {
			if (another == null)
				return false;
			if (another instanceof Word) {
				Word anotherWord = (Word) another;
				return anotherWord.getWord().equals(word);
			} else
				return false;
		}

		public int hashCode() {
			return word.hashCode();
		}

		public int compareTo(Word w) {
			if (occurrence < w.getOccurrence())
				return -1;
			else if (occurrence == w.getOccurrence())
				return 0;
			else
				return 1;
		}
	}
}
