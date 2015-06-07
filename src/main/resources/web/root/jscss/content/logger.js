"use strict";

function Logger( pc_id )
{
    Pane.call(this, pc_id);
}

Logger.prototype = Object.create(Pane.prototype);



Logger.prototype.getGlobalContent = function()
{
    return "<div id=\"blub\">hallo</div>";
}
