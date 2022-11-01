package de.devland.masterpassword.util.event;

import de.devland.masterpassword.model.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by deekay on 03/11/14.
 */
@Getter
@RequiredArgsConstructor
public class CategoryChangeEvent {
    private final Category category;
}
