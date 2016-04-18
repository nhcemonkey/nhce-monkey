package nhmonkey;

import java.awt.Desktop;
import java.awt.Dimension;
import java.net.URI;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.http.cookie.Cookie;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;

public class Browser extends JWebBrowser {

	private static final long serialVersionUID = -6486403936860559931L;

	public Browser() {
		super();
		setMinimumSize(new Dimension(100, 220));
		try {
			if (!SwingUtilities.isEventDispatchThread())
				SwingUtilities.invokeAndWait(new Thread() {
					@Override
					public void run() {
						setDefaultPopupMenuRegistered(true);
					}
				});
			else
				setDefaultPopupMenuRegistered(true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		setBarsVisible(false);
		addWebBrowserListener(new WebBrowserAdapter() {
			@Override
			public void windowWillOpen(WebBrowserWindowWillOpenEvent e) {
				e.getNewWebBrowser().addWebBrowserListener(new WebBrowserAdapter() {
					@Override
					public void locationChanging(WebBrowserNavigationEvent e) {
						final JWebBrowser webBrowser = e.getWebBrowser();
						webBrowser.removeWebBrowserListener(this);
						String newResourceLocation = e.getNewResourceLocation();
						e.consume();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								webBrowser.getWebBrowserWindow().dispose();
							}
						});
						try {
							Desktop desktop = Desktop.getDesktop();
							if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
								URI uri = new URI(newResourceLocation);
								desktop.browse(uri);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				});
			}
		});
	}

	public void copyCookiesFrom(Client client, String host) {
		List<Cookie> cs = client.getCookies();
		for (Cookie c : cs) {
			JWebBrowser.setCookie(host,
					c.getName() + "=" + c.getValue() + "; path=" + c.getPath() + "; domain=" + c.getDomain());
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.stopLoading();
	}
}
