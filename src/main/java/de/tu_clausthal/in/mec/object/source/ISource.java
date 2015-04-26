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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.source.generator.IGenerator;
import de.tu_clausthal.in.mec.object.source.sourcetarget.CComplexTarget;
import de.tu_clausthal.in.mec.runtime.IReturnSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.io.Serializable;


/**
 * factory interface of car source - defines a source
 * @todo change to a abstract class for storing color information
 */
public interface ISource extends IReturnSteppable<ICar>, Painter<COSMViewer>, Serializable
{

    /**
     * returns the position of the source
     *
     * @return geoposition of the source
     */
    public GeoPosition getPosition();

    /**
     * returns the color of the source
     *
     * @return color of the source
     */
    public Color getColor();

    /**
     * set a new color for this source
     *
     * @param p_color new Color
     * @deprecated
     */
    public void setColor( Color p_color );

    /**
     * return the generator of the source
     *
     * @return generator object of this source
     * @deprecated
     */
    public IGenerator getGenerator();

    /**
     * return the ComplexTarget of the source
     *
     * @return ComplexTarget of the source
     * @deprecated
     */
    public CComplexTarget getComplexTarget();

}
