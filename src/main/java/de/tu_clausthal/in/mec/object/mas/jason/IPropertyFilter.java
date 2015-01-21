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

import java.lang.reflect.Field;


/**
 * Created by pkraus on 20.01.15.
 */
public class IPropertyFilter
{

    /**
     * method to filter properties for the environment
     *
     * @param p_object   object that is referenced to bind the field
     * @param p_property fild which should be bind
     * @return true / false to bind field
     */
    public boolean filter( Object p_object, Field p_property )
    {
        return true;
    }


    /**
     * method to filter properties for an agent
     *
     * @param p_agent    agent that will be used for the bind
     * @param p_object   object that is referenced to bind the field
     * @param p_property fild which should be bind
     * @return true / false to bind field
     */
    public boolean filter( CAgentArchitecture p_agent, Object p_object, Field p_property )
    {
        return true;
    }

}
