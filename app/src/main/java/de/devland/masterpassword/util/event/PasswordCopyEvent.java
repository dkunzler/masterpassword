package de.devland.masterpassword.util.event;

import de.devland.masterpassword.ui.SiteCard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 12.10.2014.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class PasswordCopyEvent {
    @Getter
    private final SiteCard card;
}
