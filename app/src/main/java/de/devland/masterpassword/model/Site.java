package de.devland.masterpassword.model;

import com.lyndir.lhunath.masterpassword.MPElementType;
import com.orm.SugarRecord;

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
    protected String siteName = "";
    protected String userName = "";
    protected int siteCounter = 0;
    protected MPElementType passwordType = MPElementType.GeneratedMaximum;
}
