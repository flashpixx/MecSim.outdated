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
 * ctor to create the source editor for MAS files
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function MASEditor( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    // object with arrays of file objects in the structure { name: -filename-, config: -key of the configuration object- }
    this.mo_files = {};

    // set the configuration for all file access
    this.mo_configuration = {

        "Jason" : {
            reader : "/cagentenvironment/jason/read",
            writer : "/cagentenvironment/jason/write",
            list   : "/cagentenvironment/jason/list"
        }

    }

    // read files on initialisation
    this.readFiles();
}

/** inheritance call **/
MASEditor.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
MASEditor.prototype.getGlobalContent = function()
{
    return Pane.prototype.getGlobalContent.call(this);;
}


/**
 * @Overwrite
**/
MASEditor.prototype.getContent = function()
{
    return '<div id="' + this.generateSubID("files") + '"></div>' + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
MASEditor.prototype.afterDOMAdded = function()
{
/*
    jQuery( "#"+pc_item ).selectmenu({
                select: function(po_event, po_ui) { self.updateConfiguration( po_event.target.id, po_ui.item.value ); }
            })
*/
}


/**
 * read all files from the REST-API
 * @see https://api.jquery.com/category/deferred-object/
 * @see https://api.jquery.com/jquery.when/
 * @note handle asynchron calls and create at the done call one array with all data
**/
MASEditor.prototype.readFiles = function()
{
    var self = this;

    // create Ajax calls
    var la_tasks = [];
    jQuery.each(this.mo_configuration, function( pc_configkey, po_config ) {

        var lo_task = new jQuery.Deferred();
        lo_task.done( function( po_data ) {
            po_data.config = pc_configkey;
        });

        MecSim.ajax({
            url     :  po_config.list,
            success : lo_task.resolve
        });

        la_tasks.push( lo_task.promise() );
    });


    // collect results if all calls are finished
    jQuery.when.apply(jQuery, la_tasks).done(

        function()
        {
            self.mo_files = {};

            // collapse data
            Array.prototype.slice.call(arguments).forEach( function(px) {

                // on multiple Ajax call px is an array
                if ((Array.isArray(px)) && (px.length == 3) && (px[1] == "success"))
                {
                    if (!Array.isArray(self.mo_files[ px[0].config ]))
                        self.mo_files[ px[0].config ] = [];

                    Array.prototype.push.apply( self.mo_files[ px[0].config ], px[0].agents );
                    return;
                }


                // on single Ajax call px is an object
                if ((px instanceof Object) && (px.agents) && (px.config))
                {
                    if (!Array.isArray(self.mo_files[px.config]))
                        self.mo_files[px.config] = [];

                    Array.prototype.push.apply( self.mo_files[px.config], px.agents );
                    return;
                }
            });

            // update select box
            jQuery( self.generateSubID("files", "#") ).empty();
            jQuery(
                Layout.selectgroup({ id: self.generateSubID("mas"),  options: self.mo_files })
            ).appendTo( self.generateSubID("files", "#") );
        }
    );

}


/**
 * returns a deep-copy of the filelist
 *
 * @return array with filelist objects
**/
MASEditor.prototype.getFiles = function()
{
    return jQuery.extend( {}, this.mo_files );
}