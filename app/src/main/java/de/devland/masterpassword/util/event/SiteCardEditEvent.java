package de.devland.masterpassword.util.event;

import de.devland.masterpassword.ui.passwordlist.SiteCard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by deekay on 07.06.2015.
 */
@Getter
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class SiteCardEditEvent {
    private final SiteCard card;
}
