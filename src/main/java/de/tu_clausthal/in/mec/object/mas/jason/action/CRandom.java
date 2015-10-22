package de.tu_clausthal.in.mec.object.mas.jason.action;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.EDistribution;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * internal action to use a complex random generator
 */
public final class CRandom extends DefaultInternalAction
{
    /**
     * map containing distributions which can be accessed by keys
     * as triples of distribution name, first and second parameter
     */
    private final Map<Triple<EDistribution, Double, Double>, AbstractRealDistribution> m_distributions = new ConcurrentHashMap<>();

    @Override
    public int getMinArgs()
    {
        return 3;
    }

    @Override
    public int getMaxArgs()
    {
        return 4;
    }

    @Override
    public Object execute( final TransitionSystem p_transitionsystem, final Unifier p_unifier, final Term[] p_args ) throws Exception
    {
        if ( ( !p_args[0].isString() ) || ( !p_args[1].isString() ) || ( !p_args[2].isNumeric() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "arguments" ) );

        return p_unifier.unifies(
                p_args[p_args.length - 1],
                EOption.valueOf( p_args[0].toString() ).get(
                        this,
                        p_args[1].toString(),
                        ( (NumberTerm) p_args[1] ).solve(),
                        ( p_args.length > 2 ) && ( p_args[2].isNumeric() ) ? ( (NumberTerm) p_args[2] ).solve() : 0
                )
        );
    }

    /**
     * returns the distribution associated with the distirbution definition
     *
     * @param p_name name of the distribution (match to the enum)
     * @param p_arg1 first argument of the distribution
     * @param p_arg2 second argument of the distribution
     * @return random value
     */
    private Double getDistribution( final String p_name, final double p_arg1, final double p_arg2 )
    {
        final Triple<EDistribution, Double, Double> l_key = new ImmutableTriple<>(
                EDistribution.valueOf( p_name ),
                p_arg1,
                p_arg2
        );

        return m_distributions.putIfAbsent(
                l_key,
                m_distributions.getOrDefault( l_key, l_key.getLeft().get( l_key.getMiddle(), l_key.getRight() ) )
        ).sample();
    }


    /**
     * enum to define the option of action
     */
    private enum EOption
    {
        Value,
        Expection;

        /**
         * returns the value of the option
         *
         * @param p_object random object
         * @param p_distribution distribution name
         * @param p_args number arguments
         * @return value term
         */
        public NumberTermImpl get( final CRandom p_object, final String p_distribution, final double... p_args )
        {
            switch ( this )
            {
                case Value:
                    return new NumberTermImpl(
                            p_object.getDistribution(
                                    p_distribution,
                                    p_args[0],
                                    p_args.length > 1 ? p_args[1] : 0
                            )
                    );

                case Expection:
                    return new NumberTermImpl(
                            EDistribution.valueOf( p_distribution ).getExpectation(
                                    p_args[0],
                                    p_args.length > 1 ? p_args[1] : 0
                            )
                    );

                default:
                    throw new IllegalStateException( CCommon.getResourceString( EOption.class, "unknownoption" ) );

            }
        }
    }
}
