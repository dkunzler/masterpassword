package com.lyndir.lhunath.masterpassword;

import android.content.res.Resources;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.sf.plist.*;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import de.devland.masterpassword.App;
import de.devland.masterpassword.R;


/**
 * <i>07 04, 2012</i>
 *
 * @author lhunath
 */
public class MPTemplates extends MetaObject {

    static final Logger logger = Logger.get( MPTemplates.class );

    private final Map<MPElementType, List<MPTemplate>> templates;

    public MPTemplates(final Map<MPElementType, List<MPTemplate>> templates) {

        this.templates = templates;
    }

    public static MPTemplates load() {

        return loadFromPList();
    }

    // changed to use android raw loading mechanismn
    // TODO use android arrays instead
    public static MPTemplates loadFromPList() {

        @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
        Resources res = App.get().getResources();
        InputStream templateStream = res.openRawResource(R.raw.ciphers);
        Preconditions.checkNotNull( templateStream, "Not found: ciphers.plist" );
        try {
            NSObject plistObject = PropertyListParser.parse( templateStream );
            Preconditions.checkState( NSDictionary.class.isAssignableFrom( plistObject.getClass() ) );
            NSDictionary plist = (NSDictionary) plistObject;

            NSDictionary characterClassesDict = (NSDictionary) plist.get( "MPCharacterClasses" );
            NSDictionary templatesDict = (NSDictionary) plist.get( "MPElementGeneratedEntity" );

            ImmutableMap.Builder<Character, MPTemplateCharacterClass> characterClassesBuilder = ImmutableMap.builder();
            for (final Map.Entry<String, NSObject> characterClassEntry : characterClassesDict.entrySet()) {
                String key = characterClassEntry.getKey();
                NSObject value = characterClassEntry.getValue();
                Preconditions.checkState( key.length() == 1 );
                Preconditions.checkState( NSString.class.isAssignableFrom( value.getClass() ));

                char character = key.charAt( 0 );
                char[] characterClass = ((NSString)value).getValue().toCharArray();
                characterClassesBuilder.put( character, new MPTemplateCharacterClass( character, characterClass ) );
            }
            ImmutableMap<Character, MPTemplateCharacterClass> characterClasses = characterClassesBuilder.build();

            ImmutableMap.Builder<MPElementType, List<MPTemplate>> templatesBuilder = ImmutableMap.builder();
            for (final Map.Entry<String, NSObject> template : templatesDict.entrySet()) {
                String key = template.getKey();
                NSObject value = template.getValue();
                Preconditions.checkState( NSArray.class.isAssignableFrom( value.getClass() ) );

                MPElementType type = MPElementType.forName( key );
                List<NSObject> templateStrings = ((NSArray) value).getValue();

                ImmutableList.Builder<MPTemplate> typeTemplatesBuilder = ImmutableList.<MPTemplate>builder();
                for (final NSObject templateString : templateStrings)
                    typeTemplatesBuilder.add( new MPTemplate( ((NSString) templateString).getValue(), characterClasses ) );

                templatesBuilder.put( type, typeTemplatesBuilder.build() );
            }
            ImmutableMap<MPElementType, List<MPTemplate>> templates = templatesBuilder.build();

            return new MPTemplates( templates );
        }
        catch (PropertyListException e) {
            logger.err( e, "Could not parse templates from: ciphers.plist");
            throw Throwables.propagate( e );
        }
        catch (IOException e) {
            logger.err( e, "Could not read templates from: ciphers.plist");
            throw Throwables.propagate( e );
        }
        finally {
            Closeables.closeQuietly( templateStream );
        }
    }

    public MPTemplate getTemplateForTypeAtRollingIndex(final MPElementType type, final int templateIndex) {

        List<MPTemplate> typeTemplates = templates.get( type );

        return typeTemplates.get( templateIndex % typeTemplates.size() );
    }

    public static void main(final String... arguments) {

        load();
    }
}
