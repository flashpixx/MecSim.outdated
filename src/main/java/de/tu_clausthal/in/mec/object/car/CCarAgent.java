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

package de.tu_clausthal.in.mec.object.car;


import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import org.jxmapviewer.viewer.GeoPosition;


/**
 * agent car
 */
public class CCarAgent extends CDefaultCar
{

    /**
     * agent object *
     */
    protected CAgent<CDefaultCar> m_agent = null;


    /**
     * ctor to create the initial values
     *
     * @param p_asl           agent ASL file
     * @param p_StartPosition start positions (position of the source)
     */
    public CCarAgent( String p_asl, GeoPosition p_StartPosition )
    {
        super( p_StartPosition );

        try
        {
            m_agent = new CAgent( p_asl, this );

            ( (IMultiLayer) CSimulation.getInstance().getWorld().get( "Jason Car Agents" ) ).add( m_agent );
        }
        catch ( Exception l_exception )
        {
        }
    }


    @Override
    public void release()
    {
        super.release();
        if ( m_agent != null )
            m_agent.release();
    }
}
