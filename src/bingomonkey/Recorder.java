package bingomonkey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.thoughtworks.xstream.XStream;

public class Recorder {
	private static XStream xStream;
	private File file;
	private Reader reader;
	private FileWriter writer;
	final public String ANS_RECORD_TITLE = "<!-- Answer Record of " + VersionPanel.NAME + " V" + VersionPanel.VERSION
			+ "-->\n";
	final public String USER_INFO_TITLE = "<!-- User Info of " + VersionPanel.NAME + " V" + VersionPanel.VERSION
			+ " -->\n";
	final public String CONFIG_INFO_TITLE = "<!-- Configuration Info of " + VersionPanel.NAME + " V"
			+ VersionPanel.VERSION + " -->\n";
	final public static String TEMP_DIR = System.getenv("APPDATA")+'\\'+ "BingoHack" + File.separator;

	public Recorder() {
		// System.out.println(DIR_PATH+"rec");
		xStream = new XStream();
	}





	public void writeUserInfo(String school,String classID,String name,String stdID,String classUrl) {
		try {
			File dir = new File(TEMP_DIR);
			if (!dir.exists())
				dir.mkdirs();
			file = new File(TEMP_DIR + "user.xml");
			if (file == null)
				System.out.println("User info file create failure!");//Modified in 2.2.1
			writer = new FileWriter(file);
			writer.write(USER_INFO_TITLE);
			writer.write("<!-- Generated in " + new Date().toString() + " -->\n\n");
			xStream.toXML(new UserInfo(school,classID,name,stdID,classUrl), writer);
			writer.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public UserInfo readUserInfo() throws FileNotFoundException, IOException {
		File infoFile = new File(TEMP_DIR + "user.xml");
		if (!infoFile.exists())
			return null;
		// System.out.println("Parsing user info file...");
		reader = new FileReader(infoFile);
		char[] cbuf = new char[USER_INFO_TITLE.length()];
		reader.read(cbuf, 0, USER_INFO_TITLE.length());
		UserInfo info = null;
		String str = new String(cbuf);
		if (str.substring(0, str.lastIndexOf('.')).equals(
				USER_INFO_TITLE.substring(0, USER_INFO_TITLE.lastIndexOf('.')))) {
			info = (UserInfo) xStream.fromXML(reader);
			// System.out.println(rec);
		} else
			System.out.println("User info file mismatch!");//Modified in 2.2.1
		reader.close();
		return info;
	}

	public void writeConfigInfo(ConfigInfo config) {
		try {
			File dir = new File(TEMP_DIR);
			if (!dir.exists())
				dir.mkdirs();
			file = new File(TEMP_DIR + "config.xml");
			if (file == null)
				System.out.println("Config info file create failure!");//Modified in 2.2.1
			writer = new FileWriter(file);
			writer.write(CONFIG_INFO_TITLE);
			writer.write("<!-- Generated in " + new Date().toString() + " -->\n\n");
			xStream.toXML(config, writer);
			writer.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public ConfigInfo readConfigInfo() throws FileNotFoundException, IOException {
		File infoFile = new File(TEMP_DIR + "config.xml");
		if (!infoFile.exists())
			return null;
		// System.out.println("Parsing user info file...");
		reader = new FileReader(infoFile);
		char[] cbuf = new char[CONFIG_INFO_TITLE.length()];
		reader.read(cbuf, 0, CONFIG_INFO_TITLE.length());
		ConfigInfo info = null;
		String str = new String(cbuf);
		if (str.substring(0, str.lastIndexOf('.')).equals(
				CONFIG_INFO_TITLE.substring(0, CONFIG_INFO_TITLE.lastIndexOf('.')))) {
			info = (ConfigInfo) xStream.fromXML(reader);
			// System.out.println(rec);
		} else
			System.out.println("Config info file mismatch!");//Modified in 2.2.1
		reader.close();
		return info;
	}

	/**
	 * Modified in 2.1 --- Add return type
	 * @param zFile
	 * @param dir
	 * @throws IOException
	 */
	public int extractFile(ZipFile zFile, String dir) throws IOException { // 解压文件

		Enumeration<? extends ZipEntry> enu = zFile.entries();
		int count = 0;
		while (enu.hasMoreElements()) { // 依次枚举条目
			ZipEntry entry = (ZipEntry) enu.nextElement(); // 得到压缩条目
			// entry.getComment();
			// entry.getCompressedSize();
			// entry.getCrc();
			// entry.isDirectory();
			// entry.getSize();
			// entry.getMethod();
			File file = new File(dir + '/' + entry.getName()); // 实例化文件对象
			if (entry.isDirectory()) {
				if (!file.exists())
					file.mkdirs();
			} else {
				InputStream in = zFile.getInputStream(entry); // 得到文件输入流
				FileOutputStream out = new FileOutputStream(file); // 得到文件输出流
				byte[] buffer = new byte[1024]; // 缓冲区大小
				int length;
				while ((length = in.read(buffer)) != -1) { // 循环读取数据
					out.write(buffer, 0, length); // 写数据到输出流
				}
				count++;
				in.close(); // 关闭输入流
				out.close(); // 关闭输出流
			}
		}
		return count;
	}

	public void zipFiles(Object[] sources, File target) throws IOException { // 压缩文件

		FileOutputStream fout = new FileOutputStream(target); // 得到目标文件输出流
		ZipOutputStream zout = new ZipOutputStream(fout); // 得到压缩输出流
		byte[] buf = new byte[1024];// 设定读入缓冲区尺寸
		int num;
		FileInputStream fin = null;
		ZipEntry entry = null;
		for (int i = 0; i < sources.length; i++) {
			String filename = sources[i].toString(); // 得到待压缩文件路径名
			String entryname = filename.substring(filename.lastIndexOf("\\") + 1); // 得到文件名
			entry = new ZipEntry(entryname); // 实例化条目列表
			zout.putNextEntry(entry); // 将ZIP条目列表写入输出流
			fin = new FileInputStream(filename); // 从源文件得到文件输入流
			while ((num = fin.read(buf)) != -1) { // 如果文件未读完
				zout.write(buf, 0, num); // 写入缓冲数据
			}
		}
		zout.close(); // 关闭压缩输出流
		fout.close(); // 关闭文件输出流
		fin.close(); // 关闭文件输入流
	}

	



	public static class UserInfo {
		public UserInfo(String school, String classID, String name, String stdID, String classUrl) {
			super();
			this.school = school;
			this.classID = classID;
			this.name = name;
			this.stdID = stdID;
			this.classUrl = classUrl;
		}
		String school,classID,name,stdID;
		String classUrl;
	}

	public static class ConfigInfo {
		int autoMode = 1;
		boolean loop = false;
		int minTimeGap = 30, maxTimeGap = 60, totalTimeLimit = 0;
		boolean autoAns = false, autoHide = true;
		int minCorrectRate = 70, maxCorrectRate = 100, allCorrectRate = 10;//Modified in 2.2
		boolean checkUpdate = true, autoRefresh = true;

		public ConfigInfo() {
		}

		public void copyConfig(ConfigInfo config) {
			this.autoMode = config.autoMode;
			this.loop = config.loop;
			this.minTimeGap = config.minTimeGap;
			this.maxTimeGap = config.maxTimeGap;
			this.autoAns = config.autoAns;
			this.autoHide = config.autoHide;
			this.minCorrectRate = config.minCorrectRate;
			this.maxCorrectRate = config.maxCorrectRate;
			this.allCorrectRate = config.allCorrectRate;
			this.checkUpdate = config.checkUpdate;
			this.autoRefresh = config.autoRefresh;
		}

	}
}
