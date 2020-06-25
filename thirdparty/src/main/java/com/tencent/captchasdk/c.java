package com.tencent.captchasdk;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

@SuppressWarnings("unused")
class c {
    private static final String TAG = "c";

    /* renamed from: a reason: collision with root package name */
    static String f25172a = "";

    /* renamed from: b reason: collision with root package name */
    static String f25173b = "";

    /* renamed from: c reason: collision with root package name */
    static String f25174c = "";

    static int a(Context context) {
        /* nlifew-changed: I have no idea why he want it */
        return 0;

//        int i = -1;
//        if (context == null) {
//            return i;
//        }
//        try {
//            return context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("level", 0);
//        } catch (Exception e) {
//            return i;
//        }
        /* nlifew-changed: end */
    }

    private static int a(Context context, float f) {
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int w = metrics.widthPixels, h = metrics.heightPixels;

            int i = (int) (w < h ? (w * 0.9f):  (h * 0.9f));
            return i > ((int) (400.0f * f)) ? (int) (400.0f * f) : i;
        } catch (Exception e) {
            return (int) (300.0f * f);
        }
    }

    private static int a(Context context, Window window, RelativeLayout relativeLayout, RelativeLayout relativeLayout2, b bVar) {
        float f = context.getResources().getDisplayMetrics().density;
        int a2 = a(context, f);
        window.setBackgroundDrawable(new ColorDrawable(0));
        bVar.setVisibility(View.INVISIBLE);
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.addRule(13);
        relativeLayout.addView(bVar, layoutParams);
        ViewGroup.LayoutParams layoutParams2 = relativeLayout2.getLayoutParams();
        layoutParams2.width = (int) (((float) 140) * f);
        layoutParams2.height = (int) (((float) 140) * f);
        relativeLayout2.setLayoutParams(layoutParams2);
        a aVar = new a(context);
        aVar.a(new d());
        LayoutParams layoutParams3 = new LayoutParams((int) (((float) 52) * f), (int) (((float) 52) * f));
        layoutParams3.addRule(13);
        relativeLayout2.addView(aVar, layoutParams3);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = a2;
        attributes.height = a2;
        window.setAttributes(attributes);
        bVar.a(a2, a2, f * 3.0f);
        return 140;
    }

    private static String a(Context context, int i) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (i == 0) {
                return telephonyManager != null ? telephonyManager.getNetworkType() + "" : "";
            }
            if (i == 1) {
                return telephonyManager != null ? telephonyManager.getNetworkOperatorName() : "";
            }
            /* nlifew-changed: remove all ConnectivityManager because we have no permission */
            return "";

//            if (i != 2) {
//                return "";
//            }
//            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
//                    .getActiveNetworkInfo();
//            if (activeNetworkInfo == null) {
//                return "";
//            }
//            switch (activeNetworkInfo.getType()) {
//                case 0:
//                    switch (activeNetworkInfo.getSubtype()) {
//                        case 1:
//                        case 2:
//                        case 4:
//                        case 7:
//                        case 11:
//                            return "2G";
//                        case 3:
//                        case 5:
//                        case 6:
//                        case 8:
//                        case 9:
//                        case 10:
//                        case 12:
//                        case 14:
//                        case 15:
//                            return "3G";
//                        case 13:
//                            return "4G";
//                        default:
//                            return "UNKOWN";
//                    }
//                case 1:
//                    return "WIFI";
//                default:
//                    return "";
//            }
            /* nlifew-changed: end */
        } catch (Exception e) {
            Log.e(TAG, "a: ", e);
            return "";
        }
    }

    private static void a() {
        /* nlifew-changed: return fake cpu info */
        f25172a = "AArch64 Processor rev 4 (aarch64)";
        f25173b = "unknownH";
//
//        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(Runtime.getRuntime().exec("cat /proc/cpuinfo").getInputStream());
//            LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader);
//            int i = 0;
//            while (true) {
//                String readLine = lineNumberReader.readLine();
//                if (readLine != null) {
//                    if (i == 0) {
//                        String[] split = readLine.split(":\\s+", 2);
//                        if (split.length >= 2) {
//                            f25172a = split[1];
//                        }
//                    }
//                    if (readLine.indexOf("Serial") > -1) {
//                        f25174c = readLine.substring(readLine.indexOf(Constants.COLON_SEPARATOR) + 1, readLine.length()).trim();
//                    }
//                    if (readLine.indexOf("Hardware") > -1) {
//                        f25173b = readLine.substring(readLine.indexOf(Constants.COLON_SEPARATOR) + 1, readLine.length()).trim();
//                    }
//                    i++;
//                } else {
//                    inputStreamReader.close();
//                    return;
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "a: ", e);
//        }
        /* nlifew-changed: end */
    }

    static float b(Context context) {
        try {
            return context.getResources().getDisplayMetrics().density;
        } catch (Exception e) {
            Log.e(TAG, "b: ", e);
            return DisplayHelper.DENSITY;
        }
    }

    static String b() {
        return Build.DISPLAY.contains(VERSION.INCREMENTAL) ? Build.DISPLAY : Build.DISPLAY + "|" + VERSION.INCREMENTAL;
    }

    static String b(Context context, int i) {
        /* nlifew-changed: remove all WifiManager because we have no permission */
        return "";
//        try {
//            WifiInfo connectionInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
//                    .getConnectionInfo();
//            if (i != 0) {
//                return i == 1 ? connectionInfo != null ? connectionInfo.getBSSID() : "" : "";
//            }
//            String str = connectionInfo != null ? connectionInfo.getSSID() : "";
//            if (str != null && str.length() > 0 && str.indexOf("\"") == 0) {
//                str = str.substring(1, str.length());
//            }
//            return (str == null || str.length() <= 0 || str.lastIndexOf("\"") != str.length() + -1) ? str : str.substring(0, str.length() - 1);
//        } catch (Exception e) {
//            Log.e(TAG, "b: ", e);
//            return "";
//        }
        /* nlifew-changed: end */
    }

    static int c() {
        boolean z;
        try {
            z = Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || "google_sdk".equals(Build.PRODUCT);
        } catch (Exception e) {
            Log.e(TAG, "c: ", e);
            z = false;
        }
        return z ? 1 : 0;
    }

    static int c(Context context) {
        try {
            return context.getResources().getDisplayMetrics().widthPixels;
        } catch (Exception e) {
            Log.e(TAG, "c: ", e);
            return 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:20:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int d() {
        /* nlifew-changed: return 0 */
        return 0;
        /*
            r1 = 1
            r2 = 0
            java.lang.String r4 = "su"
            r0 = 8
            java.lang.String[] r5 = new java.lang.String[r0]     // Catch:{ Exception -> 0x0057 }
            r0 = 0
            java.lang.String r3 = "/system/bin/"
            r5[r0] = r3     // Catch:{ Exception -> 0x0057 }
            r0 = 1
            java.lang.String r3 = "/system/xbin/"
            r5[r0] = r3     // Catch:{ Exception -> 0x0057 }
            r0 = 2
            java.lang.String r3 = "/sbin/"
            r5[r0] = r3     // Catch:{ Exception -> 0x0057 }
            r0 = 3
            java.lang.String r3 = "/system/sd/xbin/"
            r5[r0] = r3     // Catch:{ Exception -> 0x0057 }
            r0 = 4
            java.lang.String r3 = "/system/bin/failsafe/"
            r5[r0] = r3     // Catch:{ Exception -> 0x0057 }
            r0 = 5
            java.lang.String r3 = "/data/local/xbin/"
            r5[r0] = r3     // Catch:{ Exception -> 0x0057 }
            r0 = 6
            java.lang.String r3 = "/data/local/bin/"
            r5[r0] = r3     // Catch:{ Exception -> 0x0057 }
            r0 = 7
            java.lang.String r3 = "/data/local/"
            r5[r0] = r3     // Catch:{ Exception -> 0x0057 }
            int r6 = r5.length     // Catch:{ Exception -> 0x0057 }
            r3 = r2
            r0 = r2
        L_0x0033:
            if (r3 >= r6) goto L_0x005d
            r7 = r5[r3]     // Catch:{ Exception -> 0x0062 }
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0062 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0062 }
            r9.<init>()     // Catch:{ Exception -> 0x0062 }
            java.lang.StringBuilder r7 = r9.append(r7)     // Catch:{ Exception -> 0x0062 }
            java.lang.StringBuilder r7 = r7.append(r4)     // Catch:{ Exception -> 0x0062 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0062 }
            r8.<init>(r7)     // Catch:{ Exception -> 0x0062 }
            boolean r7 = r8.exists()     // Catch:{ Exception -> 0x0062 }
            if (r7 == 0) goto L_0x0054
            r0 = r1
        L_0x0054:
            int r3 = r3 + 1
            goto L_0x0033
        L_0x0057:
            r0 = move-exception
            r3 = r2
        L_0x0059:
            com.google.a.a.a.a.a.a.a(r0)
            r0 = r3
        L_0x005d:
            if (r0 == 0) goto L_0x0060
        L_0x005f:
            return r1
        L_0x0060:
            r1 = r2
            goto L_0x005f
        L_0x0062:
            r3 = move-exception
            r10 = r3
            r3 = r0
            r0 = r10
            goto L_0x0059
        */
        // throw new UnsupportedOperationException("Method not decompiled: com.tencent.captchasdk.c.d():int");
        /* nlifew-changed: end */
    }

    static int d(Context context) {
        try {
            return context.getResources().getDisplayMetrics().heightPixels;
        } catch (Exception e) {
            Log.e(TAG, "d: ", e);
            return 0;
        }
    }

    static int e(Context context) {
        /* nlifew-changed: we have all sensors */
        return 511;

//        int i = 1;
//        try {
//            SensorManager sensorManager = (SensorManager) context
//                    .getSystemService(Context.SENSOR_SERVICE);
//
//            if (sensorManager.getDefaultSensor(1) == null) {
//                i = 0;
//            }
//            if (sensorManager.getDefaultSensor(2) != null) {
//                i |= 2;
//            }
//            if (sensorManager.getDefaultSensor(4) != null) {
//                i |= 4;
//            }
//            if (sensorManager.getDefaultSensor(9) != null) {
//                i |= 8;
//            }
//            if (sensorManager.getDefaultSensor(10) != null) {
//                i |= 16;
//            }
//            if (sensorManager.getDefaultSensor(5) != null) {
//                i |= 32;
//            }
//            if (sensorManager.getDefaultSensor(8) != null) {
//                i |= 64;
//            }
//            if (sensorManager.getDefaultSensor(7) != null) {
//                i |= 128;
//            }
//            return sensorManager.getDefaultSensor(6) != null ? i | 256 : i;
//        } catch (Exception e) {
//            Log.e(TAG, "e: ", e);
//            return 0;
//        }
        /* nlifew-changed: end */
    }

    static String e() {
        try {
            return System.getProperty("os.name");
        } catch (Exception e) {
            Log.e(TAG, "e: ", e);
            return "";
        }
    }

    static int f(Context context) {
        /* nlifew-changed: remove all ConnectivityManager */
        return 0;
//        try {
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
//            return (connectivityManager == null || connectivityManager.getActiveNetworkInfo() == null || connectivityManager.getActiveNetworkInfo().getType() != 1) ? 0 : 1;
//        } catch (Exception e) {
//            Log.e(TAG, "f: ", e);
//            return 0;
//        }
        /* nlifew-changed: end */
    }

    static String f() {
        /* nlifew-changed: we don't want to access this by untrusted app */
        return "";

//        String str = "";
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("cat /proc/version").getInputStream()), 8192);
//            String str2 = "";
//            while (true) {
//                String readLine = bufferedReader.readLine();
//                if (readLine == null) {
//                    break;
//                }
//                str2 = str2 + readLine;
//            }
//            if (str2 == "") {
//                return str;
//            }
//            String str3 = "version ";
//            String substring = str2.substring(str3.length() + str2.indexOf(str3));
//            return substring.substring(0, substring.indexOf(" "));
//        } catch (Exception e) {
//            Log.e(TAG, "f: ", e);
//            return str;
//        }
        /* nlifew-changed: end */
    }

    static String g(Context context) {
        try {
            return context.getResources().getString(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.labelRes);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "g: ", e);
            return null;
        }
    }

    static String h(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e(TAG, "h: ", e);
            return null;
        }
    }
}