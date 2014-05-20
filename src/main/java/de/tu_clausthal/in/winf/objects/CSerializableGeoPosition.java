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

package de.tu_clausthal.in.winf.objects;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * serializable geoposition object, because mapviewer.GeoPosition object does not
 * implement the serializable interface
 */
public class CSerializableGeoPosition extends CSerializableAdapter<GeoPosition> {

    /**
     * ctor to create a serializable geoposition object
     *
     * @param p_object geoposition object
     */
    public CSerializableGeoPosition(GeoPosition p_object) {
        m_object = p_object;
    }


    /**
     * output method
     *
     * @param p_output stream
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream p_output) throws IOException {
        p_output.writeDouble(m_object.getLatitude());
        p_output.writeDouble(m_object.getLongitude());
    }


    /**
     * input method
     *
     * @param p_input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream p_input) throws IOException, ClassNotFoundException {
        m_object = new GeoPosition(p_input.readDouble(), p_input.readDouble());
    }


}
