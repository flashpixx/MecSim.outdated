/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package de.tu_clausthal.in.mec.object.mas.jason.belief;


import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBaseMask;
import jason.asSyntax.Literal;


/**
 * beliefbase mask that represent the Jason beliefbase
 */
public class CMask extends de.tu_clausthal.in.mec.object.mas.generic.implementation.CMask<Literal>
{
    private final String m_separator;


    /**
     * ctor
     *
     * @param p_name name of the mask
     * @param p_beliefbase beliefbase
     */
    public CMask( final String p_name, final IBeliefBase<Literal> p_beliefbase, final String p_separator )
    {
        super( p_name, p_beliefbase );
        m_separator = p_separator;
    }

    /*
     * ctor
     *
     * @param p_name name of the mask
     * @param p_beliefbase beliefbase
     * @param p_parent parent mask
     */
    public CMask( final String p_name, final IBeliefBase<Literal> p_beliefbase,
            final IBeliefBaseMask<Literal> p_parent, final String p_separator
    )
    {
        super( p_name, p_beliefbase, p_parent );
        m_separator = p_separator;
    }

/*
    @Override
    public Iterator<Literal> getCandidateBeliefs( final PredicateIndicator p_predicateIndicator )
    {
        //return this.getLiteralIterator( this.getLiterals( this.splitPath( p_predicateIndicator.getFunctor() ) ).values().iterator() );
        return null;
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final Literal p_literal, final Unifier p_unifier )
    {
        if ( p_literal.isVar() )
            return this.iterator();

        // splits the functor of the literal and walk through the beliefbase tree
        final CPath l_path = this.splitPath( p_literal.getFunctor() );
        final Set<ILiteral<Literal>> l_literals = this.getLiterals( this.splitPath( l_path ).getSubPath( 0, -1 ) ).get( l_path );
        return l_literals == null ? null : this.getLiteralIterator( l_literals.iterator() );

    }

    @Override
    public Iterator<Literal> getRelevant( final Literal p_literal )
    {
        // deprecated
        return getCandidateBeliefs( p_literal, null );
    }

    @Override
    public boolean remove( final Literal p_literal )
    {
        final Pair<CPath, CLiteral> l_split = this.cloneLiteral( p_literal );
        this.remove( l_split.getLeft(), l_split.getRight() );
        return true;
    }

    @Override
    public boolean abolish( final PredicateIndicator p_predicateIndicator )
    {
        // remove masks and literals
        this.remove( this.splitPath( p_predicateIndicator.getFunctor() ) );
        return true;
    }

*/


    @Override
    public IBeliefBaseMask<Literal> clone( final IBeliefBaseMask<Literal> p_parent )
    {
        return new CMask( m_name, m_beliefbase, p_parent, m_separator );
    }

}
