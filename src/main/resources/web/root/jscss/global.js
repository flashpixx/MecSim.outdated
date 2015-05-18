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


/**
 * prototype overload - add startwidth to string
 * @param prefix
 * @return boolean existance
**/
String.prototype.startsWith = function( p_prefix )
{
    return this.indexOf( p_prefix ) === 0;
}


/**
 * prototype overload - add endwidth to string
 * @param suffix
 * @return boolean existance
**/
String.prototype.endsWith = function( p_suffix )
{
    return this.match( p_suffix+"$" ) == p_suffix;
};


/**
 * clear null bytes from the string
 * @return cleared null bytes
**/
String.prototype.clearnull = function()
{
    return this.replace(/\0/g, "");
};


/**
 * parse the string to a JSON object
**/
String.prototype.toJSON = function()
{
    return $.parseJSON(this.clearnull());
}