package de.tu_clausthal.in.mec.object.mas.general;

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
}
