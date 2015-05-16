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

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.CMethodFilter;

import java.util.HashMap;
import java.util.Map;


/**
 * global default implementation to get information about a simulation object
 */
public abstract class IInspectorDefault extends IUIListener implements IInspector
{
    /**
     * inspect variable *
     */
    @CFieldFilter.CAgent( bind = false )
    private final Map<String, Object> m_inspect = new HashMap<>();
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;

    @Override
    @CMethodFilter.CAgent( bind = false )
    public Map<String, Object> inspect()
    {
        // data must be added in the method, because the instantiation of the map is called before the child object is instantiate,
        // so to get the correct data must be read during method call, to avoid calling new strings we check the key first
        if ( !m_inspect.containsKey( CCommon.getResourceString( IInspectorDefault.class, "objectid" ) ) )
            m_inspect.put( CCommon.getResourceString( IInspectorDefault.class, "objectid" ), this.hashCode() );

        if ( !m_inspect.containsKey( CCommon.getResourceString( IInspectorDefault.class, "classname" ) ) )
            m_inspect.put( CCommon.getResourceString( IInspectorDefault.class, "classname" ), CCommon.removePackageName( this.getClass().getName() ) );

        return m_inspect;
    }

}
