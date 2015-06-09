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

"use strict";

/**
 * prototype overload - add startwidth to string
 * @param pc_prefix prefix
 * @return boolean existance
**/
String.prototype.startsWith = String.prototype.startsWith || function( pc_prefix )
{
    return this.indexOf( pc_prefix ) === 0;
}


/**
 * prototype overload - add endwidth to string
 * @param pc_suffix
 * @return boolean existance
**/
String.prototype.endsWith = String.prototype.endsWith || function( pc_suffix )
{
    return this.match( pc_suffix+"$" ) == pc_suffix;
};


/**
 * clear null bytes from the string
 * @return cleared null bytes
**/
String.prototype.clearnull = String.prototype.clearnull || function()
{
    return this.replace(/\0/g, "");
};


/**
 * parse the string to a JSON object
**/
String.prototype.toJSON = String.prototype.toJSON || function()
{
    return $.parseJSON(this.clearnull());
}


/**
 * removes an element of an array
 * @param px_value value
**/
Array.prototype.remove = Array.prototype.remove || function( px_value )
{
    delete this[ Array.prototype.indexOf.call(this, px_value) ];
}


