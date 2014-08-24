package de.devland.masterpassword.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lyndir.lhunath.masterpassword.MPElementType;
import com.orm.Database;
import com.orm.SugarApp;
import com.orm.SugarDb;
import com.orm.SugarRecord;

import de.devland.masterpassword.App;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by David Kunzler on 23.08.2014.
 */
@Getter
@Setter
@NoArgsConstructor
public class Site extends SugarRecord<Site> {

    public static final String ID = "ID";
    public static final String SITE_NAME = "SITE_NAME";
    public static final String USER_NAME = "USER_NAME";
    public static final String SITE_COUNT = "SITE_COUNTER";
    public static final String PASSWORD_TYPE = "PASSWORD_TYPE";

    protected String siteName = "";
    protected String userName = "";
    protected int siteCounter = 0;
    protected MPElementType passwordType = MPElementType.GeneratedMaximum;

    public static Cursor findAll() {
        Database database = App.getDb();
        SQLiteDatabase sqLiteDatabase = database.getDB();
        Cursor c = sqLiteDatabase.rawQuery("select rowid _id, * from SITE order by SITE_NAME", null);
        return c;
    }

    public boolean complete() {
        boolean complete = true;
        if (siteName == null || siteName.isEmpty()) {
            complete = false;
        }
        if (userName == null || userName.isEmpty()) {
            complete = false;
        }
        return complete;
    }
}
