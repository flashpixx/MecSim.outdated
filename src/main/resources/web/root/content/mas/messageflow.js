/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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
 * ctor to create the MAS message flow view
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function MASMessageFlow( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    this.mo_visualization = null;
    this.mo_socket        = null;
}

/** inheritance call **/
MASMessageFlow.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
MASMessageFlow.prototype.getContent = function()
{
    return '<button id = "' + this.getID() + '" ></button>' + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
MASMessageFlow.prototype.getGlobalCSS = function()
{
    return this.generateSubID("communicationdiagram", "#") +
           '{' +
           'left: 10em;' +
           'position: relative;' +
           '}' +

           this.generateSubID("communicationdiagram", "path.")+'-arc' +
           '{' +
           'fill: #fff;' +
           '}' +

           this.generateSubID("communicationdiagram", ".")+'-node' +
           '{' +
           'font-size: 8px;' +
           '}' +


           this.generateSubID("communicationdiagram", ".")+'-link' +
           '{' +
           'fill: none;' +
           'stroke: #1f77b4;' +
           'stroke-opacity: .4;' +
           'pointer-events: none;' +
           '}' +

           this.generateSubID("communicationdiagram", ".")+'-link.source, ' + this.generateSubID("communicationdiagram", ".")+'-link.target' +
           '{' +
           'stroke-opacity: 1;' +
           'stroke-width: 2px;' +
           '}' +

           this.generateSubID("communicationdiagram")+'-node.target' +
           '{' +
           'fill: #d62728 !important;' +
           '}' +

           this.generateSubID("communicationdiagram", ".")+'-link.source' +
           '{' +
           'stroke: #d62728;' +
           '}' +

           this.generateSubID("communicationdiagram", ".")+'-node.source' +
           '{' +
           'fill: #2ca02c;' +
           '}' +

           this.generateSubID("communicationdiagram", ".")+'.-link.target' +
           '{' +
           'stroke: #2ca02c;' +
           '}' +

           Pane.prototype.getGlobalCSS.call(this);
}



/**
 * @Overwrite
**/
MASMessageFlow.prototype.afterDOMAdded = function()
{
    var self = this;
    MecSim.language({ url : "/clanguageenvironment/mascommunicate", target : this });


    jQuery(this.getID("#")).button().click( function() {

        jQuery(MecSim.ui().content("#")).empty().on( "empty", self.close.bind(self) );

        self.mo_visualization = Visualization.HierarchicalEdgeBundling( MecSim.ui().content("#"), { id : self.generateSubID("communicationdiagram") });
        self.mo_socket        = MecSim.websocket( "/cmessagesystem/flow", {

            "onmessage" : self.onmessage.bind(self),
            "onerror"   : self.close.bind(self)

        });

    });
}


/**
 * close the websocket
**/
MASMessageFlow.prototype.close = function()
{
    if (!this.mo_socket)
        return;

    this.mo_socket.close();
}


/**
 * websocket message callback
 *
 * @param po_event websocket message
**/
MASMessageFlow.prototype.onmessage = function( po_event )
{
    if (!this.mo_visualization)
        return;

    /** variable to store the tree **/
    var lo_tree = {};

    /**
     * build from an item (and its separator) the full path within the tree
     * @param pc_path string with path
     * @param pc_separator path separator
    **/
    function add2Tree( pc_path, pc_separator )
    {
        if (lo_tree[pc_path])
            return;

        // array.slice(x,y) is definied as [x,y)
        for( var i=1, la = pc_path.split(pc_separator), ln_length = la.length+1; i < ln_length; ++i )
        {
            var lc_parent = la.slice(0, i-1).join(pc_separator);
            if (!lo_tree[lc_parent])
                lo_tree[lc_parent] =  { children : new Set(), connect : new Set() };

            lo_tree[lc_parent].children.add( la.slice(0, i).join(pc_separator) );
        }

        // add child if not exists
        if (!lo_tree[pc_path])
            lo_tree[pc_path] =  { children : new Set(), connect : new Set() };
    };


    /**
     * iterator of the data for building the tree
    **/
    po_event.data.toJSON().cells.forEach( function( po_object ) {

        add2Tree( po_object.source.path, po_object.source.separator );
        add2Tree( po_object.target.path, po_object.target.separator );

        lo_tree[po_object.source.path].connect.add( po_object.target.path );

    });

    // update visualization
    this.mo_visualization(lo_tree);

}
