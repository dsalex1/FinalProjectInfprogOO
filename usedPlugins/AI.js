(function () {
	var plugin = new (Java.extend(InputPlugin, {
		init: function (model) {
			// don't trigger any events here as not all plugins are loaded and/or not initialized

		},
		onPluginsLoaded: function (model) {
			var lvl = model.getLevel()
			var seenLvls = {};
			seenLvls[this.lvlToString(lvl)] = true;

			var gameMovesTree = [{ lvl: lvl, nextMoves: [] }]
			gameMovesTree[0].nextMoves = this.generateNextMoves(lvl, gameMovesTree, seenLvls, 100)

			//plugin.log(JSON.stringify(gameMovesTree));
			for (e in seenLvls) {
				//plugin.log(e);
			}
			plugin.log(Object.keys(seenLvls).length);


		},
		getName: function () { return "AIPlugin" },

		lvlToString: function (lvl) {
			typeMap = {};
			i = 0;
			for each(e in lvl.getTileMap().entrySet()) {
				typeMap[e.getValue().getType()] = i++;
			}
			output = "\n";
			for (y = 0; y < lvl.getHeight(); y++) {
				for (x = 0; x < lvl.getWidth(); x++) {
					if (lvl.fieldOccupied(x, y) != null)
						output += typeMap[lvl.getTile(lvl.fieldOccupied(x, y)).getType()]
					else
						output += "."
				}
				if (x != lvl.getWidth() - 1) output += "\n"
			}
			return output
		},
		generateNextMoves: function (lvl, parent, seenLvls, depth) {
			var nextMoves = []
			var searchString=
"\n33..\
\n2300\
\n2200\
\n11..\
\n14...\
\n44..\n"

			//plugin.log(searchString);
			for each(e in lvl.getTileMap().entrySet()) {
				if(lvl.canTileMoveTo(e.getKey(), e.getValue().getX(), e.getValue().getY() + 1) == true) {
					nextLvl = lvl.clone();
					nextLvl.setTilePositionInternal(e.getKey(), e.getValue().getX(), e.getValue().getY() + 1);
					nextLvlString = this.lvlToString(nextLvl);
					if (nextLvlString==searchString)plugin.log("Found in depth "+depth);

					if (!seenLvls[nextLvlString]) {
						nextMove = { lvl: nextLvl, nextMoves: [] }
						nextMoves.push(nextMove);
						seenLvls[nextLvlString] = true;
					}
				}
				if(lvl.canTileMoveTo(e.getKey(), e.getValue().getX(), e.getValue().getY() - 1) == true) {
					nextLvl = lvl.clone();
					nextLvl.setTilePositionInternal(e.getKey(), e.getValue().getX(), e.getValue().getY() - 1);
					nextLvlString = this.lvlToString(nextLvl);
					if (nextLvlString==searchString)plugin.log("Found in depth "+depth);

					if (!seenLvls[nextLvlString]) {
						nextMove = { lvl: nextLvl, nextMoves: [] }
						nextMoves.push(nextMove);
						seenLvls[nextLvlString] = true;
					}
				}
				if(lvl.canTileMoveTo(e.getKey(), e.getValue().getX()+1, e.getValue().getY() ) == true) {
					nextLvl = lvl.clone();
					nextLvl.setTilePositionInternal(e.getKey(), e.getValue().getX()+1, e.getValue().getY() );
					nextLvlString = this.lvlToString(nextLvl);
					if (nextLvlString==searchString)plugin.log("Found in depth "+depth);

					if (!seenLvls[nextLvlString]) {
						nextMove = { lvl: nextLvl, nextMoves: [] }
						nextMoves.push(nextMove);
						seenLvls[nextLvlString] = true;
					}
				}
				if(lvl.canTileMoveTo(e.getKey(), e.getValue().getX()-1, e.getValue().getY()) == true) {
					nextLvl = lvl.clone();
					nextLvl.setTilePositionInternal(e.getKey(), e.getValue().getX()-1, e.getValue().getY() );
					nextLvlString = this.lvlToString(nextLvl);
					if (nextLvlString==searchString)plugin.log("Found in depth "+depth);
					if (!seenLvls[nextLvlString]) {
						nextMove = { lvl: nextLvl, nextMoves: [] }
						nextMoves.push(nextMove);
						seenLvls[nextLvlString] = true;
					}
				}
			}
			if(depth> 1){
				for(e in nextMoves){
					nextMoves[e].nextMoves = this.generateNextMoves(nextMoves[e].lvl, parent, seenLvls, depth - 1)
				}
			}
			return nextMoves;
		}	
	})) ();
return plugin;
}());