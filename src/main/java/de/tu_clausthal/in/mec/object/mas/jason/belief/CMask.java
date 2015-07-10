/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
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


import de.tu_clausthal.in.mec.object.mas.general.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.PredicateIndicator;
import jason.bb.BeliefBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;


/**
 * beliefbase mask that represent the Jason beliefbase
 */
public class CMask extends de.tu_clausthal.in.mec.object.mas.general.implementation.CMask<Literal> implements BeliefBase
{

    /**
     * ctor
     *
     * @param p_name name of the mask
     * @param p_beliefbase reference to the beliefbase context
     */
    public CMask( final String p_name, final IBeliefBase<Literal> p_beliefbase )
    {
        super( p_name, p_beliefbase );
    }

    /**
     * private ctor
     *
     * @param p_name name of the mask
     * @param p_beliefbase reference to the beliefbase context
     * @param p_parent reference to the parent mask
     */
    public CMask( final String p_name, final IBeliefBase<Literal> p_beliefbase,
            final IBeliefBaseMask<Literal> p_parent
    )
    {
        super( p_name, p_beliefbase, p_parent );
    }

    @Override
    public void init( final Agent ag, final String[] args )
    {

    }

    @Override
    public void stop()
    {

    }

    @Override
    public boolean add( final Literal l )
    {
        return false;
    }

    @Override
    public boolean add( final int index, final Literal l )
    {
        return false;
    }

    @Override
    public Iterator<Literal> getAll()
    {
        return null;
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final PredicateIndicator p_predicateIndicator )
    {
        return null;
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final Literal p_literal, final Unifier p_unifier )
    {
        return null;
    }

    @Override
    public Iterator<Literal> getRelevant( final Literal p_literal )
    {
        return null;
    }

    @Override
    public Literal contains( final Literal p_literal )
    {
        return null;
    }

    @Override
    public Iterator<Literal> getPercepts()
    {
        return null;
    }

    @Override
    public boolean remove( final Literal p_literal )
    {
        return false;
    }

    @Override
    public boolean abolish( final PredicateIndicator p_predicateIndicator )
    {
        return false;
    }

    @Override
    public Element getAsDOM( final Document p_document )
    {
        return null;
    }

    @Override
    public BeliefBase clone()
    {
        return null;
    }

}
