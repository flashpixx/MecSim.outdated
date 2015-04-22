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
 * global singleton prototype of MecSim UI calls
 * @return instance
 */
function UI()
{

    /** instantiation **/
    if ( arguments.callee._singletonInstance )
        return arguments.callee._singletonInstance;
    arguments.callee._singletonInstance = this;

    /** private UI components **/
    var m_ui = {
        accordion   : $("#mecsim_global_accordion").accordion({ active: false, collapsible: true }),
        menu        : $("#mecsim_global_menu"),
        content     : $("#mecsim_global_content")
    };


    /**
     * returns the UI components
     * @returns JSON object with UI references
     */
    this.get = function()
    {
        return m_ui;
    };


    /**
     * need for unrefactored code
     * @todo remove
     **/
    this.getContent = function()
    {
        return m_ui.content;
    }


}
