package cn.nlifew.linovel.utils;

import cn.nlifew.linovel.app.ThisApp;

public final class DisplayUtils {

    private DisplayUtils() {
    }

    public static int dp2px(float dp) {
        float density = ThisApp
                .currentApplication
                .getResources()
                .getDisplayMetrics()
                .density;
        return (int) (dp * density + 0.5f);
    }
    
    public static int px2sp(float px) {
        float density = ThisApp
                .currentApplication
                .getResources()
                .getDisplayMetrics()
                .scaledDensity;
        return (int) (px / density + 0.5f);
    }

    public static int sp2px(float sp) {
        float density = ThisApp
                .currentApplication
                .getResources()
                .getDisplayMetrics()
                .scaledDensity;
        return (int) (sp * density + 0.5f);
    }
    
    public static int px2dp(float px) {
        float density = ThisApp
                .currentApplication
                .getResources()
                .getDisplayMetrics()
                .density;
        return (int) (px / density + 0.5f);
    }
}
