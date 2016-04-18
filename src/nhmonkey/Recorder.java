package nhmonkey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import nhmonkey.NHMonkey.Classroom.Section;

import com.thoughtworks.xstream.XStream;

public class Recorder {
	private static XStream xStream;
	private File file;
	private Reader reader;
	private FileWriter writer;
	final public String ANS_RECORD_TITLE = "<!-- Answer Record of " + VersionPanel.NAME + " V" + VersionPanel.VERSION
			+ "-->\n";
	final public String ANS_RECORD_TITLE_OLD = "<!-- Answer Record of NHCEHack V" + VersionPanel.VERSION + "-->\n";// Added
																													// in
																													// 2.4-5,
																													// 考虑兼容性
	final public String USER_INFO_TITLE = "<!-- User Info of " + VersionPanel.NAME + " V" + VersionPanel.VERSION
			+ " -->\n";
	final public String CONFIG_INFO_TITLE = "<!-- Configuration Info of " + VersionPanel.NAME + " V"
			+ VersionPanel.VERSION + " -->\n";
	final static public String TEMP_DIR = System.getenv("APPDATA") +'\\'+ "NHCEHack" + File.separator;

	public Recorder() {
		// System.out.println(DIR_PATH+"rec");
		xStream = new XStream();
	}

	public void writeRecord(Form answer) {
		try {
			File dir = new File(TEMP_DIR + "rec");
			if (!dir.exists())
				dir.mkdirs();
			file = new File(TEMP_DIR + "rec" + '/' + answer.toFileName());
			if (file == null)
				System.out.println("Record file create failure!");// Modified in
																	// 2.2.1
			writer = new FileWriter(file);
			writer.write(ANS_RECORD_TITLE);
			writer.write("<!-- Generated in " + new Date().toString() + " -->\n\n");
			xStream.toXML(answer, writer);
			writer.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void writeRecord2(Form2 answer) {
		try {
			File dir = new File(TEMP_DIR + "rec");
			if (!dir.exists())
				dir.mkdirs();
			file = new File(TEMP_DIR + "rec" + '/' + answer.toFileName());
			if (file == null)
				System.out.println("Record file create failure!");// Modified in
																	// 2.2.1
			writer = new FileWriter(file);
			writer.write(ANS_RECORD_TITLE);
			writer.write("<!-- Generated in " + new Date().toString() + " -->\n\n");
			xStream.toXML(answer, writer);
			writer.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public Form readRecord(File readFile) throws FileNotFoundException, IOException {
		System.out.println("Parsing record file...");
		reader = new FileReader(readFile);
		char[] cbuf = new char[ANS_RECORD_TITLE.length()];
		reader.read(cbuf, 0, ANS_RECORD_TITLE.length());
		Form rec = null;
		String str = new String(cbuf);
		int lastIndex = str.lastIndexOf('.');// Modified in 2.2.1
		if (lastIndex >= 0
				&& (str.substring(0, lastIndex).equals(// Modified in 2.4-5
						ANS_RECORD_TITLE.substring(0, ANS_RECORD_TITLE.lastIndexOf('.'))) || str
						.substring(0, lastIndex).equals(
								ANS_RECORD_TITLE_OLD.substring(0, ANS_RECORD_TITLE_OLD.lastIndexOf('.'))))) {
			rec = (Form) xStream.fromXML(reader);
			// System.out.println(rec);
		} else
			System.out.println("Record file mismatch!");// Modified in 2.2.1
		reader.close();
		return rec;
	}

	public Form2 readRecord2(File readFile) throws FileNotFoundException, IOException {
		System.out.println("Parsing record file...");
		reader = new FileReader(readFile);
		char[] cbuf = new char[ANS_RECORD_TITLE.length()];
		reader.read(cbuf, 0, ANS_RECORD_TITLE.length());
		Form2 rec = null;
		String str = new String(cbuf);
		int lastIndex = str.lastIndexOf('.');// Modified in 2.2.1
		if (lastIndex >= 0
				&& (str.substring(0, lastIndex).equals(// Modified in 2.4-5
						ANS_RECORD_TITLE.substring(0, ANS_RECORD_TITLE.lastIndexOf('.'))) || str
						.substring(0, lastIndex).equals(
								ANS_RECORD_TITLE_OLD.substring(0, ANS_RECORD_TITLE_OLD.lastIndexOf('.'))))) {
			rec = (Form2) xStream.fromXML(reader);
			// System.out.println(rec);
		} else
			System.out.println("Record file mismatch!");// Modified in 2.2.1
		reader.close();
		return rec;
	}

	/**
	 * 从默认的记录文件夹读取名为fileName的文件
	 * 
	 * @param fileName
	 * @return 若不存在，返回null
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Form readRecord(String fileName) throws FileNotFoundException, IOException {
		File record = new File(TEMP_DIR + "rec" + '/' + fileName);
		if (record.exists())
			return readRecord(record);
		else
			return null;
	}

	public Form2 readRecord2(String fileName) throws FileNotFoundException, IOException {
		File record = new File(TEMP_DIR + "rec" + '/' + fileName);
		if (record.exists())
			return readRecord2(record);
		else
			return null;
	}

	public void writeUserInfo(String host, String userID) {
		try {
			File dir = new File(TEMP_DIR);
			if (!dir.exists())
				dir.mkdirs();
			file = new File(TEMP_DIR + "user.xml");
			if (file == null)
				System.out.println("User info file create failure!");// Modified
																		// in
																		// 2.2.1
			writer = new FileWriter(file);
			writer.write(USER_INFO_TITLE);
			writer.write("<!-- Generated in " + new Date().toString() + " -->\n\n");
			xStream.toXML(new UserInfo(host, userID), writer);
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
			System.out.println("User info file mismatch!");// Modified in 2.2.1
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
				System.out.println("Config info file create failure!");// Modified
																		// in
																		// 2.2.1
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
			System.out.println("Config info file mismatch!");// Modified in
																// 2.2.1
		reader.close();
		return info;
	}

	/**
	 * Modified in 2.1
	 * 
	 * @param dist
	 */
	public void pack(File dist) {
		try {
			File dir = new File(TEMP_DIR + "rec");
			if (!dir.exists())
				return;
			File[] list = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return Form.checkFileName(name) || Form2.checkFileName(name);
				}
			});
			if (list.length > 0) {
				zipFiles(list, dist);
				NHMonkey.output.info("导出成功！(" + list.length + "条记录)");
			}
		} catch (Exception e) {
			e.printStackTrace();
			NHMonkey.output.error("导出失败！");
		}
	}

	/**
	 * Added in 2.1
	 * 
	 * @param sources
	 */
	public void unpack(File[] sources) {
		try {
			File dir = new File(TEMP_DIR + "rec");
			if (!dir.exists())
				dir.mkdirs();
			int count = 0;
			for (File source : sources)
				count += extractFile(new ZipFile(source), TEMP_DIR + "rec");
			NHMonkey.output.info("导入成功！(" + sources.length + "个数据包," + count + "条记录)");// Modified
																						// in
																						// 2.1
		} catch (Exception e) {
			e.printStackTrace();
			NHMonkey.output.error("导入失败！");
		}
	}

	public void unpack(File source) {
		try {
			File dir = new File(TEMP_DIR + "rec");
			if (!dir.exists())
				dir.mkdirs();
			int count = extractFile(new ZipFile(source), TEMP_DIR + "rec");
			NHMonkey.output.info("导入成功！(1个数据包," + count + "条记录)");// Modified in
																	// 2.1
		} catch (Exception e) {
			e.printStackTrace();
			NHMonkey.output.error("导入失败！");
		}
	}

	/**
	 * Modified in 2.1 --- Add return type
	 * 
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

	public static interface GeneralForm {
		public String toFileName();
	}

	public static class Form2 implements GeneralForm {
		String name;
		String timeInMin, timeStart, timeEnd;
		public ArrayList<FormPart> inputs = new ArrayList<FormPart>();

		public Form2(Form2 f) {
			this(f.name, f.timeInMin, f.timeStart, f.timeEnd);
			for (FormPart p : f.inputs) {
				FormPart fp = new FormPart();
				for (Input in : p.inputs)
					fp.addInput(in.name, in.value);
				this.inputs.add(fp);
			}
		}

		public Form2(String name, String timeInMin, String timeStart, String timeEnd) {
			this.name = name;
			this.timeInMin = timeInMin;
			this.timeStart = timeStart;
			this.timeEnd = timeEnd;
		}

		public void addFormPart(FormPart fp) {
			inputs.add(fp);
		}

		public static String getHash(String name, String timeInMin, String timeStart, String timeEnd) {
			String hash = Math.abs((name + timeInMin + timeStart + timeEnd).hashCode()) + "";
			System.out.println(name + "-" + timeInMin + "-" + timeStart + "-" + timeEnd + " Hash:" + hash);
			return hash;
		}

		public String getHash() {
			return getHash(name, timeInMin, timeStart, timeEnd);
		}

		@Override
		public String toString() {
			return xStream.toXML(this);
		}

		@Override
		public String toFileName() {
			return "quiz-" + getHash() + ".xml";
		}

		public static String generateFileName(String name, String timeInMin, String timeStart, String timeEnd) {
			return "quiz-" + getHash(name, timeInMin, timeStart, timeEnd) + ".xml";
		}

		public static boolean checkFileName(String name) {
			return name.matches("quiz\\-[\\d]+.xml");
		}
	}

	public static class FormPart {
		public ArrayList<Input> inputs = new ArrayList<Input>();

		public void addInput(String name, String value) {
			inputs.add(new Input(name, value));
		}
	}

	public static class Form implements GeneralForm {
		public int bookID, unitID, sectionID, sisterID;
		public String testID;
		public ArrayList<Input> inputs = new ArrayList<Input>();

		public void addInput(String name, String value) {
			inputs.add(new Input(name, value));
		}

		public Form(Section sec) {
			this.bookID = sec.bookID;
			this.unitID = sec.unitID;
			this.sectionID = sec.sectionID;
			this.sisterID = sec.sisterID;
			this.testID = sec.testID;
		}

		@Override
		public String toString() {
			return xStream.toXML(this);
		}

		@Override
		public String toFileName() {
			return bookID + "-" + unitID + "-" + sectionID + "-" + sisterID + "-" + testID + ".xml";
		}

		public static String generateFileName(int bookID, int unitID, int sectionID, int sisterID, String testID) {
			return bookID + "-" + unitID + "-" + sectionID + "-" + sisterID + "-" + testID + ".xml";
		}

		public static boolean checkFileName(String name) {
			return name.matches("[\\d]+\\-[\\d]+\\-[\\d]+\\-[\\d]+\\-[\\d]+\\.[\\d]+\\.xml");
		}
	}

	public static class Input {
		String name, value;

		public Input(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		public Input(Input newInput) {
			super();
			this.name = newInput.name;
			this.value = newInput.value;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return xStream.toXML(this);
		}
	}

	public static class UserInfo {
		public UserInfo(String host, String userID) {
			super();
			this.host = host;
			this.userID = userID;
		}

		String host, userID;
	}

	public static class ConfigInfo {
		int autoMode = 1;
		boolean loop = false;
		int minTimeGap = 30, maxTimeGap = 60, totalTimeLimit = 0;
		boolean autoAns = false, autoHide = true;
		int minCorrectRate = 70, maxCorrectRate = 100, allCorrectRate = 10;// Modified
																			// in
																			// 2.2
		boolean checkUpdate = true, autoRefresh = true, forbidTrayPop = false;

		public ConfigInfo() {
		}

		public void copyConfig(ConfigInfo config) {
			this.autoMode = config.autoMode;
			this.loop = config.loop;
			this.minTimeGap = config.minTimeGap;
			this.maxTimeGap = config.maxTimeGap;
			this.totalTimeLimit = config.totalTimeLimit;
			this.autoAns = config.autoAns;
			this.autoHide = config.autoHide;
			this.minCorrectRate = config.minCorrectRate;
			this.maxCorrectRate = config.maxCorrectRate;
			this.allCorrectRate = config.allCorrectRate;
			this.checkUpdate = config.checkUpdate;
			this.autoRefresh = config.autoRefresh;
			this.forbidTrayPop = config.forbidTrayPop;
		}
	}
}
