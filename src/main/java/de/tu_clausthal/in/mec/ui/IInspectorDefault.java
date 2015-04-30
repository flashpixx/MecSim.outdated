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
    private final Map<String, Object> m_inspect = new HashMap()
    {{
            put( CCommon.getResourceString( IInspectorDefault.class, "classname" ), CCommon.removePackageName( this.getClass().getName() ) );
            put( CCommon.getResourceString( IInspectorDefault.class, "objectid" ), this.hashCode() );
        }};


    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

}
