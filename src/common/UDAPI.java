package common;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class UDAPI {
	private final static String API_PATH = "http://wap.unidust.cn/api/searchout.do?type=wap&ch=1001";
	public final static TreeMap<String, Integer> APPID = new TreeMap<String, Integer>();
	static {
		APPID.put("天气", 71);// 为空则查本地天气，输入城市名字则查该城市的天气
		APPID.put("公交", 11);// 本城市三种方式：某地到某地 or 某路线
							// or某站点(如查其他城市，请增加输入城市名称，如北京21路)
		APPID.put("列车", 21);// 三种方式：火车某地到某地 or 火车某车次 or 火车某站点
		APPID.put("(列车)余票", 791);// 两种方式：某天某地到某地 or 某天某车次
		APPID.put("长途客车", 401);// 某城市到某城市
		APPID.put("百科", 111);// 某内容
		APPID.put("提问", 121);// 某内容
		APPID.put("翻译", 41);// 某内容
		APPID.put("词典", 431);// 某内容
		APPID.put("诗词", 531);// 某内容(如任意诗词中任意词、句或者作者)
		APPID.put("归属", 101);// 某内容(支持手机号、身份证所在地、邮编、区号等)
		APPID.put("电视节目预告", 691);// 电视台
		APPID.put("彩票开奖", 311);// 为空
		APPID.put("笑话", 61);// 类型(夫妻, 经典, 爱情, 校园, 愚人, 恶心, 冷……)
		APPID.put("短信", 131);// 类型(晚安, 早安, 起床, 爱情, 情人, 煽情等60多类)
		APPID.put("故事", 381);// 类型(恐怖、励志、爱情、童话、神话等多种)
		APPID.put("IN语", 731);// 为空
		APPID.put("QQ状态、资料", 641);// QQ号码
		APPID.put("凶吉", 471);// 内容(姓名、号码)
		APPID.put("歌词", 591);// 歌名
		APPID.put("配对", 601);// 姓名配姓名or属相配属相or星座配星座
		APPID.put("运势", 81);// 星座 or 属相(时间)
		APPID.put("附近", 51);// 某城市某地附近某事项
	}

	public static String getResult(String question,int appid) {
		String strResult = null;
		try {
			Main.client.clearInput();
			Main.client.addInput("info", question);
			strResult = Main.client.post(API_PATH+"&appid="+appid,true);
			System.out.println(strResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("UDAPI Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setLocationRelativeTo(null);
		final JTextField ques = new JTextField();
		final JTextPane ans = new JTextPane();
		final JPanel head = new JPanel(new BorderLayout());
		final JComboBox type = new JComboBox();
		for(String name : APPID.keySet()){
			type.addItem(name);
		}
		ans.setContentType("text/html");
		head.add(ques);
		head.add(type, BorderLayout.EAST);
		frame.add(head, BorderLayout.NORTH);
		frame.add(new JScrollPane(ans));
		ques.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					ans.setText(getResult(ques.getText(),APPID.get(type.getSelectedItem())));
				}
			}
		});
		ans.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				try {
					ans.setText(Main.client.get(e.getURL().toExternalForm()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.setVisible(true);
	}
}
