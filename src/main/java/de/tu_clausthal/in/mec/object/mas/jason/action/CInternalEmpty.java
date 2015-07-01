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

package de.tu_clausthal.in.mec.object.mas.jason.action;


import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;


/**
 * empty action to overwrite default behaviour
 */
public class CInternalEmpty extends DefaultInternalAction
{

    /**
     * maximum number of arguments *
     */
    private final int m_maximumarguments;
    /**
     * minimum number of arguments *
     */
    private final int m_minimumarguments;
    /**
     * default result value *
     */
    private final boolean m_result;


    /**
     * default ctor
     */
    public CInternalEmpty()
    {
        m_minimumarguments = super.getMinArgs();
        m_maximumarguments = super.getMaxArgs();
        m_result = true;
    }


    /**
     * ctor
     *
     * @param p_min minimum of arguments
     * @param p_max maximum of arguments
     */
    public CInternalEmpty( final int p_min, final int p_max )
    {
        this( p_min, p_max, true );
    }


    /**
     * ctor
     *
     * @param p_min minimum of arguments
     * @param p_max maximum of arguments
     * @param p_result result value
     */
    public CInternalEmpty( final int p_min, final int p_max, final boolean p_result )
    {
        m_minimumarguments = Math.abs( p_min );
        m_maximumarguments = Math.abs( p_max );
        m_result = p_result;
    }


    /**
     * ctor
     *
     * @param p_result result value
     */
    public CInternalEmpty( final boolean p_result )
    {
        this( 0, 0, p_result );
    }


    @Override
    public final int getMinArgs()
    {
        return m_minimumarguments;
    }

    @Override
    public final int getMaxArgs()
    {
        return m_maximumarguments;
    }

    @Override
    public final Object execute( final TransitionSystem p_transitionsystem, final Unifier p_unifier, final Term[] p_args ) throws Exception
    {
        return m_result;
    }

}
