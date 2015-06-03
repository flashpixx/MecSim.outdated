package de.tu_clausthal.in.mec.object.mas.general;

import java.util.*;

/**
 * generic term list
 */
public abstract class ITermList extends LinkedList<ITerm> implements ITerm
{
    @Override
    public boolean instanceOf(Class<?> p_class)
    {
        return List.class.isAssignableFrom(p_class) && ITerm.class.isAssignableFrom( p_class );
    }
}
