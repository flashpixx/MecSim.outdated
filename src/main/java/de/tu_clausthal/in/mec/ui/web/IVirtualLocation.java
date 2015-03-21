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

package de.tu_clausthal.in.mec.ui.web;


import fi.iki.elonen.NanoHTTPD;


/**
 * interface of locations
 */
public interface IVirtualLocation
{

    /**
     * checks an URI if it matches the named-based location
     *
     * @param p_uri input URI
     * @return machting boolean
     */
    public boolean match( final String p_uri );

    /**
     * gets the source of the file
     *
     * @param p_session HTTP session
     * @return any return data
     */
    public <T> T get( final NanoHTTPD.IHTTPSession p_session ) throws Throwable;


    /**
     * returns an markdown renderer or nul
     */
    public CMarkdownRenderer getMarkDownRenderer();

}
