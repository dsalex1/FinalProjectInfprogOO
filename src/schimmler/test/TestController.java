package schimmler.test;

import java.util.Map.Entry;
import java.util.Scanner;

import schimmler.architecture.InputPlugin;
import schimmler.architecture.Model;
import schimmler.architecture.Plugin;
import schimmler.architecture.Tile;

public class TestController implements Plugin {

	private Model model;
	private Thread thread;
	
	@Override
	public void init(Model m) {
		this.model = m;
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner scr = new Scanner(System.in);
				outer: while(scr.hasNext()) {
					String line = scr.nextLine();
					if(line.matches("(?i)^select .+$")) {
						if(model.getLevel() == null) continue;
						String name = line.split(" ")[1].trim();
						Tile tile = model.getLevel().getTile(name);
						if(tile == null) {
							System.out.println("There is no tile with the name '"+name+"'");
							continue;
						}
						if(model.getLevel().getSelected() != null) {
							System.out.println("The tile '"+model.getLevel().getSelected()+"' is already selected");
							continue;
						}
						model.getLevel().setSelected(name);
						InputPlugin.tileSelected(model, tile, name);
					}else if(line.matches("(?i)^deselect")) {
						if(model.getLevel() == null) continue;
						if(model.getLevel().getSelected() == null) {
							System.out.println("There is no tile to deselect");
							continue;
						}
						String name = model.getLevel().getSelected();
						Tile tile = model.getLevel().getTile(name);
						model.getLevel().setSelected(null);
						InputPlugin.tileDeselected(model, tile, name);
					}else if(line.matches("(?i)^move .+$")) {
						if(model.getLevel() == null) continue;
						if(model.getLevel().getSelected() == null) {
							System.out.println("There is no tile selected");
							continue;
						}
						String dir = line.split(" ")[1];
						String name = model.getLevel().getSelected();
						Tile tile = model.getLevel().getTile(name);
						int ox = tile.getX();
						int oy = tile.getY();
						int x = ox;
						int y = oy;
						if(dir.equalsIgnoreCase("up") || dir.equalsIgnoreCase("north") || dir.equalsIgnoreCase("u") || dir.equalsIgnoreCase("n"))
							y--;
						else if(dir.equalsIgnoreCase("down") || dir.equalsIgnoreCase("south") || dir.equalsIgnoreCase("d") || dir.equalsIgnoreCase("s"))
							y++;
						else if(dir.equalsIgnoreCase("right") || dir.equalsIgnoreCase("east") || dir.equalsIgnoreCase("r") || dir.equalsIgnoreCase("e"))
							x++;
						else if(dir.equalsIgnoreCase("left") || dir.equalsIgnoreCase("west") || dir.equalsIgnoreCase("l") || dir.equalsIgnoreCase("w"))
							x--;
						else {
							System.out.println("Direction '"+dir+"' unknown.");
							continue;
						}
						if(!model.getLevel().inLevel(x, y)) {
							System.out.println("Position is outside of the map");
							continue;
						}
						if(!((Math.abs(ox-x) <= 1 && oy-y == 0) || (Math.abs(oy-y) <= 1 && ox-x == 0))) {
							System.out.println("Field is too far away.");
							continue;
						}
						
						for(int i=0;i<model.getLevel().getHeight();i++)
							for(int j=0;j<model.getLevel().getWidth();j++) {
								if(!tile.fieldOccupiedRelative(i, j)) continue;
								if(!model.getLevel().inLevel(i+x, j+y)) {
									System.out.println("Position is outside of the map");
									continue outer;
								}
								String f = model.getLevel().fieldOccupied(i+x, j+y, name);
								if(f != null) {
									System.out.println("The field is occupied by tile '"+f+"'");
									continue outer;
								}
						}
						
						tile.setX(x);
						tile.setY(y);
						InputPlugin.tileMoved(model, tile, name, ox, oy);
					}else if(line.matches("(?i)^move \\d+ \\d+$")) {
						if(model.getLevel() == null) continue;
						if(model.getLevel().getSelected() == null) {
							System.out.println("There is no tile selected");
							continue;
						}
						String name = model.getLevel().getSelected();
						Tile tile = model.getLevel().getTile(name);
						int x = Integer.parseInt(line.split(" ")[1]);
						int y = Integer.parseInt(line.split(" ")[2]);
						int ox = tile.getX();
						int oy = tile.getY();
						if(!model.getLevel().inLevel(x, y)) {
							System.out.println("Position is outside of the map");
							continue;
						}
						if(!((Math.abs(ox-x) <= 1 && oy-y == 0) || (Math.abs(oy-y) <= 1 && ox-x == 0))) {
							System.out.println("Field is too far away");
							continue;
						}
						
						for(int i=0;i<model.getLevel().getHeight();i++)
							for(int j=0;j<model.getLevel().getWidth();j++) {
								if(!tile.fieldOccupiedRelative(i, j)) continue;
								if(!model.getLevel().inLevel(i+x, j+y)) {
									System.out.println("Position is outside of the map");
									continue outer;
								}
								String f = model.getLevel().fieldOccupied(i+x, j+y, name);
								if(f != null) {
									System.out.println("The field is occupied by tile '"+f+"'");
									continue outer;
								}
						}
						
						tile.setX(x);
						tile.setY(y);
						InputPlugin.tileMoved(model, tile, name, ox, oy);
					}else if(line.matches("(i?)^list$")) {
						if(model.getLevel() == null) continue;
						System.out.println("Tiles:");
						for(Entry<String, Tile> e:m.getLevel().getTileMap().entrySet())
							System.out.println("   "+e.getKey());
					}else if(line.matches("(i?)^help$")) {
						System.out.println("select <id> ,deselect ,move <dir>, move <x> <y> ,list, help, exit");
					}else if(line.matches("(i?)^exit$")) {
						System.out.println("Exiting...");
						break;
					}else {
						System.out.println("?");
					}
				}
				scr.close();
				Plugin.kill(model);
			}

		});
		this.thread.start();
	}
	

	@Override
	public String getName() {
		return "ASCIIController";
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onKill(Model m) {
		try {
		if(this.thread != null && this.thread.isAlive())
			this.thread.stop();
		}catch(Exception e) {}
	}
	
	

}
