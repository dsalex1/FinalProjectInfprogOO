(function () {
	var plugin = new (Java.extend(InputPlugin, {
		init: function(model) {
			// don't trigger any events here as not all plugins are loaded and/or not initialized
			plugin.log("Initialized Plugin");
		},
		onTileSelected: function(model, tile, id) {
			plugin.log("Selected tile '"+id+"'");
		},
		onTileDeselected: function(model, tile, id) {
			plugin.log("Deselected tile '"+id+"'");
		},
		onTileMoved: function(model, tile, id, oldx, oldy) {
			plugin.log("Moved tile '"+id+"' from ("+oldx+","+oldy+") to ("+tile.getX()+","+tile.getY()+")");
		},
		getName: function() { return "Debug Message Plugin" },
	}))();
	return plugin;
}());