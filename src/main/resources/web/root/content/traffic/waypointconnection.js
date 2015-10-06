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
 * ctor to create the waypoint connection widget
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function WaypointConnection( pc_id, pc_name, pa_panel, po_options )
{
    Widget.call(this, pc_id, pc_name, pa_panel, po_options );
    this.mc_currentMakrovWaypoint = null;
    this.mo_graphEditor = null;
    this.mo_waypointList = {};
    this.mo_currentNodes = [];
    this.mo_currentEdges = [];

    //todo make labels multilanguage (maybe set in afterDomAdded-method)
    this.mo_labels = {
        edit    : "Edit",
        finish  : "Finish",
        add     : "Add",
        remove  : "Remove"
    };
}

/** inheritance call **/
WaypointConnection.prototype = Object.create(Widget.prototype);


/**
 * @Overwrite
**/
WaypointConnection.prototype.getContent = function()
{

    // add waypoint connection content
    return Widget.prototype.getContent.call( this,

        '<div id="' + this.generateSubID("waypointlist") + '">' +
        '<table id="' + this.generateSubID("waypointtable") + '">' +
        '<thead id="' + this.generateSubID("thead") + '">' +
        '<tr>' +
        '<td id="' + this.generateSubID("identifier") + '"></td>' +
        '<td id="' + this.generateSubID("waypointname") + '"></td>' +
        '<td id="' + this.generateSubID("latitude") + '"></td>' +
        '<td id="' + this.generateSubID("longitude") + '"></td>' +
        '<td id="' + this.generateSubID("buttonCol") + '"></td>' +
        '</tr>' +
        '</thead>' +
        '<tbody id="' + this.generateSubID("tbody") + '">' +
        '</tbody>' +
        '</table>' +
        '</div>' +
        '<div id="' + this.generateSubID("waypointeditor") + '">' +
        '</div>'
    );
}


/**
 * @Overwrite
**/
WaypointConnection.prototype.getGlobalCSS = function()
{
    return  Widget.prototype.getGlobalCSS.call(this) +

        this.generateSubID("waypointlist", "#") +
        '{' +
            'float: left;' +
            'width: 50%;' +
        '}' +

        this.generateSubID("waypointtable", "#") +
        '{' +
            'width: 100%;' +
        '}' +

        this.generateSubID("thead", "#") +
        '{' +
            'margin: 0;' +
            'color: white;' +
            'background: #008C4F;' +
            'border: 1px solid #CACACA;' +
            'position: relative;' +
            'text-align: center;' +
            'margin-bottom: 10px;' +
        '}' +

        this.generateSubID("waypointname", "#") +
        '{' +
            'width: 15%;' +
        '}'+

        this.generateSubID("latitude", "#") + "," + this.generateSubID("longitude", "#") +
        '{' +
            'width: 30%;' +
        '}'+

        this.generateSubID("id", "#") +
        '{' +
            'width: 5%;' +
        '}' +

        this.generateSubID("buttonCol", "#") +
        '{' +
            'width: 20%;' +
        '}' +

        this.generateSubID("dynamicButton", ".") +
        '{' +
            'width: 100%;' +
        '}' +

        this.generateSubID("waypointeditor", "#") +
        '{' +
            'float: right;' +
            'margin-right: 10px;' +
            'border: 1px solid #008C4F;' +
            'width: 45%;' +
            'height : 90%' +
        '}'
}


/**
 * @Overwrite
**/
WaypointConnection.prototype.afterDOMAdded = function()
{
    var self = this;

    self.ml_cancel = self.generateSubID("waypointeditor", "#");

    MecSim.language({

        url    : "/cwaypointenvironment/labelwaypointconnection",
        target : this,

        finish : function() {
            //initalize waypoint list
            self.refresh();

            //initalize waypoint editor
            self.mo_graphEditor = new GraphEditor(self.generateSubID("waypointeditor", "#"),  {
                nodes : [],
                edges : [],
                lastNodeID : 0,
                mouseMode : false,
                onRemoveNode : self.onRemoveNode.bind(self),
                onAddEdge : self.onAddEdge.bind(self),
                onRemoveEdge : self.onRemoveEdge.bind(self)
            });

            Widget.prototype.afterDOMAdded.call(self);
        }
    });
}


/**
 * method to refresh table data
**/
WaypointConnection.prototype.refresh = function()
{
    var self = this;

    jQuery(self.generateSubID("tbody", "#")).empty();
    self.mo_waypointList = {};

    MecSim.ajax({
        url : "/cwaypointenvironment/listpathwaypoints",
        method : "GET",
        success : function(po_data){
            jQuery.each(po_data, function(pc_waypoint, po_info){

                //create client list
                self.mo_waypointList[pc_waypoint] = {id : po_info.id, name : po_info.name, type : po_info.type, lat : po_info.latitude, lon : po_info.longitudem};

                //create table entry
                var l_row = jQuery("<tr></tr>");
                jQuery("<td></td>").text(po_info.id).appendTo(l_row);
                jQuery("<td></td>").text(po_info.name).appendTo(l_row);
                jQuery("<td></td>").text(po_info.latitude).appendTo(l_row);
                jQuery("<td></td>").text(po_info.longitude).appendTo(l_row);

                //initial for the dynamic button
                var l_button = jQuery("<button></button>")
                    .attr("class", self.generateSubID("dynamicButton"))
                    .attr("value", pc_waypoint)
                    .click(self.dynamicListener.bind(self));

                if(po_info.type === "path"){
                    l_button.addClass(self.generateSubID("pathButton"))
                        .addClass(self.generateSubID("editClass"))
                        .text(self.mo_labels.edit)
                        .click(self.editListener.bind(self));
                 }else{
                    l_button.addClass(self.generateSubID("noPathButton"))
                        .addClass(self.generateSubID("addClass"))
                        .text(self.mo_labels.add)
                        .click(self.addListener.bind(self))
                        .prop('disabled', true);
                }

                jQuery("<td></td>").append(l_button).appendTo(l_row);
                l_row.appendTo(jQuery(self.generateSubID("tbody", "#")));
            });
        }
    });
}


/**
 * listen to the buttons in the waypoint list
 * will dynamicly modify buttons and rebind listeners
**/
WaypointConnection.prototype.dynamicListener = function(p_event)
{
    var self = this;
    var clickedElement = jQuery(event.target);
    var dynamicButtonCollection = jQuery(self.generateSubID("dynamicButton", "."));
    var pathCollection = jQuery(self.generateSubID("pathButton", "."));
    var noPathCollection = jQuery(self.generateSubID("noPathButton", "."));

    //if edit was clicked
    if(clickedElement.hasClass(self.generateSubID("editClass"))){

        //all buttons to add/remove depending if they are already in the makrov chain
        pathCollection.removeClass(self.generateSubID("editClass"))
            .removeClass(self.generateSubID("addClass"))
            .removeClass(self.generateSubID("removeClass"))
            .addClass(self.generateSubID("addClass"))
            .text(self.mo_labels.add)
            .unbind("click")
            .click(self.dynamicListener.bind(self))
            .click(self.addListener.bind(self));

        //clicked edit button to finish
        clickedElement.removeClass(self.generateSubID("addClass")) //todo not needed
            .addClass(self.generateSubID("finishClass"))
            .text(self.mo_labels.finish)
            .unbind("click")
            .click(self.dynamicListener.bind(self))
            .click(self.finishListener.bind(self));

        //enable noPath buttons
        jQuery(self.generateSubID("noPathButton", ".")).prop('disabled', false);
        return;
    }

    //if finish was clicked
    if(clickedElement.hasClass(self.generateSubID("finishClass"))){

        //all path buttons to edit
        pathCollection.removeClass(self.generateSubID("finishClass"))
            .removeClass(self.generateSubID("addClass"))
            .removeClass(self.generateSubID("removeClass"))
            .addClass(self.generateSubID("editClass"))
            .text(self.mo_labels.edit)
            .unbind("click")
            .click(self.dynamicListener.bind(self))
            .click(self.editListener.bind(self));

        // todo not needed
        //all noPath buttons to add
        noPathCollection.removeClass(self.generateSubID("removeClass"))
            .addClass(self.generateSubID("addClass"))
            .text(self.mo_labels.add)
            .unbind("click")
            .click(self.dynamicListener.bind(self))
            .click(self.addListener.bind(self));

        //disable all noPath button
        jQuery(self.generateSubID("noPathButton", ".")).prop('disabled', true);
        return;
    }

    //if add was clicked
    if(clickedElement.hasClass(self.generateSubID("addClass"))){

        //switch add and remove
        clickedElement.removeClass(self.generateSubID("addClass"))
            .addClass(self.generateSubID("removeClass"))
            .text(self.mo_labels.remove)
            .unbind("click")
            .click(self.dynamicListener.bind(self))
            .click(self.removeListener.bind(self));

        return;
    }

    //if remove was clicked
    if(clickedElement.hasClass(self.generateSubID("removeClass"))){

        //switch add and remove
        clickedElement.removeClass(self.generateSubID("removeClass"))
            .addClass(self.generateSubID("addClass"))
            .text(self.mo_labels.add)
            .unbind("click")
            .click(self.dynamicListener.bind(self))
            .click(self.addListener.bind(self));

        return;
    }
}


/**
 * fires when a edit button was clicked
 * this method will load the makrov chain from java into the d3-graphEditor
**/
WaypointConnection.prototype.editListener = function(p_event)
{
    this.mc_currentMakrovWaypoint = p_event.target.value;
    this.reload(p_event.target.value);
}


/**
 * fires when a finish button was clicked
 * this method will clear the d3-GraphEditor
**/
WaypointConnection.prototype.finishListener = function(p_event)
{
    this.mc_currentMakrovWaypoint = null;
    this.reload(null);
}


/**
 * fires when an add button was clicked
 * this method will add a waypoint to the currently loaded makrov chain
**/
WaypointConnection.prototype.addListener = function(p_event)
{
    var self = this;

    //add node to makrov chain and reload after success
    MecSim.ajax({
        url : "cwaypointenvironment/addnode",
        data : {"makrovwaypoint" : self.mc_currentMakrovWaypoint, "waypoint" : p_event.target.value},
        success : function(){
            self.reload(self.mc_currentMakrovWaypoint);
        }
    });
}


/**
 * fires when a remove button was clicked
 * this method will remove a waypoint from the currently loaded makrov chain
**/
WaypointConnection.prototype.removeListener = function(p_event)
{
    var self = this;

    var node;
    self.mo_currentNodes.forEach(function(element, index){
        if(p_event.target.value === element.waypointname)
            node = element;
    })

    self.mo_graphEditor.removeNode(node);

    MecSim.ajax({
        url : "cwaypointenvironment/removenode",
        data : {"makrovwaypoint" : self.mc_currentMakrovWaypoint, "waypoint" : p_event.target.value}
    });
}


/**
 * method to reload the graph editor
**/
WaypointConnection.prototype.reload = function(p_makrovwaypoint)
{
    var self = this;

    MecSim.ajax({
        url : "cwaypointenvironment/getmakrovchain",
        data : {"makrovwaypoint" : p_makrovwaypoint},
        success : function(po_data){

            var l_lastID = 0;
            var l_newNodes = [];
            var l_newEdges = [];

            //build nodes array
            jQuery.each(po_data, function(p_key, p_value){
                var l_waypointID = self.mo_waypointList[p_key].id
                l_newNodes.push({id : l_waypointID, reflexive : false, waypointname : p_key});
                if(l_waypointID > l_lastID) l_lastID = ++l_waypointID;
            });

            //build edge array
            jQuery.each(po_data, function(p_key, p_value){
                jQuery.each(p_value, function(p_key2, p_value2){

                    //check if edge already exist
                    var l_edge;
                    jQuery.each(l_newEdges, function(l_index){
                        if(l_newEdges[l_index].source.waypointname === p_key && l_newEdges[l_index].target.waypointname === p_key2){
                            l_newEdges[l_index].right = true;
                            l_edge = l_newEdges[l_index];
                            return;
                        }

                        if(l_newEdges[l_index].target.waypointname === p_key && l_newEdges[l_index].source.waypointname === p_key2){
                            l_newEdges[l_index].left = true;
                            l_edge = l_newEdges[l_index];
                            return;
                        }
                    });

                    if(!l_edge){
                        //find node object
                        var l_source, l_target;
                        jQuery.each(l_newNodes, function(l_index){
                            if( l_newNodes[l_index].waypointname === p_key ) l_source = l_newNodes[l_index]
                            if( l_newNodes[l_index].waypointname === p_key2 ) l_target = l_newNodes[l_index]
                        });

                        l_newEdges.push({source : l_source, target : l_target, left : false, right : true});
                    }
                });
            });

            //actual graph reload
            self.mo_graphEditor.reload({nodes : l_newNodes, edges : l_newEdges, lastNodeID : --l_lastID})
            self.mo_currentNodes = l_newNodes;
            self.mo_currentEdges = l_newEdges;
        }
    });
}


/**
 * fires when a node was removed via d3-graphEditor
 * this method will remove the waypoint out of the java makrov chain
**/
WaypointConnection.prototype.onRemoveNode = function(p_node)
{
    var self = this;

    MecSim.ajax({
        url : "cwaypointenvironment/removenode",
        data : {"makrovwaypoint" : self.mc_currentMakrovWaypoint, "waypoint" : p_node.waypointname}
    });
}


/**
 * fires when an edge was created via d3-graphEditor
 * this method will add this edge to the java makrov chain
**/
WaypointConnection.prototype.onAddEdge = function(p_edge)
{
    var self = this;
    if(p_edge.left)
        MecSim.ajax({
            url : "cwaypointenvironment/addedge",
            data : {"makrovwaypoint" : self.mc_currentMakrovWaypoint, "source" : p_edge.target.waypointname, "target" : p_edge.source.waypointname}
        });

    if(p_edge.right)
        MecSim.ajax({
            url : "cwaypointenvironment/addedge",
            data : {"makrovwaypoint" : self.mc_currentMakrovWaypoint, "source" : p_edge.source.waypointname, "target" : p_edge.target.waypointname}
        });
}


/**
 * fires when an edge was removed via d3-graphEditor
 * this method will remove this edge from the java makrov chain
**/
WaypointConnection.prototype.onRemoveEdge = function(p_edge)
{
    var self = this;
    if(p_edge.left)
        MecSim.ajax({
            url : "cwaypointenvironment/removeedge",
            data : {"makrovwaypoint" : self.mc_currentMakrovWaypoint, "source" : p_edge.target.waypointname, "target" : p_edge.source.waypointname}
        });

    if(p_edge.right)
        MecSim.ajax({
            url : "cwaypointenvironment/removeedge",
            data : {"makrovwaypoint" : self.mc_currentMakrovWaypoint, "source" : p_edge.source.waypointname, "target" : p_edge.target.waypointname}
        });
}