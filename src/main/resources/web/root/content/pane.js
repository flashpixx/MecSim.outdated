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
 * @param px name of the panel or array with subpanels
 * @param pa array with subpanels
**/
function Pane( pc_id, px, pa )
{
    if (!classof(pc_id, "string"))
        throw "ID undefinied";

    // name ID, children, parent definition
    this.mc_name     = classof(px, "string") ? px : null;
    this.mc_id       = pc_id.replace(/[^a-z0-9_]+|\s+/gmi, "").toLowerCase();
    this.ma_children = [];
    this.mo_parent   = null;

    // caches of ID
    this.mo_idcache  = {};

    // read subpanels if exists
    if ((px) || (pa))
    {
        var self = this;
        var la   = Array.isArray(px) ? px : pa;
        if (la)
            la.forEach( function( po_item ) { self.addChild(po_item); } );
    }

    // adds the element to the static context
    Pane.elements[this.getID()] = this;
}


/** global static object for define all elements **/
Pane.elements    = {};


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

    // add child to the static context
    Pane.elements[po.getID()] = po;
}


/**
 * returns all elements
 *
 * @return object elements
**/
Pane.prototype.getElements = function()
{
    return Pane.elements;
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
    this.clearCachedID();
}


/**
 * clears the ID cache
**/
Pane.prototype.clearCachedID = function()
{
    this.mo_idcache = {};
}


/**
 * hash function for IDs with cache
 * @param pc_search search string
 * @param pc_value value which is set if the hash is generated
 * @return cached value
**/
Pane.prototype.getCachedID = function( pc_search, pc_value )
{
    // check cached value
    var lc_hash = pc_search.hash();
    if (!this.mo_idcache[lc_hash])
        this.mo_idcache[lc_hash] = pc_value;

    return this.mo_idcache[lc_hash];
}


/**
 * returns the internal ID
 *
 * @param pc_prefix optional prefix for the result
 * @return ID
**/
Pane.prototype.getID = function( pc_prefix )
{
    return (pc_prefix || "") + this.getCachedID(  this.mc_id,  this.mo_parent ? ["mecsim", this.mo_parent.getID().replace("mecsim_", ""), this.mc_id].join("_") : ["mecsim", this.mc_id].join("_")  );
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

    return (pc_prefix || "") + this.getCachedID(  pc_id,  [this.getID(), pc_id.replace(/[^a-z0-9_]+|\s+/gmi, "").toLowerCase()].join("_")  );
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
 * changes the name
**/
Pane.prototype.setName = function(pc)
{
    this.mc_name = pc;
    jQuery( this.getID("#") ).text(this.mc_name);
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


/**
 * method to set up the item hidden within the menu
 *
 * @return hidden boolean flag
**/
Pane.prototype.isHidden = function()
{
    return false;
}
