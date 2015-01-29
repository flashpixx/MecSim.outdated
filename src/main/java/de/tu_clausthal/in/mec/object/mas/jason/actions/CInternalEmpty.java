/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.mas.jason.actions;


import jason.asSemantics.*;
import jason.asSyntax.Term;


/**
 * empty action to overwrite default behaviour
 */
public class CInternalEmpty extends DefaultInternalAction
{

    protected boolean m_result = true;
    protected int m_minimumarguments = super.getMinArgs();
    protected int m_maximumarguments = super.getMaxArgs();


    public CInternalEmpty()
    {
    }

    public CInternalEmpty( int p_min, int p_max, boolean p_result )
    {
        this.initialize( p_min, p_max, p_result );
    }

    public CInternalEmpty( int p_min, int p_max )
    {
        this.initialize( p_min, p_max, true );
    }

    public CInternalEmpty( boolean p_result )
    {
        this.initialize( 0, 0, p_result );
    }


    protected void initialize( int p_min, int p_max, boolean p_result )
    {
        m_minimumarguments = Math.abs( p_min );
        m_maximumarguments = Math.abs( p_max );
        m_result = p_result;
    }


    @Override
    public int getMinArgs()
    {
        return m_minimumarguments;
    }

    @Override
    public int getMaxArgs()
    {
        return m_maximumarguments;
    }

    @Override
    public Object execute( TransitionSystem p_ts, Unifier p_unifier, Term[] p_args ) throws Exception
    {
        return m_result;
    }

}
