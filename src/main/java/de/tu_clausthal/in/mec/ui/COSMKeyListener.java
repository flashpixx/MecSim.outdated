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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;


/**
 * key listener for JxViewer
 */
public class COSMKeyListener implements KeyListener
{

    /**
     * Variable which define the Offset for navigating threw the Map
     */
    private static final int c_offset = 10;
    /**
     * Variable which indicates if the STRG Key is pressed
     */
    private boolean m_strgPressed = false;
    /**
     * Variable which indicates if the Shift Key is pressed
     */
    private boolean m_shiftPressed = false;
    /**
     * Variable which indicates if the Alt Key is pressed
     */
    private boolean m_altPressed = false;

    /**
     * Number of Keys which are pressed
     */


    @Override
    public final void keyTyped( final KeyEvent p_event )
    {
    }

    @Override
    public final void keyPressed( final KeyEvent p_event )
    {
        //Set Shift/Alt/STRG Status if the specific Key is pressed
        if ( p_event.getKeyCode() == KeyEvent.VK_SHIFT ) m_shiftPressed = true;
        if ( p_event.getKeyCode() == KeyEvent.VK_ALT ) m_altPressed = true;
        if ( p_event.getKeyCode() == KeyEvent.VK_CONTROL ) m_strgPressed = true;

        //Check if Arrows are Pressed and Repaint the Map
        int l_delta_x = 0;
        int l_delta_y = 0;

        switch ( p_event.getKeyCode() )
        {
            case KeyEvent.VK_LEFT:
                l_delta_x = -c_offset;
                break;
            case KeyEvent.VK_RIGHT:
                l_delta_x = c_offset;
                break;
            case KeyEvent.VK_UP:
                l_delta_y = -c_offset;
                break;
            case KeyEvent.VK_DOWN:
                l_delta_y = c_offset;
                break;
        }

        if ( l_delta_x != 0 || l_delta_y != 0 )
        {
            final COSMViewer l_viewer = (COSMViewer) p_event.getSource();
            final Rectangle l_bounds = l_viewer.getViewportBounds();
            l_viewer.setCenter( new Point2D.Double( l_bounds.getCenterX() + l_delta_x, l_bounds.getCenterY() + l_delta_y ) );
            l_viewer.repaint();
        }

    }

    @Override
    public final void keyReleased( final KeyEvent p_event )
    {
        //Update the Key Pressed Status
        if ( p_event.getKeyCode() == KeyEvent.VK_SHIFT ) m_shiftPressed = false;
        if ( p_event.getKeyCode() == KeyEvent.VK_ALT ) m_altPressed = false;
        if ( p_event.getKeyCode() == KeyEvent.VK_CONTROL ) m_strgPressed = false;

    }

    /**
     * Get Strg Pressed Status
     */
    public final boolean isStrgPressed()
    {
        return m_strgPressed;
    }

    /**
     * Get ShiftPressed Status
     */
    public final boolean isShiftPressed()
    {
        return m_shiftPressed;
    }

    /**
     * Get AltPressed Status
     */
    public final boolean isAltPressed()
    {
        return m_altPressed;
    }

    /**
     * Get Key Pressed Status
     */
    public final boolean isAnyKeyPressed()
    {
        return m_shiftPressed || m_strgPressed || m_altPressed;
    }

    /**
     * Get the Number of Keys which are pressed
     */
    public final int getKeyPressedCount()
    {
        int l_keyPressedCount = 0;

        if ( m_altPressed ) l_keyPressedCount++;
        if ( m_shiftPressed ) l_keyPressedCount++;
        if ( m_strgPressed ) l_keyPressedCount++;

        return l_keyPressedCount;
    }

}
