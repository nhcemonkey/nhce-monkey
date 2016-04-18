package bluemonkey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.http.Header;
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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.tool.ThunderTool;
import org.tool.ThunderTool.TDListener;

public class Client {

	DefaultHttpClient httpclient;
	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	String url;
	public String charset = HTTP.UTF_8;
	private ArrayList<DownloadListener> downloadListener = new ArrayList<DownloadListener>();// Added
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

	public void downloadSliently(String url, String dir, String fileName) throws IOException {
		downloadSliently(url, dir, fileName, false);
	}

	/**
	 * Added in 2.1 for downloading data packages silently Modified in 2.4-6 for
	 * Thunder Download Module
	 * 
	 * @param url
	 * @param dir
	 * @throws IOException
	 */
	public void downloadSliently(String url, String dir, String fileName, boolean useThunder) throws IOException {
		if (useThunder) {
			//Added in 2.5-1
			String tdTempName = dir + fileName+".tmp.td";
			File tdTempFile = new File(tdTempName);
			File tdTempConfigFile = new File(tdTempName+".cfg");
			if(tdTempFile.exists())
				tdTempFile.delete();
			if(tdTempConfigFile.exists())
				tdTempConfigFile.delete();
			
			boolean TDOk = false;
			try {

				ThunderTool.clearListeners();
				ThunderTool.addListener(new TDListener() {

					@Override
					public void stateChanged(double down, double total) {
						for (DownloadListener lis : downloadListener)
							lis.downloadStateChanged((int) down, (int) total);
					}
				});
				ThunderTool.TunderDownload(url, dir + fileName);
				TDOk = true;
			} catch (Throwable e) {
				System.out.println(e.getLocalizedMessage());
			}
			if (TDOk)
				return;
		}
		System.out.println("[Client] Download from " + url);
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// String fileName = "";
			// Header[] headers = response.getHeaders("Content-Disposition");
			// if (headers.length > 0) {
			// String value = headers[0].getValue();
			// value = value.substring(value.indexOf("filename=") + 9).trim();
			// value = value.substring(1, value.length() - 1).trim();
			// fileName = new String(value.getBytes("ISO8859-1"), "GB2312");
			// }
			// JFileChooser fileChooser = new
			// JFileChooser(System.getProperty("user.dir"));
			// fileChooser.setSelectedFile(new
			// File(System.getProperty("user.dir") + '\\' + fileName));
			// fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			// if (fileChooser.showSaveDialog(NHMonkey.nh) ==
			// JFileChooser.APPROVE_OPTION) {
			// File saveDir = fileChooser.getSelectedFile();
			// File file = fileChooser.getSelectedFile();
			// if (!file.exists()
			// || JOptionPane.showConfirmDialog(NHMonkey.nh, file.getName() +
			// "已存在！\n是否要覆盖原文件？", "请选择",
			// JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (fileName == null || fileName.length() <= 0) {
				Header[] headers = response.getHeaders("Content-Disposition");
				if (headers.length > 0) {
					String value = headers[0].getValue();
					value = value.substring(value.indexOf("filename=") + 9).trim();
					value = value.substring(1, value.length() - 1).trim();
					fileName = new String(value.getBytes("ISO8859-1"), "GB2312");
				} else
					throw new IllegalArgumentException("File name not set!");
			}

			int size = 0;
			try {
				size = Integer.parseInt(response.getHeaders("Content-Length")[0].getValue());
				System.out.println("[Client] Download File Size: " + size + " Byte");
			} catch (Exception e) {
			}
			File dirFile = new File(dir);
			if (!dirFile.exists())
				dirFile.mkdirs();
			fileName = new File(dir + fileName).getCanonicalPath();
			System.out.println("[Client] Download started. File:" + fileName);
			FileOutputStream output = new FileOutputStream(fileName + ".tmp");
			InputStream instream = entity.getContent();
			int l = 0, down = 0;
			byte[] tmp = new byte[2048];
			boolean interrupted = false;
			while ((l = instream.read(tmp)) != -1) {
				output.write(tmp, 0, l);
				down += l;
				for (DownloadListener lis : downloadListener)
					lis.downloadStateChanged(down, size);
				if (!BlueMonkey.working) {
					interrupted = true;
					break;
				}
			}
			output.close();

			File tempFile = new File(fileName + ".tmp");
			if (interrupted) {
				tempFile.delete();
			} else {

				File newFile = new File(fileName);
				if (tempFile.exists()) {
					if (newFile.exists())
						newFile.delete();
					tempFile.renameTo(newFile);
				}
			}
			// }
			// }
		}
		if (entity != null)
			EntityUtils.consume(entity);
	}

	public void download(String url) throws ClientProtocolException, IOException {
		System.out.println("[Client] Download from " + url);
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String fileName = "";
			Header[] headers = response.getHeaders("Content-Disposition");
			if (headers.length > 0) {
				String value = headers[0].getValue();
				value = value.substring(value.indexOf("filename=") + 9).trim();
				value = value.substring(1, value.length() - 1).trim();
				fileName = new String(value.getBytes("ISO8859-1"), "GB2312");
			}
			int size = 0;
			try {
				size = Integer.parseInt(response.getHeaders("Content-Length")[0].getValue());
				System.out.println("[Client] Download File Size: " + size + " Byte");
			} catch (Exception e) {
			}
			JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
			fileChooser.setSelectedFile(new File(System.getProperty("user.dir") + '\\' + fileName));
			// fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showSaveDialog(BlueMonkey.bm) == JFileChooser.APPROVE_OPTION) {
				// File saveDir = fileChooser.getSelectedFile();
				File file = fileChooser.getSelectedFile();
				if (!file.exists()
						|| JOptionPane.showConfirmDialog(BlueMonkey.bm, file.getName() + "已存在！\n是否要覆盖原文件？", "请选择",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					fileName = file.getCanonicalPath();
					System.out.println("[Client] Download started. File:" + fileName);
					FileOutputStream output = new FileOutputStream(fileName + ".tmp");
					InputStream instream = entity.getContent();
					int l = 0, down = 0;
					byte[] tmp = new byte[2048];
					boolean interrupted = false;
					while ((l = instream.read(tmp)) != -1) {
						output.write(tmp, 0, l);
						down += l;
						for (DownloadListener lis : downloadListener)
							lis.downloadStateChanged(down, size);
						if (!BlueMonkey.working) {
							interrupted = true;
							break;
						}
					}
					output.close();

					File tempFile = new File(fileName + ".tmp");
					if (interrupted) {
						tempFile.delete();
					} else {

						File newFile = new File(fileName);
						if (tempFile.exists()) {
							if (newFile.exists())
								newFile.delete();
							tempFile.renameTo(newFile);
						}
					}
				}
			}
		}
		if (entity != null)
			EntityUtils.consume(entity);
	}

	public void upload(String url, String nameOfFile, File file) throws IOException {
		System.out.println("[Client] Upload to " + url);
		this.url = url;
		HttpPost httpost = new HttpPost(url);
		MultipartEntity multiEntity = new MultipartEntity();
		for (NameValuePair nv : nvps)
			multiEntity.addPart(nv.getName(), new StringBody(nv.getValue()));
		multiEntity.addPart(nameOfFile, new FileBody(file));
		httpost.setEntity(multiEntity);
		HttpResponse response = httpclient.execute(httpost);
		HttpEntity entity = response.getEntity();
		if (entity != null)
			EntityUtils.consume(entity);
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
