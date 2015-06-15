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
 * ctor to create the a wizard
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
 * @param po_options configuration options
**/
function Wizard( pc_id, pc_name, pa_panel, po_options )
{
    Widget.call(this, pc_id, pc_name, pa_panel, po_options );

    // sets the configuration of the wizard
    var lo_options = po_options || {};
    this.mo_configuration = {

        headertag   : lo_options.headertag   || "h3",
        bodytag     : lo_options.bodytag     || "section",

        transition  : lo_options.transition  || "slideLeft",
        orientation : lo_options.orientation || "vertical"

    };
}

/** inheritance call **/
Wizard.prototype = Object.create(Widget.prototype);


/**
 * @Overwrite
 *
 * @param pc_content optional content
**/
Wizard.prototype.getContent = function( pc_content )
{
    return Widget.prototype.getContent.call( this, '<div id="' + this.generateSubID("content") + '">' + (pc_content ? pc_content : "") + '</div>' );
}

/**
 * @Overwrite
**/
Wizard.prototype.afterDOMAdded = function()
{
    Widget.prototype.afterDOMAdded.call(this);

    jQuery( this.generateSubID("content", "#") ).steps({

        headerTag        : this.mo_configuration.headertag,
        bodyTag          : this.mo_configuration.bodytag,
        transitionEffect : this.mo_configuration.transition,
        stepsOrientation : this.mo_configuration.orientation,

        onInit           : this.init,
        onStepChanging   : this.validatestep,
        onFinished       : this.finish,

        /*
        labels: {
            finish: SourcePanel.settings.labels.finish,
            next: SourcePanel.settings.labels.next,
            previous: SourcePanel.settings.labels.previous,
        }
        */

    });
}


/**
 * wizard initializing to read data
**/
Wizard.prototype.init = function()
{
}

/**
 * is called on every step
 *
 * @param po_event event
 * @param pn_current current step
 * @param pn_next next step
**/
Wizard.prototype.validatestep = function( po_event , pn_current, pn_next )
{
}

/**
 * is called after finish click
**/
Wizard.prototype.finish = function()
{
}

