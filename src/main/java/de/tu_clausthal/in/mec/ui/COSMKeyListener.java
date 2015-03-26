/**
 * @cond ###################################################################################### # GPL License # # # #
 * This file is part of the TUC Wirtschaftsinformatik - MecSim                        # # Copyright (c) 2014-15,
 * Philipp
 * Kraus (philipp.kraus@tu-clausthal.de)               # # This program is free software: you can redistribute it
 * and/or
 * modify               # # it under the terms of the GNU General Public License as                            # #
 * published by the Free Software Foundation, either version 3 of the # # License, or (at your option) any later
 * version.                                    # # # # This program is distributed in the hope that it will be useful,
 * # # but WITHOUT ANY WARRANTY; without even the implied warranty of # # MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the                      # # GNU General Public License for more details.
 * # # # # You should have received a copy of the GNU General Public License # # along with
 * this program. If not, see http://www.gnu.org/licenses/                  # ######################################################################################
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
    private static final int m_OFFSET = 10;
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
    public void keyTyped( KeyEvent p_event )
    {

    }

    @Override
    public void keyPressed( KeyEvent p_event )
    {
        //Set Shift/Alt/STRG Status if the specific Key is pressed
        if ( p_event.getKeyCode() == KeyEvent.VK_SHIFT ) m_shiftPressed = true;
        if ( p_event.getKeyCode() == KeyEvent.VK_ALT ) m_altPressed = true;
        if ( p_event.getKeyCode() == KeyEvent.VK_CONTROL ) m_strgPressed = true;

        //Check if Arrows are Pressed and Repaint the Map
        int delta_x = 0;
        int delta_y = 0;

        switch ( p_event.getKeyCode() )
        {
            case KeyEvent.VK_LEFT:
                delta_x = -m_OFFSET;
                break;
            case KeyEvent.VK_RIGHT:
                delta_x = m_OFFSET;
                break;
            case KeyEvent.VK_UP:
                delta_y = -m_OFFSET;
                break;
            case KeyEvent.VK_DOWN:
                delta_y = m_OFFSET;
                break;
        }

        if ( delta_x != 0 || delta_y != 0 )
        {
            final COSMViewer l_viewer = (COSMViewer) p_event.getSource();
            Rectangle bounds = l_viewer.getViewportBounds();
            double x = bounds.getCenterX() + delta_x;
            double y = bounds.getCenterY() + delta_y;
            l_viewer.setCenter( new Point2D.Double( x, y ) );
            l_viewer.repaint();
        }

    }

    @Override
    public void keyReleased( KeyEvent p_event )
    {
        //Update the Key Pressed Status
        if ( p_event.getKeyCode() == KeyEvent.VK_SHIFT ) m_shiftPressed = false;
        if ( p_event.getKeyCode() == KeyEvent.VK_ALT ) m_altPressed = false;
        if ( p_event.getKeyCode() == KeyEvent.VK_CONTROL ) m_strgPressed = false;

    }

    /**
     * Get Strg Pressed Status
     */
    public boolean isStrgPressed()
    {
        return m_strgPressed;
    }

    /**
     * Get ShiftPressed Status
     */
    public boolean isShiftPressed()
    {
        return m_shiftPressed;
    }

    /**
     * Get AltPressed Status
     */
    public boolean isAltPressed()
    {
        return m_altPressed;
    }

    /**
     * Get Key Pressed Status
     */
    public boolean isAnyKeyPressed()
    {
        return m_shiftPressed || m_strgPressed || m_altPressed;
    }

    /**
     * Get the Number of Keys which are pressed
     */
    public int getKeyPressedCount()
    {
        int l_keyPressedCount = 0;

        if ( m_altPressed ) l_keyPressedCount++;
        if ( m_shiftPressed ) l_keyPressedCount++;
        if ( m_strgPressed ) l_keyPressedCount++;


        return l_keyPressedCount;
    }

}
