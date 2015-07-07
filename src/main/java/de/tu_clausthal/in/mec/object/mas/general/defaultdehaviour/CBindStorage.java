package de.tu_clausthal.in.mec.object.mas.general.defaultdehaviour;

import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;


/**
 * binding belief storage
 */
public abstract class CBindStorage<N,M extends Iterable<N>> extends CImmutableBeliefStorage<N,M>
{
    /**
     * field filter
     */
    protected static final CFieldFilter c_filter = new CFieldFilter();

    /**
     * bind objects - map uses a name / annotation as key value and a pair of object and the map of fields and getter /
     * setter handles, so each bind can configurate individual
     */
    protected final Map<String, Pair<Object, Map<String, CReflection.CGetSet>>> m_bind = new HashMap<>();


    /**
     * default ctor
     */
    public CBindStorage()
    {
    }


    /**
     * ctor - bind an object
     *
     * @param p_name name / annotation of the bind object
     * @param p_object bind object
     */
    public CBindStorage( final String p_name, final Object p_object )
    {
        this.push( p_name, p_object );
    }


    /**
     * adds / binds an object
     *
     * @param p_name name / annotation of the object
     * @param p_object object
     */
    public final void push( final String p_name, final Object p_object )
    {
        m_bind.put(
                p_name, new ImmutablePair<Object, Map<String, CReflection.CGetSet>>(
                        p_object, CReflection.getClassFields(
                        p_object.getClass(), c_filter
                )
                )
        );
    }

    /**
     * removes an object from the bind
     *
     * @param p_name name
     */
    public final void removeBinding( final String p_name )
    {
        m_bind.remove( p_name );
    }


    @Override
    public final void update()
    {
        super.clear();
        super.update();
        this.updateBind();
    }


    /**
     * updates the element structure
     */
    protected abstract void updateBind();
}
