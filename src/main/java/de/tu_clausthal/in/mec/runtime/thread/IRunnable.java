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

package de.tu_clausthal.in.mec.runtime.thread;

import de.tu_clausthal.in.mec.common.CCommon;

import java.util.concurrent.Callable;


/**
 * runnable class to define objects which is callable and runable for thread-pool
 */
public abstract class IRunnable<T> implements Runnable, Callable<Object>
{

    /**
     * object reference
     */
    protected final T m_object;


    /**
     * ctor for setting the object
     *
     * @param p_object performing object
     */
    public IRunnable( final T p_object )
    {
        if ( p_object == null ) throw new IllegalArgumentException( CCommon.getResourceString( this, "notnull" ) );

        m_object = p_object;
    }

    /**
     * run method to perform the action on runnable and callable interface
     */
    protected void perform()
    {

    }


    @Override
    public final Object call() throws Exception
    {
        this.perform();
        return null;
    }

    @Override
    public final void run()
    {
        this.perform();
    }

}
