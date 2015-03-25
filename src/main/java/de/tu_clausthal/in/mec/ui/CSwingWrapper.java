/**
 * @cond
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


import de.tu_clausthal.in.mec.common.CCommon;
import javafx.embed.swing.SwingNode;

import javax.swing.*;


/**
 * JavaFX node wrapper for swing windows
 */
public class CSwingWrapper<T extends JComponent> extends SwingNode
{
    /**
     * swing component *
     */
    private final T m_component;


    /**
     * ctor
     *
     * @param p_component component
     */
    public CSwingWrapper( final T p_component )
    {
        if ( p_component == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "isnull" ) );

        m_component = p_component;
        this.setContent( p_component );
    }

    /**
     * returns the component
     *
     * @return component
     */
    public final T getComponent()
    {
        return m_component;
    }

}
