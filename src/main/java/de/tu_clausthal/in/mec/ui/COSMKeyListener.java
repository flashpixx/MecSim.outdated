/**
 * Created by christoph on 16.02.15.
 */

package de.tu_clausthal.in.mec.ui;

import org.jxmapviewer.JXMapViewer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

/**
 * Key Listener Class
 */
public class COSMKeyListener implements  KeyListener{

    /**
     * Reference to JYMapViewer
     */
    private JXMapViewer m_viewer;
    /**
     * Offset to Navigate via Arrow-Keys
     */
    private static final int m_OFFSET = 10;
    /**
     * Boolean Variable to indicate if shift is pressed
     */
    private boolean m_shiftPressed;


    /**
     * CTOR Key Listener
     * @param p_viewer
     */
    public COSMKeyListener(JXMapViewer p_viewer)
    {
        this.m_viewer = p_viewer;
    }

    /**
     * Method which will trigger if a key was typed
     * @param p_event
     */
    @Override
    public void keyTyped(KeyEvent p_event)
    {

    }

    /**
     * Method which will trigger if a key was pressed
     * @param p_event
     */
    @Override
    public void keyPressed(KeyEvent p_event)
    {
        //Set Shift Status if Shift is pressed
        if(p_event.getKeyCode() == KeyEvent.VK_SHIFT)
            m_shiftPressed = true;

        //Check if Arrows are Pressed and Repaint the Map
        int delta_x = 0;
        int delta_y = 0;

        switch (p_event.getKeyCode())
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

        if (delta_x != 0 || delta_y != 0)
        {
            Rectangle bounds = m_viewer.getViewportBounds();
            double x = bounds.getCenterX() + delta_x;
            double y = bounds.getCenterY() + delta_y;
            m_viewer.setCenter(new Point2D.Double(x, y));
            m_viewer.repaint();
        }
    }

    /**
     * Method which will trigger if a key was released
     * @param p_event
     */
    @Override
    public void keyReleased(KeyEvent p_event)
    {
        if(p_event.getKeyCode() == KeyEvent.VK_SHIFT)
            m_shiftPressed = false;
    }

    /**
     * Return the Status of the Shift Key
     * @return boolean
     */
    public boolean isShiftPressed()
    {
        return m_shiftPressed;
    }

}
