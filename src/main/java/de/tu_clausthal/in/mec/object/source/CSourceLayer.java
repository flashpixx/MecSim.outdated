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
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.source.generator.CDefaultCarGenerator;
import de.tu_clausthal.in.mec.object.source.generator.CJasonCarGenerator;
import de.tu_clausthal.in.mec.object.source.generator.CNormCarGenerator;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;


/**
 * layer with all sources
 */
public class CSourceLayer extends IMultiLayer<ISource>
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Member Variable to save a Source which was selected (null if no Source is selected)
     */
    private static ISource m_selectedSource = null;


    /**
     * Define the Calculation Index (When this Layer should be executes)
     * @return
     */
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
     * Method to create a Source
     * @param p_geoPosition
     */
    public void createSource(GeoPosition p_geoPosition)
    {
        CLogger.out("Source created");
        this.add(new CSource(p_geoPosition));
    }

    /**
     * Method to remove a Source
     * @param p_source
     */
    public void removeSource(ISource p_source)
    {
        CLogger.out("Source removed");
        if(isSelectedSource(p_source)){
            m_selectedSource=null;
        }

        p_source.release();
        this.remove(p_source);
    }

    /**
     * Method to set a specifig Generator in a Source (Or remove it)
     * @param p_source
     * @param p_selectedGenerator
     * @param p_aslname
     */
    public void getOrSetGenerator(ISource p_source, String p_selectedGenerator, String p_aslname)
    {

        if(p_source.getGenerator() == null){
            CLogger.out("Generator was set");

            if(p_selectedGenerator.equals("Default"))
                p_source.setGenerator(new CDefaultCarGenerator(p_source.getPosition()));
            if(p_selectedGenerator.equals("Jason Agent"))
                p_source.setGenerator(new CJasonCarGenerator(p_source.getPosition(), p_aslname));
            if(p_selectedGenerator.equals("Norm"))
                p_source.setGenerator(new CNormCarGenerator(p_source.getPosition()));

        }else{
            CLogger.out("Generator was removed");
        }

    }

    /**
     * Method to create a Destination
     * @param p_geoPosition
     */
    public void createDestination(GeoPosition p_geoPosition)
    {
        if(this.m_selectedSource != null){
            CLogger.out("Destination removed");
        }
    }

    /**
     * Method to remove a Destination
     */
    public void removeDestination()
    {
        CLogger.out("Destination removed");
    }

    /**
     * Method to select a Source
     * @param p_source
     */
    public void setSelectedSource(ISource p_source)
    {
        CLogger.out("Source selected");
        if(m_selectedSource!=null){
            m_selectedSource.setColor(m_selectedSource.getGenerator()==null ? Color.BLACK : m_selectedSource.getGenerator().getColor());
        }
        if(p_source != null){
            p_source.setColor(Color.WHITE);
        }
        m_selectedSource=p_source;
    }

    /**
     * Getter for the selected Source
     */
    public ISource getSelectedSource()
    {
        return m_selectedSource;
    }

    /**
     * Check if the Source is selected
     * @param p_source
     * @return
     */
    public boolean isSelectedSource(ISource p_source)
    {
        if(p_source != null)
            return p_source.equals(m_selectedSource);

        return false;
    }

}
