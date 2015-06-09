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
 * ctor to create the MAS instance
 *
 * @param pc_id ID
 * @param pc_name name of the panel
**/
function MAS( pc_id, pc_name )
{
    Pane.call(this, pc_id, pc_name );
}

/** inheritance call **/
MAS.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
MAS.prototype.getContent = function()
{
    return '<button id = "' + this.generateSubID("jasonmind") + '" >Jason Mindinspector</button >' +
           '<button id = "' + this.generateSubID("communication") + '" >Message Communication</button >';
}


/**
 * @Overwrite
**/
MAS.prototype.getGlobalCSS = function()
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
           '}';
}


/**
 * @Overwrite
**/
MAS.prototype.afterDOMAdded = function()
{

    var self = this;

    // Jason mindinspector bind
    jQuery(self.generateSubID("jasonmind", "#")).button().click( function() {

        jQuery(MecSim.ui().content("#")).empty();
        jQuery(MecSim.ui().content("#")).append( '<iframe id = "mindinspector" class = "template" src = "http://localhost:3272" seamless />' );

    });


    // communication binding
    jQuery(self.generateSubID("communication", "#")).button().click( function() {

        jQuery(MecSim.ui().content("#")).empty();
        var lo_agentcommunication = Visualization.HierarchicalEdgeBundling( MecSim.ui().content("#"), { id   : this.generateSubID("communicationdiagram") });

        MecSim.websocket( "/cmessagesystem/flow", {

            "onmessage" : function( po_event ) {

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
                lo_agentcommunication(lo_tree);

            }

        });

    });

}
