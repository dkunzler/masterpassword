package de.devland.masterpassword.model;

import android.database.Cursor;

import com.google.common.primitives.UnsignedInteger;
import com.google.gson.annotations.Expose;
import com.lyndir.masterpassword.MPSiteType;
import com.lyndir.masterpassword.MPSiteVariant;
import com.lyndir.masterpassword.MasterKey;
import com.lyndir.masterpassword.model.MPSite;
import com.lyndir.masterpassword.model.MPUser;
import com.orm.SugarRecord;

import java.util.Date;

import de.devland.masterpassword.util.MasterPasswordHolder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by David Kunzler on 23.08.2014.
 */
@Getter
@Setter
@NoArgsConstructor
public class Site extends SugarRecord {

    public static final String NOCASE_ORDER_SUFFIX = " COLLATE NOCASE";
    public static final String DESC_ORDER_SUFFIX = " DESC";

    public static final String ID = "ID";
    public static final String SITE_NAME = "SITE_NAME";
    public static final String USER_NAME = "USER_NAME";
    public static final String GENERATED_USER_NAME = "GENERATED_USER_NAME";
    public static final String SITE_COUNTER = "SITE_COUNTER";
    public static final String PASSWORD_TYPE = "PASSWORD_TYPE";
    public static final String ALGORITHM_VERSION = "ALGORITHM_VERSION";
    public static final String CATEGORY = "CATEGORY";
    public static final String LAST_USED = "LAST_USED";

    @Expose
    protected String siteName = "";
    @Expose
    protected String userName = "";
    @Expose
    protected boolean generatedUserName = false;
    @Expose
    protected int siteCounter = 0;
    @Expose
    protected MPSiteType passwordType = MPSiteType.GeneratedMaximum;
    @Expose
    protected MPSiteVariant passwordVariant = MPSiteVariant.Password;
    @Expose
    protected MasterKey.Version algorithmVersion = MasterKey.Version.CURRENT;
    @Expose
    protected Date lastUsed = new Date(0);
    @Expose
    protected String category;
    protected String storedPassword;

    public boolean complete() {
        boolean complete = true;
        if (siteName == null || siteName.isEmpty()) {
            complete = false;
        }
        return complete;
    }

    public void touch() {
        lastUsed = new Date();
        this.save();
    }

    public MPSite toMPSite(MPUser user) {
        MPSite mpSite = new MPSite(user, siteName, passwordType, UnsignedInteger.fromIntBits(siteCounter));
        if (generatedUserName) {
            String generatedUserName = MasterPasswordHolder.INSTANCE.generate(
                    MPSiteType.GeneratedName, MPSiteVariant.Login,
                    siteName, siteCounter, algorithmVersion);
            mpSite.setLoginName(generatedUserName);
        } else {
            mpSite.setLoginName(userName);
        }
        mpSite.setAlgorithmVersion(algorithmVersion);
        return mpSite;
    }

    public static Site fromMPSite(MPSite mpSite) {
        Site site = new Site();
        site.algorithmVersion = mpSite.getAlgorithmVersion();
        site.lastUsed = mpSite.getLastUsed().toDate();
        site.passwordType = mpSite.getSiteType();
        site.siteCounter = mpSite.getSiteCounter().intValue();
        site.siteName = mpSite.getSiteName();
        site.userName = mpSite.getLoginName();
        site.setPasswordVariant(MPSiteVariant.Password);
        return site;
    }

    public static Site fromCursor(Cursor cursor) {
        Site result = new Site();
        result.setSiteName(cursor.getString(cursor.getColumnIndex(SITE_NAME)));
        result.setUserName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
        result.setSiteCounter(cursor.getInt(cursor.getColumnIndex(SITE_COUNTER)));
        result.setPasswordType(MPSiteType.valueOf(cursor.getString(cursor.getColumnIndex(PASSWORD_TYPE))));
        result.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
        result.setId(cursor.getLong(cursor.getColumnIndex(ID)));
        return result;
    }
}
