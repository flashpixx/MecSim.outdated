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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import org.jxmapviewer.viewer.GeoPosition;


/**
 * layer with all sources
 */
public class CSourceLayer extends IMultiLayer<ISource>
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public final int getCalculationIndex()
    {
        return 2;
    }

    /**
     * release overwrite, because all sources will be removed of the reset is called
     */
    @Override
    public void release()
    {

    }

    /**
     * returns a list of source names
     *
     * @return source names
     */
    public final String[] getSourceNamesList()
    {
        return new String[]{"Default", "Norm", "Jason Agent", "Profile"};
    }

    /**
     * Method to select a Source
     * @param l_source
     */
    public void setSelectedSource(ISource l_source)
    {
        CLogger.out("Source selected");
    }

    /**
     * Method to remove a Source
     * @param l_source
     */
    public void removeSource(ISource l_source)
    {
        CLogger.out("Source removed");
        l_source.release();
        this.remove(l_source);
    }

    /**
     * Method to create a Source
     * @param l_geoPosition
     */
    public void createSource(GeoPosition l_geoPosition)
    {
        CLogger.out("Source created");
        this.add(new CDefaultSourceFactory(l_geoPosition));
    }

    /**
     * Method to set a specifig Generator in a Source (Or remove it)
     * @param l_source
     * @param l_selectedGenerator
     * @param l_aslname
     */
    public void getOrSetGenerator(ISource l_source, String l_selectedGenerator, String l_aslname)
    {
        CLogger.out("Get or Set Generator");
    }

    /**
     * Method to create a Destination
     * @param l_geoPosition
     */
    public void createDestination(GeoPosition l_geoPosition)
    {
        CLogger.out("Destination removed");
    }

    /**
     * Method to remove a Destination
     */
    public void removeDestination()
    {
        CLogger.out("Destination removed");
    }

}
