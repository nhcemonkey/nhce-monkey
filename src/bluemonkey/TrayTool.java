package bluemonkey;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrayTool {
	TrayIcon trayIcon;// TrayIcon是一个预定义的类
	final boolean SUPPORTED = SystemTray.isSupported();

	public TrayTool() {
		if (!SUPPORTED) {
			return;
		}
		try{
		PopupMenu popup = new PopupMenu();
		MenuItem menuExit = new MenuItem("Exit "+BlueMonkey.bm.getTitle());
		MenuItem menuShow = new MenuItem("Show Main Window");
		MenuItem menuCancel = new MenuItem("Cancle");
		// 创建退出菜单监听器
		ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		// 创建打开监听器
		ActionListener showListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					SystemTray.getSystemTray().remove(trayIcon);
					BlueMonkey.inTray = false;
					BlueMonkey.bm.setExtendedState(Frame.NORMAL);
					BlueMonkey.bm.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		menuExit.addActionListener(exitListener);
		menuShow.addActionListener(showListener);
		popup.add(menuShow);
		popup.add(menuCancel);
		popup.add(menuExit);

		Image image = BlueMonkey.getIconRes("logo.png", 0).getImage();// 获取一个图标对象
		trayIcon = new TrayIcon(image, BlueMonkey.bm.getTitle(), popup);
		trayIcon.setImageAutoSize(true);
		final ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SystemTray.getSystemTray().remove(trayIcon);
				BlueMonkey.inTray = false;
				BlueMonkey.bm.setExtendedState(Frame.NORMAL);
				BlueMonkey.bm.setVisible(true);
			}
		};
		trayIcon.addActionListener(al);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void hideToTray() {
		if (SUPPORTED) {
			try {
				SystemTray tray = SystemTray.getSystemTray();
				tray.add(trayIcon);
				BlueMonkey.inTray = true;
				BlueMonkey.bm.setVisible(false);
				showInfo("我潜伏在此处！");//
			} catch (AWTException e) {
				BlueMonkey.output.error("不支持托盘功能！");
			}
		} else {
			BlueMonkey.output.error("不支持托盘功能！");
		}
	}
	
	public void showInfo(String str){
		trayIcon.displayMessage(BlueMonkey.bm.getTitle(), str, TrayIcon.MessageType.INFO);
	}
}