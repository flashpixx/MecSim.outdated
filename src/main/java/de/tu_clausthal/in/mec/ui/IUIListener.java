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

package de.tu_clausthal.in.mec.ui;

import org.jxmapviewer.JXMapViewer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * class to handle UI events with encapsulate the default lister structures
 */
public abstract class IUIListener implements MouseListener
{

    /**
     * ctor to register component on the viewer
     */
    public IUIListener()
    {
        COSMViewer.getInstance().addMouseListener( this );
    }

    /**
     * click method which is called by a click on the object
     *
     * @param e      mouse event
     * @param viewer viewer
     */
    public void onClick( MouseEvent e, JXMapViewer viewer )
    {
    }


    /**
     * release of the event handler
     */
    public void release()
    {
        COSMViewer.getInstance().removeMouseListener( this );
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
        if ( SwingUtilities.isLeftMouseButton( e ) )
            this.onClick( e, COSMViewer.getInstance() );
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
    }

    @Override
    public void mouseEntered( MouseEvent e )
    {
    }

    @Override
    public void mouseExited( MouseEvent e )
    {
    }

}
