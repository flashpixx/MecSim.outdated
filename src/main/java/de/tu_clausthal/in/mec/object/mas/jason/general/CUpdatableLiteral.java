package de.tu_clausthal.in.mec.object.mas.jason.general;

import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.general.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcel on 03.06.15.
 */
public class CUpdatableLiteral extends CLiteral implements IUpdatableLiteral
{
    /**
     * field filter
     */
    private static final CFieldFilter c_filter = new CFieldFilter();
    /**
     * bind objects - map uses a name / annotation as key value and a pair of object and the map of fields and getter /
     * setter handles, so each bind can configurate individual
     */
    private final Map<String, Pair<Object, Map<String, CReflection.CGetSet>>> m_bind = new HashMap<>();
    /**
     * set with literals
     */
    private final ITermList m_literals = new CTermList();

    /**
     * default ctor - a literal at least contains a functor
     *
     * @param p_functor
     */
    public CUpdatableLiteral(String p_functor)
    {
        super(p_functor);
    }


    @Override
    public void clear()
    {

    }

    @Override
    public void update()
    {

    }

}
