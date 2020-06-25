package cn.nlifew.xqdreader.bean.ranking;

import cn.nlifew.xqdreader.bean.BeanSupport;

public class RankingCategoryBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType[] Data;

    public static final class DataType {
        public CategoryType[] CategoryList;
        public int SiteId;
    }

    public static final class CategoryType {
        public int Id;
        public String Name;
    }
}
