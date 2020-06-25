package cn.nlifew.xqdreader.bean.category;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

import cn.nlifew.xqdreader.bean.BeanSupport;

public class CategoryBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public FilterLineType[] FiltrLines;
        public OrderType[] Orders;
        public SiteType[] SiteList;
        public int SiteType;
        public int OrderType;

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (! (obj instanceof DataType)) {
                return false;
            }
            DataType o = (DataType) obj;
            return this.SiteType == o.SiteType
                    && Arrays.equals(this.FiltrLines, o.FiltrLines)
                    && Arrays.equals(this.Orders, o.Orders)
                    && Arrays.equals(this.SiteList, o.SiteList);
        }
    }

    public static final class FilterLineType {
        public FilterUnionType[] FilterUnions;
        public int Level;
        public String Tag;
        public int UnionType;

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (! (obj instanceof FilterLineType)) {
                return false;
            }
            FilterLineType o = (FilterLineType) obj;

            return this.Level == o.Level
                    && this.UnionType == o.UnionType
                    && Objects.equals(this.Tag, o.Tag)
                    && Arrays.equals(this.FilterUnions, o.FilterUnions);
        }
    }

    public static final class FilterUnionType {
        public ExtValueType[] Extvalue;
        public int Id;
        public String Name;
        public String SubTag;
        public int ExtType;

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return false;
            }
            if (! (obj instanceof FilterUnionType)) {
                return false;
            }
            FilterUnionType o = (FilterUnionType) obj;
            return this.Id == o.Id
                    && Objects.equals(this.Name, o.Name)
                    && Objects.equals(this.SubTag, o.SubTag)
                    && Arrays.equals(this.Extvalue, o.Extvalue);
        }
    }

    public static final class ExtValueType {
        public int Id;
        public String Name;

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (! (obj instanceof ExtValueType)) {
                return false;
            }
            ExtValueType o = (ExtValueType) obj;
            return this.Id == o.Id && Objects.equals(this.Name, o.Name);
        }
    }

    public static final class OrderType {
        public String ExtTag;
        public int Id;
        public String Name;

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (! (obj instanceof OrderType)) {
                return false;
            }
            OrderType o = (OrderType) obj;
            return this.Id == o.Id
                    && Objects.equals(this.ExtTag, o.ExtTag)
                    && Objects.equals(this.Name, o.Name);
        }
    }

    public static final class SiteType {
        public int Id;
        public String Name;

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (! (obj instanceof SiteType)) {
                return false;
            }
            SiteType o = (SiteType) obj;
            return this.Id == o.Id && Objects.equals(this.Name, o.Name);
        }
    }

    @Override
    public void trim() {
        if (Data == null) {
            return;
        }
        if (Data.SiteList != null && Data.SiteList.length > 1) {
            SiteType[] arr = new SiteType[2];
            arr[0] = Data.SiteList[0];
            arr[1] = Data.SiteList[1];
            Data.SiteList = arr;
        }
        if (Data.Orders != null && Data.Orders.length > 0) {
            Data.OrderType = Data.Orders[0].Id;
        }
        if (Data.FiltrLines != null) {
            for (FilterLineType filter : Data.FiltrLines) {
                filter.UnionType = filter.FilterUnions[0].Id;
                for (FilterUnionType union : filter.FilterUnions) {
                    if (union.Extvalue != null) {
                        union.ExtType = union.Extvalue[0].Id;
                    }
                }
            }
        }
    }
}
