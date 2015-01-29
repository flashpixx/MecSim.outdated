package de.tu_clausthal.in.mec.object.mas.jason;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * default filter for object properties, only public properties are accepted
 *
 * @deprecated
 */
public class CDefaultFieldFilter extends IPropertyFilter
{

    @Override
    public boolean filter( Object p_object, Field p_property )
    {
        return Modifier.isPublic( p_property.getModifiers() );
    }

    @Override
    public boolean filter( CAgent p_agent, Object p_object, Field p_property )
    {
        return Modifier.isPublic( p_property.getModifiers() );
    }

}
