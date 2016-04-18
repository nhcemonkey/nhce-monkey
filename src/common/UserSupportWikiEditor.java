package common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UserSupportWikiEditor extends JFrame{

	public UserSupportWikiEditor(){
		super("User Support Wiki Editor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(700, 638);
		setLocationRelativeTo(null);
		//Main.setLookAndFeel(this);
		loadFrame();
	}
	
	private void loadFrame(){
		JPanel topPanel = new JPanel(new GridLayout(0,2));
		topPanel.add(new JLabel("序号"));
		final JTextField id = new JTextField(30);
		topPanel.add(id);	
		topPanel.add(new JLabel("Monkey"));
		final JComboBox product = new JComboBox(new String[]{"NHCE","Lange","Bingo"});
		topPanel.add(product);
		topPanel.add(new JLabel("类别"));
		final JComboBox type = new JComboBox(new String[]{"Bug","建议","其他"});
		topPanel.add(type);
		topPanel.add(new JLabel("发送时间"));
		final JTextField time = new JTextField(10);
		topPanel.add(time);	
		topPanel.add(new JLabel("主要内容"));
		final JTextField content = new JTextField(30);
		topPanel.add(content);	
		topPanel.add(new JLabel("署名"));
		final JTextField name = new JTextField(10);
		topPanel.add(name);
		topPanel.add(new JLabel("回复"));
		final JTextField reply = new JTextField(30);
		topPanel.add(reply);
		topPanel.add(new JLabel("状态"));
		final JComboBox state = new JComboBox(new String[]{"","未知","已解决","未解决","已采纳","未采纳"});
		topPanel.add(state);
		topPanel.add(new JLabel("备注"));
		final JTextField ps = new JTextField(10);
		topPanel.add(ps);
		
		add(topPanel,BorderLayout.NORTH);
		
		JPanel midPanel = new JPanel();
		JPanel botPanel = new JPanel();
		final JTextArea code = new JTextArea();
		JScrollPane pane = new JScrollPane(code);
		pane.setPreferredSize(new Dimension(500, 300));
		midPanel.add(pane);
		JButton btn = new JButton("生成代码");
		JButton clearBtn = new JButton("清除代码");	
		botPanel.add(btn);
		botPanel.add(clearBtn);
		add(botPanel);
		add(midPanel,BorderLayout.SOUTH);
		
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					int idNum = Integer.parseInt(id.getText());
					id.setText(idNum+1+"");
					String date = dateFormat.format(new Date());
					if(name.getText().length()<= 0)
						name.setText(" ");
					code.append("||"+idNum + "||"+ product.getSelectedItem() + "||" +type.getSelectedItem()+ "||" + time.getText() + "||" + content.getText()+"||"+name.getText()+"||" + date + "||" + reply.getText() + "||" + state.getSelectedItem()+ "||"+ps.getText() + "||\n");
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(code.getText()), null);
				}catch(Exception e){
					JOptionPane.showMessageDialog(UserSupportWikiEditor.this, e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		clearBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			 code.setText("");	
			}
		});
		
		this.pack();
	}
	
	public static void main(String[] args) {
		new UserSupportWikiEditor().setVisible(true);
	}

}
