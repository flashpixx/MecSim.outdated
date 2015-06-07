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

// @todo http://tobyho.com/2010/11/22/javascript-constructors-and/
// @todo http://stackoverflow.com/questions/4012998/what-it-the-significance-of-the-javascript-constructor-property
// @todo http://javascriptissexy.com/javascript-prototype-in-plain-detailed-language/
// @todo http://raganwald.com/2014/07/09/javascript-constructor-problem.html
// @todo http://www.bennadel.com/blog/1566-using-super-constructors-is-critical-in-prototypal-inheritance-in-javascript.htm

/**
 * ctor of a UI pane
 *
 * @param pc_id internal name of the id
**/
function Pane( pc_id )
{
    if (!classof(pc_id, "string"))
        throw "ID undefinied";

    this.mc_id = pc_id.replace(/[^a-z0-9]+|\s+/gmi, "").toLowerCase();
}


/**
 * returns the internal ID
 *
 * @return id
**/
Pane.prototype.getID = function()
{
    return ["mecsim", this.mc_id].join("_");
}

/**
 * returns a subID of the current pane
 *
 * @param pc_subid name of the sub structure
 * @return subID
**/
Pane.prototype.generateSubID = function( pc_id )
{
    if (!classof(pc_id, "string"))
        throw "ID undefinied";

    return ["mecsim", this.mc_id, pc_id.replace(/[^a-z0-9]+|\s+/gmi, "").toLowerCase()].join("_");
}


/**
 * returns the HTML content for the body
 *
 * @return HTML string or null
**/
Pane.prototype.getGlobalContent = function()
{
    return null;
}

/**
 * returns the CSS content for the body
 *
 * @return CSS content
**/
Pane.prototype.getGlobalCSS = function()
{
    return null;
}




/*
Pane.prototype.beforeShowMainContent = function()
{
}

Pane.prototype.beforeHideMainContent = function()
{
}

Pane.prototype.getMainContent = function()
{
}

Pane.prototype.getMainCSS     = function()
{
}



Pane.prototype.beforeOpenMenu = function()
{
}

Pane.prototype.getMenuName = function()
{
}

Pane.prototype.getMenuHeader = function()
{
}

Pane.prototype.getMenuContent = function()
{
}

Pane.prototype.getMenuCSS     = function()
{
}
*/