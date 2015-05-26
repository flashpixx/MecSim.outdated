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

 //modul to create ui widgets
 var Widget = ( function (px_module) {

    //ctor
    px_module = function(element, options){

        if(element === undefined || element === null){
            console.log("DOM Element is missing");
            return;
        }

        //settings
        this._div               = element;
        this._name              = options.name             || "Default Widgetname";
        this._animate           = options.animate          || true;
        this._animateEffect     = options.animateEffect    || "drop";
        this._animationTime     = options.animateTime      || 400;
        this._minWidth          = options.minWidth         || 750;
        this._minHeight         = options.minHeight        || 550;
        this._collapseWidth     = options.collapseWidth    || 400;
        this._collapseHeight    = options.collapseHeight   || 20;
        this._minOffset         = options.minOffSet        || 50;

        this._closedStatus = false;
        this._minimizedStatus = false;

        //meta data (header and buttons)
        this._div.attr("class", "mecsim_widget_defaultWidget");
        this._div.prepend(
            $("<h3></h3>")
                .attr("class", "mecsim_widget_widgetHeader")
                .append("<span>"+ this._name +"</span>")
                .append($("<span class = 'mecsim_widget_widgetButton'></span>")
                    .append($("<button></button>").button({icons:{primary: "ui-icon-newwin"},text: false}).on("click", this.collapse.bind(this)))
                    .append($("<button></button>").button({icons:{primary: "ui-icon-closethick"},text: false}).on("click", this.close.bind(this)))
                )
        );

        //create widget
        this._div.draggable({
            drag: function(event, ui) {
                ui.position.top = Math.max( -500, ui.position.top );
                ui.position.left = Math.max( -700, ui.position.left );
            }})
            .resizable({
                animate: this._animate,
                minWidth: this._minWidth,
                minHeight: this._minHeight
            });
    };

    //static method to create widget
    px_module.createWidget = function(element, options){
        return new Widget(element, options);
    };

    //method to collapse widget
    px_module.prototype.collapse = function(){
        if(this._minimizedStatus){
            this._div.children().not(".mecsim_widget_widgetHeader").show();
            this._div.animate({width: this._minWidth+"px", height: this._minHeight+"px"}, this._animationTime);
            this._div.resizable('enable');
        }else{
            this._div.children().not(".mecsim_widget_widgetHeader").hide();
            this._div.animate({width: this._collapseWidth+"px", height: this._collapseHeight+"px"}, this._animationTime);
            this._div.resizable('disable');
        }
        this._minimizedStatus = !this._minimizedStatus;
    };

    //method to close widget
    px_module.prototype.close = function(){
        if(this._closedStatus){
            this._div.show(this._animateEffect, this._animationTime);
        }else{
            this._div.hide(this._animateEffect, this._animationTime);
        }
        this._closedStatus = !this._closedStatus;
    };

    return px_module;

}(Widget || {}));
