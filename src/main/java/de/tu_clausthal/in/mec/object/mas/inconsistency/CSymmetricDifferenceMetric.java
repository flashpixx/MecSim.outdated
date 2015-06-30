package de.tu_clausthal.in.mec.object.mas.inconsistency;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * metric on collections returns the size of symmetric difference
 */
public class CSymmetricDifferenceMetric<T extends IAgent> implements IMetric<T>
{
    @Override
    public double calculate( final T p_first, final T p_second )
    {
        // equal objects create zero value
        if ( p_first.equals( p_second ) )
            return 0;

        final Set<ILiteral<?>> l_firstLiterals = p_first.getBeliefs().getLiterals();
        final Set<ILiteral<?>> l_secondLiterals = p_second.getBeliefs().getLiterals();

        // create aggregate belief-base
        final Set<ILiteral<?>> l_aggregate = new HashSet<ILiteral<?>>()
        {{
                addAll( l_firstLiterals );
                addAll( l_secondLiterals );
            }};


        // difference of contradiction is the sum of difference of contradictions on each belief-base (closed-world-assumption)
        return new Double( ( ( l_aggregate.size() - l_firstLiterals.size() ) + ( l_aggregate.size() - l_secondLiterals.size() ) ) );
    }
}
