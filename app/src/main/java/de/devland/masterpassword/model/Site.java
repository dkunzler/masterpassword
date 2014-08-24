package de.devland.masterpassword.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lyndir.lhunath.masterpassword.MPElementType;
import com.orm.Database;
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
    public static final String SITE_COUNTER = "SITE_COUNTER";
    public static final String PASSWORD_TYPE = "PASSWORD_TYPE";
    public static final String CATEGORY = "CATEGORY";

    protected String siteName = "";
    protected String userName = "";
    protected int siteCounter = 0;
    protected MPElementType passwordType = MPElementType.GeneratedMaximum;

    protected String category;

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

    public static Site fromCursor(Cursor cursor) {
        Site result = new Site();
        result.setSiteName(cursor.getString(cursor.getColumnIndex(SITE_NAME)));
        result.setUserName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
        result.setSiteCounter(cursor.getInt(cursor.getColumnIndex(SITE_COUNTER)));
        result.setPasswordType(MPElementType.valueOf(cursor.getString(cursor.getColumnIndex(PASSWORD_TYPE))));
        result.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
        result.setId(cursor.getLong(cursor.getColumnIndex(ID)));
        return result;
    }
}
