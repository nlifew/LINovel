package cn.nlifew.xqdreader.entity;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import cn.nlifew.xqdreader.bean.login.LoginBean;
import cn.nlifew.xqdreader.bean.login.LoginBean_V2;
import cn.nlifew.xqdreader.database.CommonDatabase;

@Entity(tableName = "account")
@SuppressWarnings("WeakerAccess")
public class Account {

    private static Account sAccount;
    private static boolean sShouldLoadFromDisk = true;
    public static Account currentAccount() {
        if (sShouldLoadFromDisk) {
            synchronized (Account.class) {
                if (sShouldLoadFromDisk) {
                    CommonDatabase db = CommonDatabase.getInstance();
                    Helper helper = db.getAccountHelper();
                    sAccount = helper.getSavedAccount();
                    sShouldLoadFromDisk = false;
                }
            }
        }
        return sAccount;
    }

    @Deprecated
    public static void saveAccount(Account account) {
        account.save();
    }


    public void setLoginBean(LoginBean bean) {
        Account account = this;

        account.appId = Integer.toString(bean.data.appId);
        account.areaId = Integer.toString(bean.data.areaId);
        account.autoLoginExpiredTime = bean.data.autoLoginExpiredTime;
        account.autoLoginFlag = bean.data.autoLoginFlag;
        account.autoLoginKeepTime = bean.data.autoLoginKeepTime;
        account.autoLoginSessionKey = bean.data.autoLoginSessionKey;
        account.inputUserId = bean.data.inputUserId;
        account.loginType = bean.data.loginType;
        account.sessionKey = bean.data.sessionKey;
        account.ticket = bean.data.ticket;
        account.ywGuid = Long.toString(bean.data.ywGuid);
        account.ywKey = bean.data.ywKey;
        account.ywOpenId = bean.data.ywOpenId;

    }

    public void setLoginBeanV2(LoginBean_V2 v2) {
        Account account = this;
        account.cmfuToken = v2.Data.CmfuToken;
        account.userInfo = v2.Data.UserInfo;
        account.headImage = v2.HeadImage;
    }

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String appId;
    public String areaId;
    public long autoLoginExpiredTime;
    public int autoLoginFlag;
    public long autoLoginKeepTime;
    public String autoLoginSessionKey;
    public String inputUserId;
    public int loginType;
    public String sessionKey;
    public String ticket;
    public String ywGuid;
    public String ywKey;
    public String ywOpenId;

    public String cmfuToken;
    public String userInfo;
    public String headImage;

    @Ignore
    private String[] userInfos;

    public void save() {
        synchronized (Account.class) {
            CommonDatabase db = CommonDatabase.getInstance();
            Helper helper = db.getAccountHelper();
            helper.deleteAll();
            helper.saveAccount(this);
            sAccount = this;
        }
    }

    private String getUserInfo(int index, String defValue) {
        if (userInfo == null) {
            return defValue;
        }
        if (userInfos == null) {
            userInfos = userInfo.split("\\|");
        }
        return index < userInfos.length ? userInfos[index] : defValue;
    }

    public String getUserName() {
        return getUserInfo(5, "未登录");
    }

    public String getUserId() {
        return getUserInfo(0, "0");
    }

    @Dao
    public interface Helper {

        @Query("SELECT * FROM account LIMIT 1")
        Account getSavedAccount();

        @Insert
        void saveAccount(Account account);

        @Query("DELETE FROM account")
        void deleteAll();

        @Update
        void update(Account account);
    }
}
