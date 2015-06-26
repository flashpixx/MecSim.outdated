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
    return jQuery.parseJSON(this.clearnull());
}


/**
 * Fowler–Noll–Vo hash function
 * @see https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function#FNV-1a_hash
 * @see http://stackoverflow.com/questions/7616461/generate-a-hash-from-string-in-javascript-jquery
 *
 * @return hash value
**/
String.prototype.hash = String.prototype.hash || function()
{
    var hval = 0x811c9dc5;

    // Strips unicode bits, only the lower 8 bits of the values are used
    for (var i = 0; i < this.length; i++) {
        hval = hval ^ (this.charCodeAt(i) & 0xFF);
        hval += (hval << 1) + (hval << 4) + (hval << 7) + (hval << 8) + (hval << 24);
    }

    var val = hval >>> 0;
    return ("0000000" + (val >>> 0).toString(16)).substr(-8);
}



/**
 * removes an element of an array
 * @param px_value value
**/
Array.prototype.remove = Array.prototype.remove || function( px_value )
{
    delete this[ Array.prototype.indexOf.call(this, px_value) ];
}


/**
 * elementwise convert to build a new array
 * @param px_value modifier closure
 * @returns a new array
**/
Array.prototype.convert = Array.prototype.convert || function( px_value )
{
    var la_result = [];
    this.forEach( function( px_item ) { la_result.push( px_value(px_item) ); } );
    return la_result;
}


/**
 * global function to get the object-type of a variable
 *
 * @param px_value value type
 * @return class type
**/
function classof( px_value, pc_type )
{
    return ({}).toString.call( px_value ).match(/\s([a-z|A-Z]+)/)[1].trim().toLowerCase() === pc_type.trim().toLowerCase();

}


/**
 * @Overload
 * add an empty trigger to the empty function
**/
jQuery.fn.raw_empty = jQuery.fn.empty;
jQuery.fn.empty    = function(){ return this.raw_empty().trigger( "empty", this ) }
