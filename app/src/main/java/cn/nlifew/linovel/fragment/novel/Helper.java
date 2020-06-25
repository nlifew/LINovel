package cn.nlifew.linovel.fragment.novel;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;


import cn.nlifew.xqdreader.bean.NovelBean;
import cn.nlifew.xqdreader.utils.Utils;

import static cn.nlifew.linovel.utils.TimeUtils.DAY;
import static cn.nlifew.linovel.utils.TimeUtils.HOUR;
import static cn.nlifew.linovel.utils.TimeUtils.MINUTE;
import static cn.nlifew.linovel.utils.TimeUtils.MONTH;
import static cn.nlifew.linovel.utils.TimeUtils.SECOND;
import static cn.nlifew.linovel.utils.TimeUtils.YEAR;

final class Helper {

    static SpannableStringBuilder toSpanText(SpannableStringBuilder sb, int num, String unit, String bottom) {
        sb.clear();

        String numberUnit = "";
        if (num >= 10000) {
            num = Math.round(num / 10000f);
            numberUnit = "万";
        }
        int i = sb.append(Integer.toString(num)).length();
        sb.setSpan(DP18, 0, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(BOLD, 0, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.append(numberUnit).append(unit).append('\n');
        sb.append(bottom, GRAY, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    private static final AbsoluteSizeSpan DP18 = new AbsoluteSizeSpan(18, true);
    private static final StyleSpan BOLD = new StyleSpan(Typeface.BOLD_ITALIC);
    private static final ForegroundColorSpan GRAY = new ForegroundColorSpan(Color.GRAY);


    static String toCategory(NovelBean.DataType data) {
        StringBuilder sb = Utils.obtainStringBuilder(32);
        if (! "连载".equals(data.BookStatus)) {
            String s = sb.append(data.TotalChapterCount).append("章完本").toString();
            Utils.recycle(sb);
            return s;
        }

        sb.append("连载至").append(data.TotalChapterCount).append("章 - ");

        long time = data.LastChapterUpdateTime > data.LastVipChapterUpdateTime ?
                data.LastChapterUpdateTime : data.LastVipChapterUpdateTime;
        time = System.currentTimeMillis() - time;

        if (time >= YEAR)
            sb.append((int) (time / YEAR)).append("年");
        else if (time >= MONTH)
            sb.append((int) (time / MONTH)).append("月");
        else if (time >= DAY)
            sb.append((int) (time / DAY)).append("天");
        else if (time >= HOUR)
            sb.append((int) (time / HOUR)).append("小时");
        else if (time >= MINUTE)
            sb.append((int) (time / MINUTE)).append("分钟");
        else
            sb.append((int) (time / SECOND)).append("秒");

        String s = sb.append("前更新").toString();
        Utils.recycle(sb);
        return s;
    }
}
