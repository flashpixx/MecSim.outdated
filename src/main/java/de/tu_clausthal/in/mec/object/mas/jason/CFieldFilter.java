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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.common.CReflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;


/**
 * class to filter object fields *
 */
public class CFieldFilter implements CReflection.IFieldFilter
{

    /**
     * set with forbidden field names *
     */
    protected Set<String> m_forbiddennames = new HashSet<>();

    /**
     * ctor *
     */
    public CFieldFilter()
    {
    }

    /**
     * ctor
     *
     * @param p_names set with forbidden field names
     */
    public CFieldFilter( Set<String> p_names )
    {
        m_forbiddennames.addAll( p_names );
    }

    @Override
    public boolean filter( Field p_field )
    {
        return !( m_forbiddennames.contains( p_field.getName() ) ) || ( Modifier.isStatic( p_field.getModifiers() ) ) || ( Modifier.isInterface( p_field.getModifiers() ) ) ||
                ( Modifier.isAbstract( p_field.getModifiers() ) ) || ( Modifier.isFinal( p_field.getModifiers() ) );
    }

}