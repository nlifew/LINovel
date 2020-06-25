package cn.nlifew.xqdreader.utils;

import cn.nlifew.xqdreader.bean.BookShelfBean;
import cn.nlifew.xqdreader.entity.BookShelf;

public final class ShelfUtils {

    public static String empty() {
        return "{\"NewCate\":[],\"EditCate\":[],\"DelCate\":[],\"NewBook\":[],\"EditBook\":[],\"DelBook\":[]}";
    }

    public static String removeBook(int bookId) {
        StringBuilder sb = Utils.obtainStringBuilder(180)
                .append("{\"NewCate\":[],\"EditCate\":[],\"DelCate\":[],\"NewBook\":[],\"EditBook\":[],\"DelBook\":[{")
                .append("\"BId\":").append(bookId).append(',')
                .append("\"OpTime\":").append(System.currentTimeMillis()).append(',')
                .append("\"BookType\":1}]}");
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }

    public static String addBook(int bookId) {
        StringBuilder sb = Utils.obtainStringBuilder(180)
                .append("{\"NewCate\":[],\"EditCate\":[],\"DelCate\":[],\"NewBook\":[{")
                .append("\"CId\":0,\"BId\":").append(bookId).append(',')
                .append("\"Ref\":0,\"OpTime\":").append(System.currentTimeMillis()).append(',')
                .append("\"IsTop\":0,\"BookType\":1}],\"EditBook\":[],\"DelBook\":[]}");
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }
}

