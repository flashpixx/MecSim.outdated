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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.object.IMultiLayer;
import org.jxmapviewer.viewer.GeoPosition;


/**
 * layer with all sources
 */
public class CSourceFactoryLayer extends IMultiLayer<ISourceFactory>
{

    /**
     * returns a list of source names
     *
     * @return source names
     */
    public String[] getSourceNamesList()
    {
        return new String[]{"Default", "Norm", "Jason Agent", "Profile"};
    }


    /**
     * creates a source
     *
     * @param p_name     name of the source type
     * @param p_position geo position of the source
     * @param p_varargs  optional arguments of the sources
     * @return source object
     */
    public ISourceFactory getSource( String p_name, GeoPosition p_position, Object... p_varargs )
    {
        if ( p_name.equals( "Default" ) )
            return new CDefaultSourceFactory( p_position );
        if ( p_name.equals( "Norm" ) )
            return new CNormSourceFactory( p_position );
        if ( ( p_name.equals( "Jason Agent" ) ) && ( p_varargs != null ) && ( p_varargs.length > 0 ) )
            return new CJasonAgentSourceFactory( p_position, (String) p_varargs[0] );
        //if (p_name.equals( "Profile" ))
        //    return new CProfileSourceFactory( p_position );

        throw new IllegalArgumentException( "source name not found" );
    }

    /**
     * release overwrite, because all sources will be removed of the reset is called
     */
    @Override
    public void release()
    {

    }

    @Override
    public int getCalculationIndex()
    {
        return 2;
    }

}
