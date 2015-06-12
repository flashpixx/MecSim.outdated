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

//@todo fix HTML5 data attributes


/**
 * ctor of a UI pane
 *
 * @param pc_id internal name of the ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Pane( pc_id, pc_name, pa_panel )
{
    if (!classof(pc_id, "string"))
        throw "ID undefinied";

    this.mc_name     = pc_name || null;
    this.mc_id       = pc_id.replace(/[^a-z0-9_]+|\s+/gmi, "").toLowerCase();
    this.ma_children = [];
    this.mo_parent   = null;

    if (Array.isArray(pa_panel))
    {
        var self = this;
        pa_panel.forEach( function( po_item ) { self.addChild(po_item); } );
    }
}


/**
 * adds a new children to the pane
 *
 * @param po pane object
**/
Pane.prototype.addChild = function( po )
{
    if (!(po instanceof Pane))
        throw "object is not a pane object";

    // create deep-copy of the object to test for duplicates
    var lo_test = Object.create(po);
    lo_test.setParent(this);

    this.ma_children.forEach( function(po_item) {
        if (po_item.getID() == lo_test.getID())
            throw "object ID [" + po.getID() + "] exists within the pane element";
    });

    // add new element and modify parent
    this.ma_children.push(po);
    po.setParent(this);

}


/**
 * sets the parent object
 *
 * @param po pane object
**/
Pane.prototype.setParent = function( po )
{
    if (!(po instanceof Pane))
        throw "object is not a pane object";

    this.mo_parent = po;
}

/**
 * returns the internal ID
 *
 * @param pc_prefix optional prefix for the result
 * @return ID
**/
Pane.prototype.getID = function( pc_prefix )
{
    if (this.mo_parent)
        return ["mecsim", this.mo_parent.getID().replace("mecsim_", ""), this.mc_id].join("_");

    return (pc_prefix || "") + ["mecsim", this.mc_id].join("_");
}

/**
 * returns a subID of the current pane
 *
 * @param pc_subid name of the sub structure
 * @param pc_prefix optional prefix for the result
 * @return subID
**/
Pane.prototype.generateSubID = function( pc_id, pc_prefix )
{
    if (!classof(pc_id, "string"))
        throw "ID undefinied";

    return (pc_prefix || "") + [this.getID(), pc_id.replace(/[^a-z0-9_]+|\s+/gmi, "").toLowerCase()].join("_");
}


/**
 * returns the HTML content for the body
 *
 * @return HTML string or null
**/
Pane.prototype.getGlobalContent = function()
{
    var lc_result = "";
    this.ma_children.forEach( function(po_item){ lc_result += po_item.getGlobalContent(); } );
    return lc_result.trim();
}

/**
 * returns the CSS content for the body
 *
 * @return CSS content
**/
Pane.prototype.getGlobalCSS = function()
{
    var lc_result = "";
    this.ma_children.forEach( function(po_item){ lc_result += po_item.getGlobalCSS(); } );
    return lc_result;
}

/**
 * returns the name of the pane,
 * button or headline name
 *
 * @return name of the pane item
**/
Pane.prototype.getName = function()
{
    return this.mc_name;
}

/**
 * returns the content of the pane
 *
 * @return content
**/
Pane.prototype.getContent = function()
{
    var lc_result = "";
    this.ma_children.forEach( function(po_item) { lc_result += po_item.getContent(); } );
    return lc_result.trim();
}


/**
 * container of the content
 *
 * @param pc_prefix string prefix
 * @param pc_suffix string suffix
 * @return content with container
**/
Pane.prototype.getContentWithContainer = function( pc_prefix, pc_suffix )
{
    var lc_content = this.getContent();
    return (pc_prefix ? pc_prefix : '<div class="' + this.generateSubID("content") + '">') + ( lc_content ? lc_content : "") + (pc_suffix ? pc_suffix : '</div>')
}

/**
 * returns the CSS of the content
 *
 * @return CSS content
**/
Pane.prototype.getCSS = function()
{
    var lc_result = "";
    this.ma_children.forEach( function(po_item) { lc_result += po_item.getCSS(); } );
    return lc_result.trim();
}

/**
 * is called after all content is added to the DOM
**/
Pane.prototype.afterDOMAdded = function()
{
    this.ma_children.forEach( function(po_item) { po_item.afterDOMAdded(); } );
}
