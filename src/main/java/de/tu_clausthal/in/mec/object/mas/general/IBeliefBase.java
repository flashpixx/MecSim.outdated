package de.tu_clausthal.in.mec.object.mas.general;

import java.util.Map;
import java.util.Set;

/**
 * beliefbase interface
 */
public interface IBeliefBase<T>
{
    /**
     * collapse method to aggregate the beliefbase
     *
     * @return beliefbase containing literals of all inherited beliefbases
     */
    public IBeliefBase<T> collapseBeliefbase();

    /**
     * returns the aggregated beliefbase which also contains
     * the literals of the inherited beliefbases
     */
    public Set<ILiteral<T>> collapseLiterals();

    /**
     * getter for literal set
     */
    public Set<ILiteral<T>> getLiterals();

    /**
     * getter for inherited beliefbases
     */
    public Map<String, IBeliefBase<T>> getBeliefbases();

    /**
     * removes the literals of the top level and the inherited beliefbases
     */
    public void clear();
}
