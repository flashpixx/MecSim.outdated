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

package de.tu_clausthal.in.mec.simulation.thread;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.simulation.IVoidStepable;


/**
 * wrapper class to process a void-stepable item
 */
public class CVoidStepable extends IRunnable<IVoidStepable>
{

    /**
     * layer object *
     */
    protected ILayer m_layer = null;
    /**
     * iteration value *
     */
    protected int m_iteration = 0;


    /**
     * ctor
     *
     * @param p_iteration current iteration value
     * @param p_object    void-stepable object
     * @param p_layer     layer of the object or null
     */
    public CVoidStepable( int p_iteration, IVoidStepable p_object, ILayer p_layer )
    {
        super( p_object );
        m_layer = p_layer;
        m_iteration = p_iteration;
    }


    /**
     * run method to perform the action on runnable and callable interface
     */
    protected void perform()
    {
        try
        {
            if ( ( m_layer != null ) && ( m_layer instanceof IMultiLayer ) )
                ( (IMultiLayer) m_layer ).beforeStepObject( m_iteration, m_object );


            m_object.step( m_iteration, m_layer );


            if ( ( m_layer != null ) && ( m_layer instanceof IMultiLayer ) )
                ( (IMultiLayer) m_layer ).afterStepObject( m_iteration, m_object );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( "object [" + m_object.toString() + "] throws: " + l_exception.toString() );
        }
    }

}
