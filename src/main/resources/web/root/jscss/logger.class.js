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
 * global logger singleton
 * @return instance
 **/
function Logger()
{

    /** instantiation **/
    if ( arguments.callee._singletonInstance )
        return arguments.callee._singletonInstance;
    arguments.callee._singletonInstance = this;


    /**
     * write warn message
     * @param px_value writing object
     */
    this.warn = function( px_value )
    {
        $.post/get("clogger/warn", px_value );
    };


    /**
     * write info  message
     * @param px_value writing object
     */
    this.info = function( px_value )
    {
        $.post("clogger/info", px_value );
    };


    /**
     * write out message
     * @param px_value writing object
     */
    this.out = function( px_value )
    {
        $.post("clogger/out", px_value );
    };


    /**
     * write debug message
     * @param px_value writing object
     */
    this.debug = function( px_value )
    {
        $.post("clogger/debug", px_value );
    };


    /**
     * write error message
     * @param px_value writing object
     */
    this.error = function( px_value )
    {
        $.post("clogger/error", px_value );
    };

};