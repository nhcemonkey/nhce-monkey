package common;

import java.awt.Dimension;
import java.awt.Image;
import java.io.DataInputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import chrriis.dj.nativeswing.swtimpl.components.JSyntaxHighlighter;

public class CodeViewer extends JDialog {

	private static final long serialVersionUID = -56809685349630601L;
	private static CodeViewer instance;

	JSyntaxHighlighter syntaxHighlighter;
	JTree tree;
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("src");
	TreeModel model = new DefaultTreeModel(root);
	TreeMap<String, String> codeLib = new TreeMap<String, String>();
	JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

	private CodeViewer() {
		super(Main.getInstance());
		setTitle("Source Code Viewer");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		syntaxHighlighter = new JSyntaxHighlighter();

		add(pane);
		tree = new JTree(model);
		JScrollPane sp = new JScrollPane(tree);
		sp.setPreferredSize(new Dimension(240, 100));
		pane.setLeftComponent(sp);
		pane.setRightComponent(syntaxHighlighter);
		setSize(1280, 600);
		setIconImage(getImageIcon("java.png", 48).getImage());
		setLocationRelativeTo(null);

		loadCode();
		Main parent = Main.getInstance();
		JOptionPane.showMessageDialog(parent, "即将展示本程序的所有源代码，望高人不吝赐教！", "提示", JOptionPane.INFORMATION_MESSAGE,
				parent.getLogo("java.png", parent.BIGICON_SIZE));
	}

	public ImageIcon getImageIcon(String fileName, int iconSize) {
		ImageIcon icon = null;
		URL url = this.getClass().getResource("logos/" + fileName);
		if (url != null) {
			icon = new ImageIcon(url);
			if (iconSize > 0)
				icon.setImage(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
			return icon;
		}
		return icon;
	}

	private void loadCode() {
		try {
			DataInputStream input = new DataInputStream(this.getClass().getResourceAsStream("src/list.txt"));
			byte[] b = new byte[1024];
			int l = 0;
			StringBuilder sb = new StringBuilder();
			while ((l = input.read(b, 0, 1024)) > 0) {
				sb.append(new String(b, 0, l));
			}
			input.close();
			String list = sb.toString();
			root.removeAllChildren();
			codeLib.clear();
			String[] srcNames = list.split("\r\n");
			for (String srcName : srcNames) {
				String name = srcName.trim();
				if (name.length() > 0)
					loadCode(name);
			}
			tree.addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					TreePath path = e.getPath();
					Object[] objs = path.getPath();
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < objs.length; i++) {
						sb.append(objs[i]);
						if (i < objs.length - 1)
							sb.append('/');
					}
					String code = codeLib.get(sb.toString());
					if (code != null)
						syntaxHighlighter.setContent(code, JSyntaxHighlighter.ContentLanguage.Java);
				}
			});
			expandAll(tree, new TreePath(root.getPath()), true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand, boolean recursively) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (recursively && node.getChildCount() > 0) {
			for (@SuppressWarnings("rawtypes")
			Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand, recursively);
			}
		}
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

	private void loadCode(String name) {
		try {
			DataInputStream input = new DataInputStream(this.getClass().getResourceAsStream("src/" + name));
			byte[] b = new byte[1024];
			int l = 0;
			StringBuilder sb = new StringBuilder();
			while ((l = input.read(b, 0, 1024)) > 0) {
				sb.append(new String(b, 0, l));
			}
			input.close();
			String code = sb.toString();
			// name = name.replace(".jsrc", ".java");
			String[] nodeName = name.split("/");
			DefaultMutableTreeNode base = root;
			NODE: for (int i = 0; i < nodeName.length; i++) {
				@SuppressWarnings("rawtypes")
				Enumeration children = base.children();
				while (children.hasMoreElements()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
					if (node.toString().equals(nodeName[i])) {
						base = node;
						continue NODE;
					}
				}
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName[i]);
				codeLib.put(name, code);
				base.add(newNode);
				base = newNode;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CodeViewer getInstance() {
		if (instance == null) {
			instance = new CodeViewer();
		}
		return instance;
	}

}
