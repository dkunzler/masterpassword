package de.devland.masterpassword.model;

import android.content.Context;

import de.devland.masterpassword.R;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 28/08/14.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Category implements Comparable<Category> {
    private final String name;

    public static Category all(Context context) {
        return new Category(context.getString(R.string.category_all));
    }

    @Override
    public int compareTo(Category that) {
        return this.name.compareTo(that.name);
    }
}
