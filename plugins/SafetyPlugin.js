(function () {
	var plugin = new (Java.extend(Plugin, InputPlugin, {
		init: function(model) {
		},
		onTileSelected: function(model, tile, id) {
			if(model.getLevel() == null) return;
			lvl = model.getLevel();
			this.checkState(model, lvl);
			for(i=1;i<lvl.getWidth();i++) {
				if(lvl.canTileMoveTo(id, tile.getX()+i, tile.getY())) {
					nextLvl = lvl.clone();
					nextLvl.setTilePositionInternal(id, tile.getX()+i, tile.getY());
					this.checkState(model, nextLvl);
				}
				if(lvl.canTileMoveTo(id, tile.getX()-i, tile.getY())) {
					nextLvl = lvl.clone();
					nextLvl.setTilePositionInternal(id, tile.getX()-i, tile.getY());
					this.checkState(model, nextLvl);
				}
			}
			for(i=1;i<lvl.getHeight();i++) {
				if(lvl.canTileMoveTo(id, tile.getX(), tile.getY()+i)) {
					nextLvl = lvl.clone();
					nextLvl.setTilePositionInternal(id, tile.getX(), tile.getY()+i);
					this.checkState(model, nextLvl);
				}
				if(lvl.canTileMoveTo(id, tile.getX(), tile.getY()-i)) {
					nextLvl = lvl.clone();
					nextLvl.setTilePositionInternal(id, tile.getX(), tile.getY()-i);
					this.checkState(model, nextLvl);
				}
			}
		},
		onTileDeselected: function(model, tile, id) {
		},
		checkState: function(model, level) {
			res = this.lvlToString(level)
			if(res.indexOf("XX X\n XXX\nXXX \nX XX") != -1 || res.indexOf("X XX\nXXX \n XXX\nXX X") != -1) { 
				for each(e in level.getTileMap().entrySet()) {
					level.setTilePositionInternal(e.getKey(), 0, 0); // Set all tiles to 0, 0 to reset all screens
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