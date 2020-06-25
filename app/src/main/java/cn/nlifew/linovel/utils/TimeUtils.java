package cn.nlifew.linovel.utils;

import java.util.Calendar;
import java.util.Locale;

import cn.nlifew.xqdreader.utils.Utils;

public final class TimeUtils {
    public static final float YEAR = 31536000000F;
    public static final float MONTH = 2592000000F;
    public static final float DAY = 86400000F;
    public static final float HOUR = 3600000F;
    public static final float MINUTE = 60000F;
    public static final float SECOND = 1000F;

    public static String formatDate(long time) {
        Calendar now = Calendar.getInstance(Locale.CHINA);
        Calendar old = Calendar.getInstance(Locale.CHINA);
        old.setTimeInMillis(time);

        StringBuilder sb = Utils.obtainStringBuilder(32);

        int year = old.get(Calendar.YEAR);
        int month = old.get(Calendar.MONTH);
        int day = old.get(Calendar.DAY_OF_MONTH);

        int nowDay = now.get(Calendar.DAY_OF_MONTH);

        if (year != now.get(Calendar.YEAR)) {
            sb.append(year).append('年')
                    .append(month + 1).append('月')
                    .append(day).append('日');
        }
        else if (nowDay - day > 2 || month != now.get(Calendar.MONTH)) {
            sb.append(month + 1).append('月').append(day).append('日');
        }
        else if (nowDay - day > 1) {
            sb.append("前天");
        }
        else if (nowDay - day > 0) {
            sb.append("昨天");
        }
        else {
            sb.append("今天");
        }

        int hour = old.get(Calendar.HOUR_OF_DAY);
        int minute = old.get(Calendar.MINUTE);
        String s = String.format(Locale.US, "%02d:%02d", hour, minute);
        s = sb.append(' ').append(s).toString();

        Utils.recycle(sb);
        return s;
    }
}
