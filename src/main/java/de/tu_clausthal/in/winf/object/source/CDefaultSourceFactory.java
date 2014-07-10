/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
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

package de.tu_clausthal.in.winf.object.source;

import de.tu_clausthal.in.winf.object.car.CCarLayer;
import de.tu_clausthal.in.winf.object.car.CDefaultCar;
import de.tu_clausthal.in.winf.object.car.ICar;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import de.tu_clausthal.in.winf.simulation.IReturnStepableTarget;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;


/**
 * default source for generating cars with a visualization on the map *
 */
public class CDefaultSourceFactory implements ISourceFactory {

    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * position of the source within the map *
     */
    protected GeoPosition m_position = null;
    /**
     * integer values how many cars are generated in a step *
     */
    protected int m_NumberCarsInStep = 1;
    /**
     * random interface *
     */
    protected Random m_random = new Random();
    /**
     * image of the waypoint *
     */
    protected BufferedImage m_image = null;
    /**
     * waypoint color *
     */
    protected Color m_color = Color.CYAN;


    /**
     * ctor that sets the geo position of the source
     *
     * @param p_position geo position object
     */
    public CDefaultSourceFactory(GeoPosition p_position) {
        m_position = p_position;
        this.setImage();
    }


    /**
     * ctor which sets the geo position of the source and the number of cars on a creation step
     *
     * @param p_position geoposition
     * @param p_number   number of cars
     */
    public CDefaultSourceFactory(GeoPosition p_position, int p_number) {
        m_position = p_position;
        m_NumberCarsInStep = p_number;
        if (p_number < 1)
            throw new IllegalArgumentException("number must be greater than zero");

        this.setImage();
    }


    /**
     * creates the image *
     */
    private void setImage() {
        try {
            BufferedImage l_image = ImageIO.read(DefaultWaypointRenderer.class.getResource("/images/standard_waypoint.png"));

            // modify blue value to the color of the waypoint
            m_image = new BufferedImage(l_image.getColorModel(), l_image.copyData(null), l_image.isAlphaPremultiplied(), null);
            for (int i = 0; i < l_image.getHeight(); i++)
                for (int j = 0; j < l_image.getWidth(); j++) {
                    Color l_color = new Color(l_image.getRGB(j, i));
                    if (l_color.getBlue() > 0)
                        m_image.setRGB(j, i, m_color.getRGB());
                }

        } catch (Exception l_exception) {
            m_Logger.warn("could not read standard_waypoint.png", l_exception);
        }
    }


    @Override
    public void setNumberOfCars(int p_number) {
        if (p_number < 1)
            throw new IllegalArgumentException("number must be greater than zero");

        m_NumberCarsInStep = p_number;
    }


    @Override
    public Collection<ICar> step(int p_currentstep, ILayer p_layer) {
        System.out.println("xxx");

        Collection<ICar> l_sources = new HashSet();

        // use random number on care creation, to avoid traffic jam on the source
        for (int i = 0; i < m_NumberCarsInStep; i++)
            if (m_random.nextDouble() > 0.15)
                l_sources.add(new CDefaultCar(m_position));

        return l_sources;
    }

    @Override
    public Collection<IReturnStepableTarget<ICar>> getTargets() {
        Collection<IReturnStepableTarget<ICar>> l_collection = new HashSet();
        l_collection.add((CCarLayer) CSimulation.getInstance().getWorld().getMap().get("Car"));
        return l_collection;
    }


    @Override
    public void paint(Graphics2D g, COSMViewer object, int width, int height) {
        if (m_image == null)
            return;

        Point2D l_point = object.getTileFactory().geoToPixel(m_position, object.getZoom());
        g.drawImage(m_image, (int) l_point.getX() - m_image.getWidth() / 2, (int) l_point.getY() - m_image.getHeight(), null);
    }
}
