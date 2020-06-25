package cn.nlifew.xqdreader.bean.login;

import cn.nlifew.xqdreader.bean.BeanSupport;

@SuppressWarnings("unused")
public class SmsBean extends BeanSupport {
    public int code;
    public String message;
    public String st;
    public String sk;
    public DataType data;

    public static final class DataType {
        public String alk;
        public String alkts;
        public String imgSrc;
        public boolean needSecureCookie;
        public int nextAction;
        public String sessionKey;
    }
}
