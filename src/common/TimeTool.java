package common;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTool {
	public static String getTimePeriod() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour >= 23 || hour < 1)
			return "夜晚";
		else if (hour >= 1 && hour < 4)
			return "深夜";
		else if (hour >= 5 && hour < 6)
			return "凌晨";
		else if (hour >= 6 && hour < 8)
			return "早上";
		else if (hour >= 8 && hour < 11)
			return "上午";
		else if (hour >= 11 && hour < 13)
			return "中午";
		else if (hour >= 13 && hour < 17)
			return "下午";
		else if (hour >= 17 && hour < 18)
			return "傍晚";
		else if (hour >= 18 && hour < 23)
			return "晚上";
		return "";
	}

	/**
	 * 计算从 startTime到endTime的时长
	 * @param startTime
	 * @param endTime
	 * @param format
	 * @param depth
	 *            结果深度：1-d 2-h 3-M 4-s
	 * @return
	 */
	public static String dateDiff(String startTime, String endTime, DateFormat sd, int depth) {
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		long ns = 1000;// 一秒钟的毫秒数
		long diff;
		try {
			// 获得两个时间的毫秒时间差异
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			long day = diff / nd;// 计算差多少天
			long hour = diff % nd / nh;// 计算差多少小时
			long min = diff % nd % nh / nm;// 计算差多少分钟
			long sec = diff % nd % nh % nm / ns;// 计算差多少秒
			// 输出结果
			switch (depth) {
			case 1:
				return day + "天";
			case 2:
				if (day != 0)
					return day + "天" + hour + "小时";
				else
					return hour + "小时";
			case 3:
				if (day != 0)
					return day + "天" + hour + "小时" + min + "分钟";
				else if(hour != 0)
					return hour + "小时" + min + "分钟";
				else
					return min + "分钟";
			default:
				return day + "天" + hour + "小时" + min + "分钟" + sec + "秒。";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
