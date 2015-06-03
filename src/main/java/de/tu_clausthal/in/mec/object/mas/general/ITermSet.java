package de.tu_clausthal.in.mec.object.mas.general;

import java.util.HashSet;
import java.util.Set;

/**
 * generic set type
 */
public abstract class ITermSet extends HashSet<ITerm> implements ITerm
{
    @Override
    public boolean instanceOf(Class<?> p_class)
    {
        return Set.class.isAssignableFrom( p_class ) && ITerm.class.isAssignableFrom( p_class );
    }
}
