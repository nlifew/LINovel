package cn.nlifew.xqdreader.bean.ranking;

import cn.nlifew.xqdreader.bean.BeanSupport;

public class RankingGroupBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType[] Data;

    public static final class DataType {
        public String ActionUrl;
        public int DefaultTopId;
        public String Name;
        public SubItemType[] SubItems;
        public String Unit;
    }

    public static final class SubItemType {
        public String Desc;
        public String Name;
        public int TopId;
    }
}
