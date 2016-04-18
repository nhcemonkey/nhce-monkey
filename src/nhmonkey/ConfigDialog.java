package nhmonkey;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nhmonkey.Recorder.ConfigInfo;

public class ConfigDialog extends JDialog {

	private static final long serialVersionUID = -4970609767230409090L;

	JTabbedPane pane;
	JPanel autoPanel, fillPanel, sysPanel;
	JPanel configPanel;
	JButton ok, cancel;
	JSlider minCorrectRateSlider, maxCorrectRateSlider;
	JTextField minTimeGapField, maxTimeGapField, totalLimitField;
	JRadioButton allSecBtn;
	JRadioButton unitSecBtn;
	JRadioButton secSecBtn;
	JCheckBox isLoopBox;
	JRadioButton enableAutoAns;
	JRadioButton disableAutoAns;
	JLabel minCorrectRateLabel;
	JLabel maxCorrectRateLabel;
	JCheckBox autoHideBox;
	JCheckBox autoRefreshBox, checkUpdateBox, trayShowBox;

	ConfigInfo config = new ConfigInfo();

	// boolean changed;

	public ConfigDialog() {
		super(NHMonkey.nm, true);
		setTitle("设置");
		setIconImage(NHMonkey.getIconRes("config.png").getImage());
		setResizable(false);
		setSize(450, 500);
		setLocationRelativeTo(NHMonkey.nm);
		add(pane = new JTabbedPane());
		pane.addTab("挂机设置", autoPanel = new JPanel(new GridLayout(4, 0)));
		pane.addTab("填写设置", fillPanel = new JPanel(new GridLayout(4, 0)));
		pane.addTab("系统设置", sysPanel = new JPanel(new GridLayout(4, 0)));
		configPanel = new JPanel();
		autoPanel.setBorder(new TitledBorder(""));
		fillPanel.setBorder(new TitledBorder(""));
		sysPanel.setBorder(new TitledBorder(""));
		add(configPanel, BorderLayout.SOUTH);
		configPanel.add(ok = new JButton("保存"));
		configPanel.add(cancel = new JButton("取消"));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		// 加载“挂机设置”页
		JPanel sectionPanel = new JPanel(new GridLayout(1, 0));
		sectionPanel.setBorder(new TitledBorder("挂机范围"));
		ButtonGroup sectionGroup = new ButtonGroup();
		allSecBtn = new JRadioButton("所有单元");
		allSecBtn.setSelected(true);
		unitSecBtn = new JRadioButton("当前单元");
		secSecBtn = new JRadioButton("当前小节");

		sectionGroup.add(allSecBtn);
		sectionGroup.add(unitSecBtn);
		sectionGroup.add(secSecBtn);
		sectionPanel.add(allSecBtn);
		sectionPanel.add(unitSecBtn);
		sectionPanel.add(secSecBtn);
		isLoopBox = new JCheckBox("循环挂机");

		sectionPanel.add(isLoopBox);
		autoPanel.add(sectionPanel);

		JPanel timePanel = new JPanel(new GridLayout(2, 0));
		timePanel.setBorder(new TitledBorder("时间设定"));
		JPanel gapPanel = new JPanel(new GridLayout(1, 0));
		timePanel.add(gapPanel);
		gapPanel.add(new JLabel("最小间隔(秒)", SwingConstants.CENTER));
		minTimeGapField = new JTextField("" + 30);
		gapPanel.add(minTimeGapField);
		gapPanel.add(new JLabel("最大间隔(秒)", SwingConstants.CENTER));
		maxTimeGapField = new JTextField("" + 60);
		gapPanel.add(maxTimeGapField);
		JPanel timeLimitPanel = new JPanel(new GridLayout(1, 0));
		timePanel.add(timeLimitPanel);
		timeLimitPanel.add(new JLabel("运行时间限制(秒)", SwingConstants.CENTER));
		totalLimitField = new JTextField("0");
		timeLimitPanel.add(totalLimitField);
		timeLimitPanel.add(new JLabel("(设0取消时间限制)", SwingConstants.CENTER));
		timeLimitPanel.setVisible(false);//Added in 2.3
		autoPanel.add(timePanel);

		JPanel autoAnsPanel = new JPanel(new GridLayout(1, 0));
		autoAnsPanel.setBorder(new TitledBorder("答题设置"));
		ButtonGroup autoAnsGroup = new ButtonGroup();
		enableAutoAns = new JRadioButton("<html>开启自动答题<br>(会延长每小节时间)</html>");
		disableAutoAns = new JRadioButton("关闭自动答题");

		disableAutoAns.setSelected(true);
		autoAnsGroup.add(enableAutoAns);
		autoAnsGroup.add(disableAutoAns);
		autoAnsPanel.add(enableAutoAns);
		autoAnsPanel.add(disableAutoAns);
		JButton jumpToFillConfigBtn = new JButton("填写设置",NHMonkey.getIconRes("fill_form.png",30));//Modified in 2.1
		jumpToFillConfigBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pane.setSelectedIndex(1);
			}
		});
		autoAnsPanel.add(jumpToFillConfigBtn);
		autoPanel.add(autoAnsPanel);

		autoHideBox = new JCheckBox("挂机时自动隐藏到托盘区  (按F10可手动隐藏)");

		autoPanel.add(autoHideBox);

		// 加载“填写设置”页
		JPanel correctRatePanel = new JPanel(new GridLayout(0, 3));
		correctRatePanel.setBorder(new TitledBorder("正确率设置  (仅限客观题或混合题中的客观题部分)"));
		minCorrectRateLabel = new JLabel(70 + " %", SwingConstants.CENTER);//Modified in 2.1
		maxCorrectRateLabel = new JLabel(100 + " %", SwingConstants.CENTER);
		// final JLabel allCorrectRateLabel = new JLabel(10 + " %",
		// SwingConstants.CENTER);
		minCorrectRateSlider = new JSlider(0, 100, 70);//Modified in 2.2
		maxCorrectRateSlider = new JSlider(70, 100, 100);//Modified in 2.2
		// final JSlider allCorrectRateSlider = new JSlider(0, 100, 10);
		correctRatePanel.add(new JLabel("最小正确率", SwingConstants.CENTER));
		correctRatePanel.add(minCorrectRateSlider);
		correctRatePanel.add(minCorrectRateLabel);
		correctRatePanel.add(new JLabel("最大正确率", SwingConstants.CENTER));
		correctRatePanel.add(maxCorrectRateSlider);
		correctRatePanel.add(maxCorrectRateLabel);
		// correctRatePanel.add(new JLabel("人品爆发概率", SwingConstants.CENTER));
		// correctRatePanel.add(allCorrectRateSlider);
		// correctRatePanel.add(allCorrectRateLabel);

		// TODO delete this after finishing fake making
		//fillPanel.add(new JLabel("<html><h2><font color=\"red\">本功能由于缺少足够测试，并未实际启用，实际正确率都设定为100%。"
		//		+"如有需要，请手动控制正确率！</font></h2></html>"));//Delected in 2.1

		minCorrectRateSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int value = minCorrectRateSlider.getValue();
				minCorrectRateLabel.setText(value + " %");
				maxCorrectRateSlider.setMinimum(value);
			}
		});
		maxCorrectRateSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				maxCorrectRateLabel.setText(maxCorrectRateSlider.getValue() + " %");
			}
		});
		// allCorrectRateSlider.addChangeListener(new ChangeListener() {
		//
		// @Override
		// public void stateChanged(ChangeEvent e) {
		// allCorrectRateLabel.setText(allCorrectRateSlider.getValue() + " %");
		// }
		// });
		fillPanel.add(correctRatePanel);

		// 加载“系统设置”页
		JPanel autoUpdatePanel = new JPanel(new GridLayout(1, 0));
		autoUpdatePanel.setBorder(new TitledBorder("更新设置"));
		sysPanel.add(autoUpdatePanel);
		autoRefreshBox = new JCheckBox("<html>自动刷新用户信息<br>(仅在登陆后)</html>");
		checkUpdateBox = new JCheckBox("自动检查程序更新");
		autoRefreshBox.setSelected(true);
		checkUpdateBox.setSelected(true);
		autoUpdatePanel.add(autoRefreshBox);
		autoUpdatePanel.add(checkUpdateBox);
		JPanel trayPanel = new JPanel(new GridLayout(1, 0));
		trayPanel.setBorder(new TitledBorder("托盘设置"));
		sysPanel.add(trayPanel);
		trayShowBox = new JCheckBox("允许显示托盘气泡");
		trayShowBox.setSelected(true);
		trayPanel.add(trayShowBox);
		
		try {
			ConfigInfo info = NHMonkey.recorder.readConfigInfo();
			if (info != null)
				config.copyConfig(info);
		} catch (IOException e1) {
			NHMonkey.output.error(e1);
		}
	}

	public void showDialog() {
		// load config info
		switch (config.autoMode) {
		case 1:
			allSecBtn.setSelected(true);
			break;
		case 2:
			unitSecBtn.setSelected(true);
			break;
		case 3:
			secSecBtn.setSelected(true);
			break;
		}
		if (config.loop)
			isLoopBox.setSelected(true);
		else
			isLoopBox.setSelected(false);
		minTimeGapField.setText("" + config.minTimeGap);
		maxTimeGapField.setText("" + config.maxTimeGap);
		totalLimitField.setText("" + config.totalTimeLimit);
		if (config.autoAns)
			enableAutoAns.setSelected(true);
		else
			disableAutoAns.setSelected(true);
		if (config.autoHide)
			autoHideBox.setSelected(true);
		else
			autoHideBox.setSelected(false);
		minCorrectRateSlider.setValue(config.minCorrectRate);
		minCorrectRateLabel.setText(config.minCorrectRate + " %");
		maxCorrectRateSlider.setValue(config.maxCorrectRate);
		maxCorrectRateLabel.setText(config.maxCorrectRate + " %");
		checkUpdateBox.setSelected(config.checkUpdate);
		autoRefreshBox.setSelected(config.autoRefresh);
		trayShowBox.setSelected(!config.forbidTrayPop);

		setVisible(true);
	}

	public void showDialog(ConfigInfo config) {
		this.config.copyConfig(config);
		showDialog();
	}

	public void save() {
		ConfigInfo backup = new ConfigInfo();
		backup.copyConfig(config);
		try {
			// Save some data to config object
			if (allSecBtn.isSelected())
				config.autoMode = 1;
			else if (unitSecBtn.isSelected())
				config.autoMode = 2;
			else if (secSecBtn.isSelected())
				config.autoMode = 3;
			config.loop = isLoopBox.isSelected();
			config.minTimeGap = Integer.parseInt(minTimeGapField.getText());
			config.maxTimeGap = Integer.parseInt(maxTimeGapField.getText());
			config.totalTimeLimit = Integer.parseInt(totalLimitField.getText());
			config.autoAns = enableAutoAns.isSelected();
			config.autoHide = autoHideBox.isSelected();
			config.minCorrectRate = minCorrectRateSlider.getValue();
			config.maxCorrectRate = maxCorrectRateSlider.getValue();
			config.checkUpdate = checkUpdateBox.isSelected();
			config.autoRefresh = autoRefreshBox.isSelected();
			config.forbidTrayPop = !trayShowBox.isSelected();
			setVisible(false);
			NHMonkey.recorder.writeConfigInfo(config);
		} catch (Exception e) {
			NHMonkey.output.warn("设置保存失败，请输入正确的信息！");
			config.copyConfig(backup);
		}
		NHMonkey.output.info("设置保存成功！");
	}

	public void cancel() {
		setVisible(false);
	}

	public ConfigInfo getInfo() {
		return config;
	}

	public String getAutoWorkInfo() {
		StringBuilder str = new StringBuilder();
		str.append("[挂机范围]:   ");
		switch (config.autoMode) {
		case 1:
			str.append("所有单元");
			break;
		case 2:
			str.append("当前单元");
			break;
		case 3:
			str.append("当前小节");
			break;
		}
		if (config.loop)
			str.append("(循环)");
		else
			str.append("(不循环)");
		str.append("\n[间隔时间]:   ");
		str.append(config.minTimeGap + "秒 - " + config.maxTimeGap + "秒");
		str.append("\n[自动答题]:   " + (config.autoAns ? "已启用" : "未启用"));
		str.append("\n[自动隐藏]:   " + (config.autoHide ? "是" : "否"));
		return str.toString();
	}

	public String getFillAnsInfo() {
		// recover this after finishing fake making
		 String str = "[答题正确率]:   " + config.minCorrectRate + " % - " +
		 config.maxCorrectRate + " %";
		 return str;
		//return "[答题正确率]:   100 %";
	}
}
