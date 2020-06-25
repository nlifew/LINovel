package cn.nlifew.xqdreader.bean.login;

import cn.nlifew.xqdreader.bean.BeanSupport;

public class LoginBean extends BeanSupport {
    public int code;
    public String message;
    public DataType data;

    public static final class DataType {
        public String alk;
        public String alkts;
        public int appId;
        public int areaId;
        public long autoLoginExpiredTime;
        public int autoLoginFlag;
        public long autoLoginKeepTime;
        public String autoLoginSessionKey;
        public String challenge;
        public String contextId;
        public String deviceDisplaytype;
        public String deviceType;
        public String imgSrc;
        public String inputUserId;
        public boolean isRiskAccount;
        public int loginType;
        public boolean needSecureCookie;
        public int nextAction;
        public String sessionKey;
        public String ticket;
        public long ywGuid;
        public String ywKey;
        public String ywOpenId;
    }
}
