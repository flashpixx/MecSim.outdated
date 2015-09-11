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
 * ctor to create the inconsistency item
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function MASInconsistency( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    // URL-base
    this.mc_url = "/cinconsistencyenvironment/" + pc_id;
}

/** inheritance call **/
MASInconsistency.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
MASInconsistency.prototype.getGlobalContent = function()
{
    return Layout.dialog({

        id        : this.generateSubID("dialog"),
        title     : "",
        content   : Layout.select({

            id    : this.generateSubID("metric"),
            label : ""

        }) +

        Layout.area({

            id    : this.generateSubID("path"),
            label : ""

        })

    }) +
    Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overwrite
**/
MASInconsistency.prototype.getContent = function()
{
    return '<p><button id = "' + this.getID() + '" ></button></p>' + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
MASInconsistency.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    MecSim.language({

        url :  this.mc_url + "/label",
        target : this,
        finish : function()
        {

            jQuery( self.generateSubID("dialog", "#") ).dialog({
                modal    : true,
                autoOpen : false,
                buttons  : {

                    OK   : function() {

                        MecSim.ajax({

                            url  : self.mc_url + "/setmetric",
                            data : {
                                id    : jQuery(self.generateSubID("metric", "#")).val(),
                                path  : jQuery(self.generateSubID("path", "#")).val()
                            },
                            success : function()
                            {
                                jQuery( self.generateSubID("dialog", "#") ).dialog("close");
                            }

                        }).fail( function( po_data ) { console.log(po_data); } );

                    }

                }
            });

        }

    });

    // bind button action
    jQuery(this.getID("#")).button().click( function() {

        MecSim.ajax({

            url     : self.mc_url + "/getmetric",
            success : function( po_data )
            {

                jQuery( self.generateSubID("metric", "#") ).find("option").remove().end();
                jQuery( self.generateSubID("path", "#") ).empty();

                jQuery.each( po_data, function( pc_key, po_value ) {

                    jQuery( Layout.option({
                        label : pc_key,
                        id    : po_value.id
                    },
                    po_value.active ? po_value.id : null) ).appendTo( self.generateSubID("metric", "#")  );

                    if (po_value.active)
                        jQuery( po_value.selector.join("\n") ).appendTo( self.generateSubID("path", "#") );

                    // @todo selectmenu not working
                    //jQuery( self.generateSubID("metric", "#") ).selectmenu({ width: 150 });
                    jQuery( self.generateSubID("dialog", "#") ).dialog( "open" );

                });


            }

        });

    });
}