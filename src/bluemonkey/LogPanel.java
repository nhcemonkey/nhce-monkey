package bluemonkey;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


public class LogPanel extends JPanel {

	private static final long serialVersionUID = 7165599893771556787L;

	public LogPanel() {
		setLayout(new BorderLayout());
		add(new MemoryUsagePanel(30000), BorderLayout.EAST);
		new MemoryUsagePanel.DataGenerator(100).start();
	}
	
	public static class MemoryUsagePanel extends JPanel {

		private static final long serialVersionUID = 3638473240570992274L;
		private static TimeSeries total = new TimeSeries("Total Memory");
		private static TimeSeries free;

		public MemoryUsagePanel(int paramInt) {
			super(new BorderLayout());
			MemoryUsagePanel.total.setMaximumItemAge(paramInt);
			MemoryUsagePanel.free = new TimeSeries("Free Memory");
			MemoryUsagePanel.free.setMaximumItemAge(paramInt);
			TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
			localTimeSeriesCollection.addSeries(MemoryUsagePanel.total);
			localTimeSeriesCollection.addSeries(MemoryUsagePanel.free);
			DateAxis localDateAxis = new DateAxis("Time");
			NumberAxis localNumberAxis = new NumberAxis("Memory(MB)");
			// localDateAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
			// localNumberAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
			// localDateAxis.setLabelFont(new Font("SansSerif", 0, 14));
			// localNumberAxis.setLabelFont(new Font("SansSerif", 0, 14));
			XYLineAndShapeRenderer localXYLineAndShapeRenderer = new XYLineAndShapeRenderer(true, false);
			localXYLineAndShapeRenderer.setSeriesPaint(0, Color.red);
			localXYLineAndShapeRenderer.setSeriesPaint(1, Color.green);
			localXYLineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(5.0F, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			localXYLineAndShapeRenderer.setSeriesStroke(1, new BasicStroke(5.0F, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			XYPlot localXYPlot = new XYPlot(localTimeSeriesCollection, localDateAxis, localNumberAxis,
					localXYLineAndShapeRenderer);
			localDateAxis.setAutoRange(true);
			localDateAxis.setLowerMargin(0.0D);
			localDateAxis.setUpperMargin(0.0D);
			localDateAxis.setTickLabelsVisible(true);
			localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			JFreeChart localJFreeChart = new JFreeChart("JVM Memory Usage", localXYPlot);
			localJFreeChart.getLegend().visible = false;// setPosition(RectangleEdge.RIGHT);
			//localJFreeChart.setAntiAlias(true);//Added in 2.1
			ChartUtilities.applyCurrentTheme(localJFreeChart);
			ChartPanel localChartPanel = new ChartPanel(localJFreeChart, true);
			localChartPanel.setPreferredSize(new Dimension(400, 100));
			// localChartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4,
			// 4, 4, 4),
			// BorderFactory.createLineBorder(Color.black)));
			add(localChartPanel);
		}

		private static void addTotalObservation(double paramDouble) {
			total.addOrUpdate(new Millisecond(), paramDouble);
		}

		private static void addFreeObservation(double paramDouble) {
			free.addOrUpdate(new Millisecond(), paramDouble);
		}

		static class DataGenerator extends Timer {
			private static final long serialVersionUID = 26955558307928525L;

			public DataGenerator(int arg2) {
				super(arg2, new ActionListener() {
					public void actionPerformed(ActionEvent paramActionEvent) {
						double l1 = Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0;
						double l2 = Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0;
						addTotalObservation(l2);
						addFreeObservation(l1);
					}
				});

			}

		}
	}
}
