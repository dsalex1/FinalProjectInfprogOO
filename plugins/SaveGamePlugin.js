(function () {
	File = Java.type("java.io.File");
	BufferedReader = Java.type("java.io.BufferedReader")
	BufferedWriter = Java.type("java.io.BufferedWriter")
	FileReader = Java.type("java.io.FileReader")
	FileWriter = Java.type("java.io.FileWriter")
	
	var plugin = new (Java.extend(Plugin, KeyboardInputPlugin, {
		init: function(model) {
			this.file = new File(System.getProperty("user.dir")+File.separator+"save.dat");
		},
		onPluginsLoaded: function(model) {
			if(this.file.exists()) {
				this.lastSave = loadFile();
				this.load(model);
			}else {
				this.save(model);
				this.saveFile(this.lastSave);
			}
		},
		onKeyPressed: function(model, key, shift) {
			if(key == "l".charCodeAt(0)) {
				this.load(model);
			}
			if(key == "s".charCodeAt(0)) {
				this.save(model);
				this.saveFile(this.lastSave);
			}
		},
		loadFile: function() {
			 reader = new BufferedReader(new FileReader(this.file));
			 line = reader.readLine();
			 reader.close();
			 return line;
		},
		saveFile: function(content) {
			 writer = new BufferedWriter(new FileWriter(this.file));
			 writer.write(content);
			 writer.close();
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
			//plugin.log(this.lastSave);
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
			//View.update(model);
		},
		getName: function() { return "SaveGamePlugin" },
	}))();
	
	return plugin;
}());