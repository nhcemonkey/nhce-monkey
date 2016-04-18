package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TranslateTool {
	public final static String apiURL = "http://fanyi.youdao.com/openapi.do"
			+ "?keyfrom=nhmonkey&key=526817779&type=data&doctype=xml&version=1.1&q=***";
	public final static String HIS_STORE_DIR = System.getenv("APPDATA") + "\\TransTool\\";
	public final static String HIS_STORE = System.getenv("APPDATA") + "\\TransTool\\his.dat";
	public static Map<String, String> result = new TreeMap<String, String>();

	public static String translate(String text) throws Exception {
		if (result.containsKey(text))
			return result.get(text);

		Document doc = Jsoup.parse(Main.client.get(apiURL.replace("***", URLEncoder.encode(text, "utf-8"))));
		Element r = doc.getElementsByTag("translation").first();
		String info = "";
		if (r != null)
			info = r.text();
		if (info.length() <= 0 || info.startsWith("$")) {
			System.err.println("[Translator] No translation for: " + text);
			return null;
		}
		System.out.println("[Translator] " + text + " --> " + info);
		result.put(text, info);
		return info;
	}

	@SuppressWarnings("unchecked")
	public static void initHistory() throws Exception {
		File file = new File(HIS_STORE);
		if (file.exists()) {
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
			result = (Map<String, String>) input.readObject();
			input.close();
		}
	}

	public static void storeHistory() throws Exception {
		File dir = new File(HIS_STORE_DIR);
		File file = new File(HIS_STORE);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (file.exists())
			file.delete();
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
		output.writeObject(result);
		output.close();
	}

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		while (scan.hasNext()) {
			System.out.println(translate(scan.nextLine()));
		}
	}

}
