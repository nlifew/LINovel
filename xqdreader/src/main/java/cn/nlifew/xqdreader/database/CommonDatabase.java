package cn.nlifew.xqdreader.database;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cn.nlifew.xqdreader.XQDReader;
import cn.nlifew.xqdreader.entity.Account;
import cn.nlifew.xqdreader.entity.BookShelf;
import cn.nlifew.xqdreader.entity.ReadingRecord;

@Database(entities = {
        Account.class, ReadingRecord.class, BookShelf.class,
        }, version = 1, exportSchema = false)
public abstract class CommonDatabase extends RoomDatabase {
    private static final String TAG = "CommonDatabase";

    public static CommonDatabase getInstance() {
        if (sInstance == null) {
            synchronized (CommonDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room
                            .databaseBuilder(XQDReader.sContext, CommonDatabase.class, "common.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return sInstance;
    }


    private static CommonDatabase sInstance;

    public abstract Account.Helper getAccountHelper();
    public abstract ReadingRecord.Helper getReadingRecordHelper();
    public abstract BookShelf.Helper getBookShiftHelper();
}
