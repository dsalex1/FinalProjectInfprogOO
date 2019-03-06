(function () {
	var plugin = new (Java.extend(Plugin, InputPlugin, {
		init: function(model) {
		},
		onTileSelected: function(model, tile, id) {
			this.save(model);
		},
		onTileDeselected: function(model, tile, id) {
			//this.load(model);
		},
		onTileMoved: function(model, tile, id, oldx, oldy) {
			//if(oldx != tile.getX() || oldy != tile.getY()) {
			//	this.save(model);
			//}
		},
		save: function(model) {
			saveGame = {};
			saveGame["tiles"] = {};
			
			for each(e in model.getLevel().getTileMap().entrySet()) {
				tile = {};
				tile["x"] = e.getValue().getX();
				tile["y"] = e.getValue().getY();
				tile["type"] = model.getTileTypeName(e.getValue().getType());
				tile["data"] = {};
				for each(d in e.getValue().getData().entrySet()) {
					tile["data"][d.getKey()] = d.getValue();
				}
				saveGame["tiles"][e.getKey()] = tile;
			}
			
			saveGame["type"] = model.getLevelTypeName(model.getLevel().getType());
			saveGame["width"] = model.getLevel().getWidth();
			saveGame["height"] = model.getLevel().getHeight();
			saveGame["selected"] = model.getLevel().getSelected();

			this.lastSave = JSON.stringify(saveGame);
			plugin.log(this.lastSave);
		},
		load: function(model) {
			saveGame = JSON.parse(this.lastSave);
			levelType = model.getLevelType(saveGame["type"]);
			level = new Level(levelType);
			level.setWidth(saveGame["width"]);
			level.setHeight(saveGame["height"]);
			
			for each(e in Object.keys(saveGame["tiles"])) {
				tileType = model.getTileType(saveGame["tiles"][e]["type"]);
				tile = new Tile(saveGame["tiles"][e]["x"],saveGame["tiles"][e]["y"],tileType);
				for each(d in Object.keys(saveGame["tiles"][e]["data"])) {
					tile.getData().put(d.getKey(), d.getValue())
				}
				level.addTile(e, tile);
			}
			level.setSelected(null);
			model.setLevel(level);
			//InputPlugin.tileMoved(model, level.getTile(level.getSelected()), level.getSelected(), level.getTile(level.getSelected()).getX(), level.getTile(level.getSelected()).getY());
			View.update(model);
		},
		getName: function() { return "SaveGamePlugin" },
	}))();
	
	return plugin;
}());