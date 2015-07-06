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

package de.tu_clausthal.in.mec.object.mas.jason.general;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import de.tu_clausthal.in.mec.object.mas.general.Old_CDefaultBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.Old_IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.Old_IBeliefBaseElement;
import jason.asSyntax.Literal;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * class for agent beliefbase
 *
 * @todo create generic call and move it to the mas/general package
 * @deprecated
 */
@Deprecated
public class CBeliefBase extends Old_CDefaultBeliefBase<Literal>
{
    /**
     * ctor - default
     */
    public CBeliefBase()
    {
        super();
    }

    /**
     * ctor - just the top-level literals are specified
     *
     * @param p_literals top level literals
     */
    public CBeliefBase( final Set<ILiteral<Literal>> p_literals )
    {
    }

    /**
     * ctor - top-level literals and inherited getBeliefbases are specified
     *
     * @param p_beliefbases inherited getBeliefbases
     * @param p_literals top level literals
     */
    public CBeliefBase( final Map<String, Old_IBeliefBase<Literal>> p_beliefbases, final Set<ILiteral<Literal>> p_literals, final CPath p_path )
    {

    }

    @Override
    public boolean add( final Old_IBeliefBaseElement p_element )
    {
        return false;
    }

    @Override
    public void clear( final CPath... p_path )
    {

    }

    @Override
    public Old_IBeliefBase get( final CPath p_path )
    {
        return null;
    }

    @Override
    public Map<String, Old_IBeliefBase<Literal>> getBeliefbases( final CPath... p_path )
    {
        return null;
    }

    @Override
    public Set<Old_IBeliefBaseElement> getElements( final CPath p_path, final String p_name, final Class<?> p_class )
    {
        return null;
    }

    @Override
    public Map<String, Map<Class<?>, Set<Old_IBeliefBaseElement>>> getElements( final CPath p_path )
    {
        return null;
    }

    @Override
    public Set<ILiteral<Literal>> getLiterals( final CPath... p_path )
    {
        return null;
    }

    @Override
    public Old_IBeliefBase getOrDefault( final CPath p_path, final Old_IBeliefBase<Literal> p_beliefbase )
    {
        return null;
    }

    @Override
    public CPath getPath()
    {
        return null;
    }

    @Override
    public boolean isBeliefbase()
    {
        return false;
    }

    @Override
    public boolean isLiteral()
    {
        return false;
    }

    @Override
    public Iterator<ILiteral<Literal>> iterator()
    {
        return null;
    }

    @Override
    public void setPath( final CPath p_path )
    {

    }

    @Override
    public boolean remove( final CPath p_path, final Old_IBeliefBaseElement p_element )
    {
        return false;
    }

    @Override
    public void update()
    {

    }
}
