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

package de.tu_clausthal.in.mec.simulation.thread;

import java.util.concurrent.Callable;


/**
 * Created by pkraus on 08.01.15.
 */
public abstract class IRunnable<T> implements Runnable, Callable<Object>
{

    /**
     * object reference *
     */
    protected T m_object = null;


    /**
     * ctor for setting the object
     *
     * @param p_object performing object
     */
    public IRunnable( T p_object )
    {
        if ( p_object == null )
            throw new IllegalArgumentException( "object argument need not to be null" );

        m_object = p_object;
    }

    /**
     * run method to perform the action on
     * runnable and callable interface
     */
    protected void perform()
    {

    }


    @Override
    public Object call() throws Exception
    {
        this.perform();
        return null;
    }

    @Override
    public void run()
    {
        this.perform();
    }

}
