package common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class Client {

	DefaultHttpClient httpclient;
	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	String url;
	public String charset = HTTP.UTF_8;
	private ArrayList<DownloadListener> downloadListener = new ArrayList<DownloadListener>();// Added
																								// //
																								// in
																								// 1.2

	public Client() {
		ThreadSafeClientConnManager conMgr = new ThreadSafeClientConnManager();
		conMgr.setMaxTotal(100);
		httpclient = new DefaultHttpClient(conMgr);
		TrustManager easyTrustManager = new X509TrustManager() {

			public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
					throws java.security.cert.CertificateException {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
					throws java.security.cert.CertificateException {
			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
			SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
			Scheme sch = new Scheme("https", 443, sf);
			httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// httpclient.getParams().setIntParameter("http.socket.timeout",10000);//延时设定
		// 伪装成Win7(32bit)下的IE8
		httpclient.getParams().setParameter(HttpProtocolParams.USER_AGENT,
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");
	}

	public Client(String charset) {
		this();
		this.charset = charset;
	}

	public void post(String url) throws Exception {
		System.out.println("[Client] Post to " + url);
		this.url = url;
		HttpPost httpost = new HttpPost(url);
		httpost.setEntity(new UrlEncodedFormEntity(nvps, charset));
		HttpResponse response = httpclient.execute(httpost);
		HttpEntity entity = response.getEntity();
		if (entity != null)
			EntityUtils.consume(entity);
	}

	public String post(String url, boolean read) throws Exception {
		if (!read) {
			post(url);
			return "";
		} else {
			System.out.println("[Client] Post to " + url);
			this.url = url;
			HttpPost httpost = new HttpPost(url);
			httpost.setEntity(new UrlEncodedFormEntity(nvps, charset));
			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			StringBuffer output = new StringBuffer();
			if (entity != null) {
				InputStream instream = entity.getContent();
				int l;
				byte[] tmp = new byte[2048];
				while ((l = instream.read(tmp)) != -1) {
					output.append(new String(tmp, 0, l, charset));
				}
			}
			if (entity != null)
				EntityUtils.consume(entity);
			return output.toString();
		}
	}

	public void post(String url, String charset) throws Exception {
		String temp = this.charset;
		this.charset = charset;
		post(url);
		this.charset = temp;
	}

	public String get(String url) throws ClientProtocolException, IOException {
		System.out.println("[Client] Get from " + url);
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		StringBuffer output = new StringBuffer();
		if (entity != null) {
			InputStream instream = entity.getContent();
			int l;
			byte[] tmp = new byte[2048];
			while ((l = instream.read(tmp)) != -1) {
				output.append(new String(tmp, 0, l, charset));
			}
		}
		if (entity != null)
			EntityUtils.consume(entity);
		return output.toString();
	}

	public String get(String url, String charset) throws ClientProtocolException, IOException {
		String temp = this.charset;
		this.charset = charset;
		String data = get(url);
		this.charset = temp;
		return data;
	}

	public void addInput(String name, String value) {
		nvps.add(new BasicNameValuePair(name, value));
	}

	public void clearInput() {
		nvps.clear();
	}

	public String getURL() {
		return url;
	}

	public List<NameValuePair> getInputs() {
		return nvps;
	}

	public String getCookieByName(String name) {
		String value = null;
		List<Cookie> cookies = httpclient.getCookieStore().getCookies();
		if (!cookies.isEmpty()) {
			for (int i = 0; i < cookies.size(); i++) {
				if (cookies.get(i).getName().equals(name)) {
					value = cookies.get(i).getValue();
					break;
				}
			}
		}
		return value;
	}

	public List<Cookie> getCookies() {
		return httpclient.getCookieStore().getCookies();
	}

	@Override
	public void finalize() {
		// 是否安全？？？
		if (httpclient != null && httpclient.getConnectionManager() != null)
			httpclient.getConnectionManager().shutdown();

	}

	public void addDownloadListener(DownloadListener listener) {
		downloadListener.add(listener);
	}

	public void clearDownloadListener() {
		downloadListener.clear();
	}

	public void removeDownloadListener(DownloadListener listener) {
		downloadListener.remove(listener);
	}

	public interface DownloadListener {
		public void downloadStateChanged(int down, int total);
	}
}
