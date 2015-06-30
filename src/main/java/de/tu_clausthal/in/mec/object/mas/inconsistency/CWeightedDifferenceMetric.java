package de.tu_clausthal.in.mec.object.mas.inconsistency;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;

import java.util.HashSet;
import java.util.Set;

/**
 * Metric implementation for agents. Calculates the distance with respect
 * to size of union and intersection of beliefbases.
 */
public class CWeightedDifferenceMetric<T extends IAgent> extends IDefaultMetric<T>
{
    /**
     * ctor
     *
     * @param p_paths path list
     */
    public CWeightedDifferenceMetric( final CPath... p_paths )
    {
        super( p_paths );
    }


    /**
     * copy-ctor
     *
     * @param p_metric metric
     */
    public CWeightedDifferenceMetric( final IDefaultMetric<T> p_metric )
    {
        super( p_metric );
    }

    @Override
    public double calculate( final T p_first, final T p_second )
    {
        // equal objects create zero value
        if ( p_first.equals( p_second ) )
            return 0;

        // collect all literals within specified paths
        final Set<ILiteral<?>> l_firstLiterals = new HashSet<>();
        final Set<ILiteral<?>> l_secondLiterals = new HashSet<>();
        for ( final CPath l_path : m_paths )
        {
            l_firstLiterals.addAll( p_first.getBeliefs().getLiterals( l_path ) );
            l_secondLiterals.addAll( p_second.getBeliefs().getLiterals( l_path ) );
        }

        // get size of union
        final Set<ILiteral<?>> l_set = new HashSet<ILiteral<?>>()
        {{
                addAll( l_firstLiterals );
                addAll( l_secondLiterals );
            }};
        final int l_unionSize = l_set.size();

        // get size of intersection
        l_set.retainAll(l_firstLiterals);
        l_set.retainAll(l_secondLiterals);
        final int l_intersectionSize = l_set.size();

        // return distance
        return new Double( ( ( l_unionSize - l_firstLiterals.size() ) +
                             ( l_unionSize - l_secondLiterals.size() ) ) * l_unionSize / l_intersectionSize );
    }
}
