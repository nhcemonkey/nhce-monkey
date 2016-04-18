package nhmonkey;

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
		try {
			PopupMenu popup = new PopupMenu();
			MenuItem menuExit = new MenuItem("Exit " + NHMonkey.nm.getTitle());
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
						NHMonkey.inTray = false;
						NHMonkey.nm.setExtendedState(Frame.NORMAL);
						NHMonkey.nm.setVisible(true);
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

			Image image = NHMonkey.getIconRes("logo.png", 0).getImage();// 获取一个图标对象
			trayIcon = new TrayIcon(image, NHMonkey.nm.getTitle(), popup);
			trayIcon.setImageAutoSize(true);
			final ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SystemTray.getSystemTray().remove(trayIcon);
					NHMonkey.inTray = false;
					NHMonkey.nm.setExtendedState(Frame.NORMAL);
					NHMonkey.nm.setVisible(true);
				}
			};
			trayIcon.addActionListener(al);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void hideToTray() {
		if (SUPPORTED) {
			try {
				SystemTray tray = SystemTray.getSystemTray();
				tray.add(trayIcon);
				NHMonkey.inTray = true;
				NHMonkey.nm.setVisible(false);
				if (NHMonkey.config.config.forbidTrayPop)
					trayIcon.displayMessage(NHMonkey.nm.getTitle(), "我“默默地”潜伏在此处！", TrayIcon.MessageType.INFO);
				else
					trayIcon.displayMessage(NHMonkey.nm.getTitle(), "我潜伏在此处！", TrayIcon.MessageType.INFO);
			} catch (AWTException e) {
				NHMonkey.output.error("不支持托盘功能！");
			}
		} else {
			NHMonkey.output.error("不支持托盘功能！");
		}
	}

	public void showInfo(String str) {
		if (!NHMonkey.config.config.forbidTrayPop)
			trayIcon.displayMessage(NHMonkey.nm.getTitle(), str, TrayIcon.MessageType.INFO);
	}
}
