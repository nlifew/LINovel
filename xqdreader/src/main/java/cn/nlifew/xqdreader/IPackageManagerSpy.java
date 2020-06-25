package cn.nlifew.xqdreader;

import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class IPackageManagerSpy implements InvocationHandler {
    private static final String TAG = "IPackageManagerSpy";

    private static final String MY_PACKAGE_NAME = "cn.nlifew.linovel";
    private static final String FAKE_SIGNATURE = "308202253082018ea00302010202044e239460300d06092a864886f70d0101050500305731173015060355040a0c0ec386c3b0c2b5c3a3c396c390c38e311d301b060355040b0c14c386c3b0c2b5c3a3c396c390c38ec384c38dc3b8311d301b06035504030c14c386c3b0c2b5c3a3c396c390c38ec384c38dc3b8301e170d3131303731383032303331325a170d3431303731303032303331325a305731173015060355040a0c0ec386c3b0c2b5c3a3c396c390c38e311d301b060355040b0c14c386c3b0c2b5c3a3c396c390c38ec384c38dc3b8311d301b06035504030c14c386c3b0c2b5c3a3c396c390c38ec384c38dc3b830819f300d06092a864886f70d010101050003818d0030818902818100a3d47f8bfd8d54de1dfbc40a9caa88a43845e287e8f40da2056be126b17233669806bfa60799b3d1364e79a78f355fd4f72278650b377e5acc317ff4b2b3821351bcc735543dab0796c716f769c3a28fedc3bca7780e5fff6c87779f3f3cdec6e888b4d21de27df9e7c21fc8a8d9164bfafac6df7d843e59b88ec740fc52a3c50203010001300d06092a864886f70d0101050500038181001f7946581b8812961a383b2d860b89c3f79002d46feb96f2a505bdae57097a070f3533c42fc3e329846886281a2fbd5c87685f59ab6dd71cc98af24256d2fbf980ded749e2c35eb0151ffde993193eace0b4681be4bcee5f663dd71dd06ab64958e02a60d6a69f21290cb496dd8784a4c31ebadb1b3cc5cb0feebdaa2f686ee2";
    private static final String FAKE_VERSION_NAME = XQDReader.VERSION_NAME;
    private static final int FAKE_VERSION_CODE = Integer.parseInt(XQDReader.VERSION_CODE);

    IPackageManagerSpy(Object pm) {
        mRemote = pm;
    }

    private final Object mRemote;

    private PackageInfo getPackageInfo(Method method, Object[] args) throws Throwable {
        PackageInfo info = (PackageInfo) method.invoke(mRemote, args);

        if (! MY_PACKAGE_NAME.equals(args[0]) || info == null) {
            Log.i(TAG, "getPackageInfo: ignore param: " + args[0]);
            return info;
        }

        info.versionCode = FAKE_VERSION_CODE;
        info.versionName = FAKE_VERSION_NAME;

        if (info.signatures == null || info.signatures.length == 0) {
            Log.w(TAG, "getPackageInfo: signature is null");
            return info;
        }
        info.signatures[0] = new Signature(FAKE_SIGNATURE);
        return info;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        Log.i(TAG, "invoke: " + name);

        if ("getPackageInfo".equals(name)) {
            return getPackageInfo(method, args);
        }
        // 不要忘记调用原函数 ！！！
        return method.invoke(mRemote, args);
    }
}
