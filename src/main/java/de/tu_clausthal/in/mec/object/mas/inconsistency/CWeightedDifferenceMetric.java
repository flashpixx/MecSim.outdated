package de.tu_clausthal.in.mec.object.mas.inconsistency;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;

import java.util.HashSet;
import java.util.Set;

/**
 * User: marcel
 * Date: 6/30/15
 */
public class CWeightedDifferenceMetric<T extends IAgent> extends IDefaultMetric<T>
{
    @Override
    public double calculate( T p_first, T p_second )
    {
        // equal objects create zero value
        if ( p_first.equals( p_second ) )
            return 0;

        final Set<ILiteral<?>> l_firstLiterals = new HashSet<>();
        final Set<ILiteral<?>> l_secondLiterals = new HashSet<>();

        for ( final CPath l_path : m_paths )
        {
            l_firstLiterals.addAll( p_first.getBeliefs().getLiterals( l_path ) );
            l_secondLiterals.addAll( p_second.getBeliefs().getLiterals( l_path ) );
        }

        final Set<ILiteral<?>> l_set = new HashSet<ILiteral<?>>()
        {{
                addAll( l_firstLiterals );
                addAll( l_secondLiterals );
            }};

        final int l_unionSize = l_set.size();

        l_set.retainAll(l_firstLiterals);
        l_set.retainAll(l_secondLiterals);

        final int l_intersectionSize = l_set.size();

        return new Double( ( ( l_set.size() - l_firstLiterals.size() ) +
                             ( l_set.size() - l_secondLiterals.size() ) ) * l_unionSize / l_intersectionSize );
    }
}
