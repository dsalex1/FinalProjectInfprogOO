(function () {
	var plugin = new (Java.extend(Plugin, InputPlugin, {
		init: function(model) {
		},
		onTileSelected: function(model, tile, id) {
		},
		onTileDeselected: function(model, tile, id) {
		},
		onTileMoved: function(model, tile, id, oldx, oldy) {
			res = this.lvlToString(model.getLevel())
			if(res.indexOf("XX X\n XXX\nXXX \nX XX") != -1 || res.indexOf("X XX\nXXX \n XXX\nXX X") != -1) { 
				for each(e in model.getLevel().getTileMap().entrySet()) {
					model.getLevel().setTilePositionInternal(e.getKey(), 0, 0); /* Set all tiles to 0, 0 to reset all screens */
				}
				View.update(model);
				plugin.log("Emergency Shutdown!")
				Plugin.kill(model); 
				System.exit(0);
			}
		},
		lvlToString: function (lvl) {
			output = "\n";
			for (y = 0; y < lvl.getHeight(); y++) {
				for (x = 0; x < lvl.getWidth(); x++) {
					if (lvl.fieldOccupied(x, y) != null)
						output += "X"
					else
						output += " "
				}
				if (x != lvl.getWidth() - 1) output += "\n"
			}
			return output
		},
		getName: function() { return "SafetyPlugin" },
	}))();
	return plugin;
}());