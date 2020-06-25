package cn.nlifew.xqdreader.bean.login;

import cn.nlifew.xqdreader.bean.BeanSupport;

public class LoginBean_V2 extends BeanSupport {
    public int Result;
    public String Message;
    public Object MZTNotice;    // todo: 空数组
    public String Reward;
    public int isNewUser;
    public String HeadImage;
    public DataType Data;

    public static final class DataType {
        public String CmfuToken;
        public String UserInfo;
    }
}
