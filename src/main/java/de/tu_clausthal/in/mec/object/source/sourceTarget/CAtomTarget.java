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
//test
package de.tu_clausthal.in.mec.object.source.sourceTarget;

import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Most Basic Target for Sources
 */
public class CAtomTarget implements Painter<COSMViewer> {

    /**
     * Position of the Target Objekt
     */
    private GeoPosition m_position;


    /**
     * CTOR
     * @param p_position
     */
    public CAtomTarget(GeoPosition p_position){
        m_position = p_position;
    }

    /**
     * Get the Position of the Target
     * @return
     */
    public GeoPosition getPosition(){
        return m_position;
    }

    /**
     * Set a specific Position for this Target
     * @param p_position
     */
    public void setPosition(GeoPosition p_position){
        this.m_position = p_position;
    }

    @Override
    public void paint(final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height ) {
        final int l_zoom = Math.max(15 - p_viewer.getZoom(), 3);
        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( this.getPosition(), p_viewer.getZoom() );
        p_graphic.setColor( Color.RED );
        p_graphic.fillRect( (int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom );
    }

}
