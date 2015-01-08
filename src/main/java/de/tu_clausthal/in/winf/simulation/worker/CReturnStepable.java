/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.simulation.worker;

import de.tu_clausthal.in.winf.CLogger;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.object.world.IMultiLayer;
import de.tu_clausthal.in.winf.simulation.IReturnStepable;
import de.tu_clausthal.in.winf.simulation.IReturnStepableTarget;

import java.util.Collection;

/**
 * class to process a return-stepable item
 */
public class CReturnStepable implements Runnable
{
    private ILayer m_layer = null;
    private IReturnStepable m_object = null;
    private int m_iteration = 0;

    public CReturnStepable( int p_iteration, IReturnStepable p_item, ILayer p_layer )
    {
        if ( p_item == null )
            throw new IllegalArgumentException( "return-stepable argument need not to be null" );

        m_object = p_item;
        m_layer = p_layer;
        m_iteration = p_iteration;
    }


    @Override
    public void run()
    {
        try
        {
            if ( ( m_layer != null ) && ( m_layer instanceof IMultiLayer ) )
                ( (IMultiLayer) m_layer ).beforeStepObject( m_iteration, m_object );


            Collection l_data = m_object.step( m_iteration, m_layer );
            Collection<IReturnStepableTarget> l_targets = m_object.getTargets();
            if ( ( l_data != null ) && ( l_targets != null ) )
                for ( IReturnStepableTarget l_target : l_targets )
                    l_target.set( l_data );


            if ( ( m_layer != null ) && ( m_layer instanceof IMultiLayer ) )
                ( (IMultiLayer) m_layer ).afterStepObject( m_iteration, m_object );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( "object [" + m_object.toString() + "] throws: " + l_exception.toString() );
        }
    }
}
