package de.devland.masterpassword.util.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 27.11.2014.
 */
@Getter
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class ProStatusChangeEvent {
    private final boolean proStatus;
}
