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
 **/

package de.tu_clausthal.in.mec.ui;


import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.common.CCommon;

import java.util.HashMap;
import java.util.Map;


/**
 * class to get access to the about data
 */
public class CAbout
{
    /**
     * map with about data *
     */
    private final HashMap<String, String> m_data = new HashMap<>();


    /**
     * ctor
     */
    public CAbout()
    {
        m_data.put( "projectname", this.getEmpty( CConfiguration.getInstance().getManifest().get( "Project-Name" ) ) );
        m_data.put( "projecturl", this.getEmpty( CConfiguration.getInstance().getManifest().get( "Project-URL" ) ) );

        m_data.put( "buildversion_title", "Build-Version" );
        m_data.put( "buildversion_value", this.getEmpty( CConfiguration.getInstance().getManifest().get( "Build-Version" ) ) );

        m_data.put( "buildnumber_title", CCommon.getResourceString( this, "buildnumber" ) );
        m_data.put( "buildnumber_value", this.getEmpty( CConfiguration.getInstance().getManifest().get( "Build-Number" ) ) );

        m_data.put( "buildcommit_title", CCommon.getResourceString( this, "buildcommit" ) );
        String l_commit = this.getEmpty( CConfiguration.getInstance().getManifest().get( "Build-Commit" ) );
        if ( !l_commit.isEmpty() ) l_commit = l_commit.substring( 0, Math.min( 9, l_commit.length() ) );
        m_data.put( "buildcommit_value", l_commit );

        m_data.put( "license_title", CCommon.getResourceString( this, "license" ) );
        m_data.put( "license_value", this.getEmpty( CConfiguration.getInstance().getManifest().get( "License" ) ) );
        m_data.put( "license_url", this.getEmpty( CConfiguration.getInstance().getManifest().get( "License-URL" ) ) );
    }


    /**
     * UI method  to get access to the data
     *
     * @return map with about data
     */
    private Map<String, String> web_static_get()
    {
        return m_data;
    }

    /**
     * returns an empty string if the input is null
     *
     * @param p_input input string
     * @return string
     */
    private String getEmpty( final String p_input )
    {
        return p_input == null ? "" : p_input;
    }

}
