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
		'<td id="' + this.generateSubID("add") + '"></td>' +
		'<td id="' + this.generateSubID("edit") + '"></td>' +
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
		'    float: left;' +
		'    width: 50%;' +
		'}' +

		this.generateSubID("waypointtable", "#") +
		'{' +
		'    width: 100%;' +
		'}' +

        this.generateSubID("thead", "#") +
        '{' +
        '    margin: 0;' +
        '    color: white;' +
        '    background: #008C4F;' +
        '    border: 1px solid #CACACA;' +
        '    position: relative;' +
        '    text-align: center;' +
        '    margin-bottom: 10px;' +
        '}' +

		this.generateSubID("waypointname", "#") +
		'{' +
		'    width: 15%;' +
		'}'+

		this.generateSubID("latitude", "#") + "," + this.generateSubID("longitude", "#") +
		'{' +
		'    width: 35%;' +
		'}'+

		this.generateSubID("id", "#") + "," + this.generateSubID("add", "#") + "," + this.generateSubID("edit", "#") +
		'{' +
		'    width: 5%;' +
		'}'
}


/**
 * @Overwrite
**/
WaypointConnection.prototype.afterDOMAdded = function()
{
	var self = this;

	self.refresh();

	MecSim.language({

		url    : "/cwaypointenvironment/labelwaypointconnection",
		target : this,

		finish : function() {
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

	MecSim.ajax({
		url : "/cwaypointenvironment/listpathwaypoints",
		success : function(po_data){
			jQuery.each(po_data, function(pc_waypoint, po_info){
				var l_data = jQuery("<tr></tr>");
				jQuery("<td></td>").text(po_info.id).appendTo(l_data);
				jQuery("<td></td>").text(po_info.name).appendTo(l_data);
				jQuery("<td></td>").text(po_info.latitude).appendTo(l_data);
				jQuery("<td></td>").text(po_info.longitude).appendTo(l_data);
				jQuery("<td></td>").append("<button>Add</button>").appendTo(l_data);

				if(po_info.type === "path"){
					jQuery("<td></td>").append("<button>Edit</button>").appendTo(l_data);
				}else{
					jQuery("<td></td>").appendTo(l_data);
				}

				l_data.appendTo(jQuery(self.generateSubID("tbody", "#")));
			});
		}
	})
}