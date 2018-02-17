package columns;

import java.applet.*;
import java.awt.*;
import java.util.*;

public class Columns extends Applet implements GameService  {
	
	Game game;

	@Override
	public void init() {
		game = new Game(this);
		game.init();
		game._gr = getGraphics();
	}

	@Override
	public boolean keyDown(Event e, int k) {
		return game.keyDown(e, k);
	}

	@Override
	public boolean lostFocus(Event e, Object w) {
		return game.lostFocus(e, w);
	}

	@Override
	public void paint(Graphics g) {
		game.paint(g);
		requestFocus();
	}


	@Override
	public void start() {
		game.start();
	}

	@Override
	public void stop() {
		game.stop();
	}
}