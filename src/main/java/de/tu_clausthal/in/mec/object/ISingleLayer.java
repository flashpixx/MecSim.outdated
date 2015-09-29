/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

package de.tu_clausthal.in.mec.object;

import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import de.tu_clausthal.in.mec.ui.CUI;
import de.tu_clausthal.in.mec.ui.IViewableLayer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;


/**
 * single layer to create a single information structure
 */
@SuppressWarnings( "serial" )
public abstract class ISingleLayer implements Painter<COSMViewer>, IViewableLayer, IVoidSteppable, ILayer
{

    /**
     * flag for activity
     */
    protected boolean m_active = true;
    /**
     * flag for visibility
     */
    protected boolean m_visible = true;

    @Override
    public int getCalculationIndex()
    {
        return 0;
    }

    @Override
    public final boolean isActive()
    {
        return m_active;
    }

    @Override
    public final void setActive( final boolean p_active )
    {
        m_active = p_active;
    }

    @Override
    public void onSimulationStart()
    {
    }

    @Override
    public void onSimulationStop()
    {
    }

    @Override
    public void onSimulationReset()
    {
        this.release();
        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();

    }

    @Override
    public final boolean isVisible()
    {
        return m_visible;
    }

    @Override
    public final void setVisible( final boolean p_visible )
    {
        m_visible = p_visible;
    }

    @Override
    public abstract void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height );

    @Override
    public abstract void step( final int p_currentstep, final ILayer p_layer );

    @Override
    public void release()
    {
    }
}
