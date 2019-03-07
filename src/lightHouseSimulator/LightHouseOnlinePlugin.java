package lightHouseSimulator;

import java.util.Map.Entry;

import de.cau.infprogoo.lighthouse.ILighthouseInputListener;
import schimmler.architecture.Model;
import schimmler.architecture.Tile;
import schimmler.architecture.plugin.GraphicalFilterPlugin;
import schimmler.architecture.plugin.GraphicalView;
import schimmler.architecture.plugin.InputPlugin;

public class LightHouseOnlinePlugin implements GraphicalFilterPlugin {

	private LightHouseOnline lightHouse;
	
	
	@Override
	public void init(Model m) {
		lightHouse = new LightHouseOnline();
		lightHouse.setGapsRatio(1, 0);
		lightHouse.getBridge().addKeyListener(new ILighthouseInputListener() {
			
			@Override
			public void keyboardEvent(int source, int button, boolean down) {
				if(!down) return;
				if(button == (int)'W')
					move(m, "up");
				if(button == (int)'S')
					move(m, "down");
				if(button == (int)'D')
					move(m, "right");
				if(button == (int)'A')
					move(m, "left");
				if(button == (int)'\t')
					tab(m);
				if(button == 16) //shift
					untab(m);
			}

			@Override
			public void controllerEvent(int source, int button, boolean down) {
			}
			
			
			public void tab(Model model) {
				if(model.getLevel() == null) return;
				boolean next = false;
				if(model.getLevel().getSelected() == null) next = true;
				for(Entry<String, Tile> entry: model.getLevel().getTileMap().entrySet()) {
					if(next == true) {
						if(model.getLevel().getSelected() != null)
							InputPlugin.tileDeselected(model, model.getLevel().getTile(model.getLevel().getSelected()), model.getLevel().getSelected());
						model.getLevel().setSelected(entry.getKey());
						InputPlugin.tileSelected(model, entry.getValue(), entry.getKey());
						return;
					}
					if(model.getLevel().getSelected().equals(entry.getKey()))
						next = true;
				}
				if(model.getLevel().getSelected() != null)
					InputPlugin.tileDeselected(model, model.getLevel().getTile(model.getLevel().getSelected()), model.getLevel().getSelected());
				model.getLevel().setSelected(null);
			}
			
			public void untab(Model model) {
				if(model.getLevel() == null) return;

				String lastTile = null;
				for(Entry<String, Tile> entry: model.getLevel().getTileMap().entrySet()) {
					if(model.getLevel().getSelected() != null && model.getLevel().getSelected().equals(entry.getKey())) {
						InputPlugin.tileDeselected(model, entry.getValue(), entry.getKey());
						model.getLevel().setSelected(lastTile);
						if(lastTile != null)
							InputPlugin.tileSelected(model, model.getLevel().getTile(lastTile), lastTile);
						return;
					}
					lastTile = entry.getKey();
				}
				
				model.getLevel().setSelected(lastTile);
				InputPlugin.tileSelected(model, model.getLevel().getTile(lastTile), lastTile);
			}
			
			
			public void move(Model model, String dir) {
				if (model.getLevel() == null)
					return;
				if (model.getLevel().getSelected() == null) {
					System.out.println("There is no tile selected");
					return;
				}
				String name = model.getLevel().getSelected();
				Tile tile = model.getLevel().getTile(name);
				int ox = tile.getX();
				int oy = tile.getY();
				int x = ox;
				int y = oy;
				if (dir.equalsIgnoreCase("up") || dir.equalsIgnoreCase("north") || dir.equalsIgnoreCase("u")
						|| dir.equalsIgnoreCase("n"))
					y--;
				else if (dir.equalsIgnoreCase("down") || dir.equalsIgnoreCase("south")
						|| dir.equalsIgnoreCase("d") || dir.equalsIgnoreCase("s"))
					y++;
				else if (dir.equalsIgnoreCase("right") || dir.equalsIgnoreCase("east")
						|| dir.equalsIgnoreCase("r") || dir.equalsIgnoreCase("e"))
					x++;
				else if (dir.equalsIgnoreCase("left") || dir.equalsIgnoreCase("west")
						|| dir.equalsIgnoreCase("l") || dir.equalsIgnoreCase("w"))
					x--;
				else {
					System.out.println("Direction '" + dir + "' unknown.");
					return;
				}
				if (!model.getLevel().inLevel(x, y)) {
					System.out.println("Position is outside of the map");
					return;
				}
				if (!((Math.abs(ox - x) <= 1 && oy - y == 0) || (Math.abs(oy - y) <= 1 && ox - x == 0))) {
					System.out.println("Field is too far away.");
					return;
				}

				for (int i = 0; i < model.getLevel().getHeight(); i++)
					for (int j = 0; j < model.getLevel().getWidth(); j++) {
						if (!tile.fieldOccupiedRelative(i, j))
							continue;
						if (!model.getLevel().inLevel(i + x, j + y)) {
							System.out.println("Position is outside of the map");
							return;
						}
						String f = model.getLevel().fieldOccupied(i + x, j + y, name);
						if (f != null) {
							System.out.println("The field is occupied by tile '" + f + "'");
							return;
						}
					}
				model.setTilePosition(name, x, y);
				InputPlugin.tileMoved(model, tile, name, ox, oy);
			}
		});	}

	@Override
	public void onFilter(Model m, GraphicalView view, int[] img, int width, int height) {
		lightHouse.setData(img, width, height);
	}

	public void onKill(Model m) {
		lightHouse.exit();
	}

	@Override
	public String getName() {
		return "LightHousePlugin";
	}

	public boolean shouldFilter(int priority) {
		return priority == GraphicalFilterPlugin.PRIORITY_LAST;
	}

}
