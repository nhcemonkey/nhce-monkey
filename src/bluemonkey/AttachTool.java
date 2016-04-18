package bluemonkey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class AttachTool {
	public static void extractAttach(String file, String dir) throws Exception{
		URL url = BlueMonkey.bm.getClass().getResource("attach/" + file);
		InputStream input = url.openStream();
		File dist = new File(dir);
		if(!dist.exists())
			dist.mkdirs();
		FileOutputStream output = new FileOutputStream(dist.getCanonicalFile()+"/"+file);
		int l = 0;
		byte[] tmp = new byte[2048];
		while ((l = input.read(tmp)) != -1) {
			output.write(tmp, 0, l);
		}
		System.out.println("[Attachment] "+file + " extracted to "+dir);
		input.close();
		output.close();
	}
}
