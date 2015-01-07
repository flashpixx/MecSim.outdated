/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
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

package de.tu_clausthal.in.winf.object.norm.range;

import de.tu_clausthal.in.winf.object.norm.institution.IInstitution;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.painter.Painter;

import java.io.Serializable;


/**
 * class to represent the workspace of an institution
 */
public interface IRange<T> extends Serializable, Painter<COSMViewer>
{


    /**
     * check if an object is within the range
     *
     * @param p_object object
     * @return boolean for existence
     */
    public boolean check(T p_object);


    /**
     * returns the institution of the range
     *
     * @return institution
     */
    public IInstitution<T> getInstitution();


    /**
     * release call *
     */
    public void release();

}
