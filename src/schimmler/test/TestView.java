package schimmler.test;

import java.util.HashMap;

import schimmler.architecture.Model;
import schimmler.architecture.Tile;
import schimmler.architecture.plugin.InputPlugin;
import schimmler.architecture.plugin.View;

public class TestView implements View, InputPlugin {

	@Override
	public void init(Model m) {
		render(m);
	}

	@Override
	public String getName() {
		return "ASCIIView";
	}

	@Override
	public void onUpdate(Model m) {

	}

	// render the current frame as ASCII art
	private void render(Model m) {
		if (m.getLevel() == null)
			return;
		HashMap<String, Character> map = new HashMap<String, Character>();
		char c = '0';
		for (String s : m.getLevel().getTileMap().keySet())
			map.put(s, c++);

		map.put(null, ' ');

		System.out.print('|');
		for (int x = 0; x < m.getLevel().getWidth(); x++)
			System.out.print('=');
		System.out.print('|');
		System.out.println();
		for (int y = 0; y < m.getLevel().getHeight(); y++) {
			System.out.print('|');
			for (int x = 0; x < m.getLevel().getWidth(); x++) {
				String atField = m.getLevel().fieldOccupied(x, y);
				if(atField != null && atField.equals(m.getLevel().getSelected()))
					System.out.print('X');
				else
					System.out.print(map.get(atField));
			}
			System.out.print('|');
			System.out.println();
		}
		System.out.print('|');
		for (int x = 0; x < m.getLevel().getWidth(); x++)
			System.out.print('=');
		System.out.print('|');
		System.out.println();
	}

	@Override
	public void onTileSelected(Model m, Tile tile, String id) {
		render(m);
	}

	@Override
	public void onTileDeselected(Model m, Tile tile, String id) {
		render(m);
	}

	@Override
	public void onTileMoved(Model m, Tile tile, String id, int oldx, int oldy) {
		render(m);
	}

}
