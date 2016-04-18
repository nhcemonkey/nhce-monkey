package common;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class CodeViewerHelper {

	public static void main(String[] args) {
		if (args.length >= 1) {
			searchSrc(new File(args[0]));
		} else {
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setDialogTitle("请选择源码“副本”所在文件夹,稍后自动整理源码并生成源码清单");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int reply = fc.showOpenDialog(null);
			if (reply == JFileChooser.APPROVE_OPTION) {
				searchSrc(fc.getSelectedFile());
			}
		}
	}

	public static void searchSrc(File dir) {
		try {
			ArrayList<String> srcList = new ArrayList<String>();
			searchSrc(dir, srcList, dir.getAbsolutePath());
			File listFile = new File(dir.getAbsoluteFile() + "\\list.txt");
			if (listFile.exists())
				listFile.delete();
			System.out.println(listFile);
			PrintWriter writer = new PrintWriter(listFile);
			for (String str : srcList) {
				System.out.println(str);
				writer.append(str.replace('\\', '/') + "\r\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void searchSrc(File dir, ArrayList<String> srcList, String basePath) {
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory())
					searchSrc(f, srcList, basePath);
				else {
					if (f.getName().endsWith(".java")) {
						String name = f.getAbsolutePath().substring(basePath.length() + 1);
						// System.out.println(name);
						srcList.add(name);
					} else
						f.delete();
				}
			}
		}
	}

}
