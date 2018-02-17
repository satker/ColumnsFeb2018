package columns;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;

public class Game implements Runnable, ModelListener {
	
	private static final int CLEARED_CELL_COLOR = 8;
	static final int SL = 25; 
	static final int MaxLevel = 7,
			TimeShift = 250, 
			FigToDrop = 33, 
			MinTimeShift = 200, 
			LeftBorder = 2,
			TopBorder = 2;

	Color MyStyles[] = { Color.black, Color.cyan, Color.blue, Color.red,
			Color.green, Color.yellow, Color.pink, Color.magenta, Color.black };

	int i, j, ii;
	int ch;

	
	long tc;
	Font fCourier;

	boolean KeyPressed = false;
	Graphics _gr;

	Thread thr = null;
	Model model;
	private GameService service;

	public Game(GameService service) {
		this.service = service;
		
	}

	@Override
	public void clearMatchedCells(int a, int b, int c, int d, int i, int j) {
		DrawBox(a, b, CLEARED_CELL_COLOR);
		DrawBox(j, i, CLEARED_CELL_COLOR);
		DrawBox(c, d, CLEARED_CELL_COLOR);
	}

	void Delay(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}

	void DrawBox(int x, int y, int c) {
		if (c == 0) {
			_gr.setColor(Color.black);
			_gr.fillRect(LeftBorder + x * SL - SL, TopBorder + y * SL - SL, SL,
					SL);
			_gr.drawRect(LeftBorder + x * SL - SL, TopBorder + y * SL - SL, SL,
					SL);
		} else if (c == 8) {
			_gr.setColor(Color.white);
			_gr.drawRect(LeftBorder + x * SL - SL + 1,
					TopBorder + y * SL - SL + 1, SL - 2, SL - 2);
			_gr.drawRect(LeftBorder + x * SL - SL + 2,
					TopBorder + y * SL - SL + 2, SL - 4, SL - 4);
			_gr.setColor(Color.black);
			_gr.fillRect(LeftBorder + x * SL - SL + 3,
					TopBorder + y * SL - SL + 3, SL - 6, SL - 6);
		} else {
			_gr.setColor(MyStyles[c]);
			_gr.fillRect(LeftBorder + x * SL - SL, TopBorder + y * SL - SL, SL,
					SL);
			_gr.setColor(Color.black);
			_gr.drawRect(LeftBorder + x * SL - SL, TopBorder + y * SL - SL, SL,
					SL);
		}
		// g.setColor (Color.black);
	}

	void DrawField() {
		int i, j;
		for (i = 1; i <= Model.Depth; i++) {
			for (j = 1; j <= Model.Width; j++) {
				DrawBox(j, i, model.newField[j][i]);
			}
		}
	}

	void DrawFigure(Figure f) {
		DrawBox(f.x, f.y, f.colors[1]);
		DrawBox(f.x, f.y + 1, f.colors[2]);
		DrawBox(f.x, f.y + 2, f.colors[3]);
	}

	void HideFigure(Figure f) {
		DrawBox(f.x, f.y, 0);
		DrawBox(f.x, f.y + 1, 0);
		DrawBox(f.x, f.y + 2, 0);
	}

	public void init() {
		model = new Model(this);
		model.newField = new int[Model.Width + 2][Model.Depth + 2];
		model.oldField = new int[Model.Width + 2][Model.Depth + 2];
	}

	public boolean keyDown(Event e, int k) {
		KeyPressed = true;
		ch = e.key;
		return true;
	}

	public boolean lostFocus(Event e, Object w) {
		KeyPressed = true;
		ch = 'P';
		return true;
	}

	public void paint(Graphics g) {
		// ShowHelp(g);

		g.setColor(Color.black);

		ShowLevel(g);
		ShowScore(g);
		DrawField();
		DrawFigure(model.fig);
	}

	@Override
	public void run() {
		model.init();
		j = 0;
		model.tripletsCollected = 0;
		_gr.setColor(Color.black);
		service.requestFocus();

		do {
			tc = System.currentTimeMillis();
			new Figure();
			DrawFigure(model.fig);
			while ((model.fig.y < Model.Depth - 2)
					&& (model.newField[model.fig.x][model.fig.y + 3] == 0)) {
				if ((int) (System.currentTimeMillis()
						- tc) > (MaxLevel - model.level) * TimeShift
								+ MinTimeShift) {
					tc = System.currentTimeMillis();
					HideFigure(model.fig);
					model.fig.y++;
					DrawFigure(model.fig);
				}
				model.DScore = (long) 0;
				do {
					Delay(50);
					if (KeyPressed) {
						KeyPressed = false;
						switch (ch) {
						case Event.LEFT:
							if ((model.fig.x > 1) && (model.newField[model.fig.x - 1][model.fig.y
									+ 2] == 0)) {
								HideFigure(model.fig);
								model.fig.x--;
								DrawFigure(model.fig);
							}
							break;
						case Event.RIGHT:
							if ((model.fig.x < Model.Width)
									&& (model.newField[model.fig.x + 1][model.fig.y
											+ 2] == 0)) {
								HideFigure(model.fig);
								model.fig.x++;
								DrawFigure(model.fig);
							}
							break;
						case Event.UP:
							i = model.fig.colors[1];
							model.fig.colors[1] = model.fig.colors[2];
							model.fig.colors[2] = model.fig.colors[3];
							model.fig.colors[3] = i;
							DrawFigure(model.fig);
							break;
						case Event.DOWN:
							i = model.fig.colors[1];
							model.fig.colors[1] = model.fig.colors[3];
							model.fig.colors[3] = model.fig.colors[2];
							model.fig.colors[2] = i;
							DrawFigure(model.fig);
							break;
						case ' ':
							HideFigure(model.fig);
							model.dropFigure();
							DrawFigure(model.fig);
							tc = 0;
							break;
						case 'P':
						case 'p':
							while (!KeyPressed) {
								HideFigure(model.fig);
								Delay(500);
								DrawFigure(model.fig);
								Delay(500);
							}
							tc = System.currentTimeMillis();
							break;
						case '-':
							if (model.level > 0)
								model.level--;
							model.tripletsCollected = 0;
							ShowLevel(_gr);
							break;
						case '+':
							if (model.level < MaxLevel)
								model.level++;
							model.tripletsCollected = 0;
							ShowLevel(_gr);
							break;
						}
					}
				} while ((int) (System.currentTimeMillis()
						- tc) <= (MaxLevel - model.level) * TimeShift
								+ MinTimeShift);
			}
			model.pasteFigure();
			do {
				model.NoChanges = true;
				model.testField();
				if (!model.NoChanges) {
					Delay(500);
					model.packField();
					DrawField();
					model.score += model.DScore;
					ShowScore(_gr);
					if (model.tripletsCollected >= FigToDrop) {
						model.tripletsCollected = 0;
						if (model.level < MaxLevel)
							model.level++;
						ShowLevel(_gr);
					}
				}
			} while (!model.NoChanges);
		} while (!model.isFieldFull());
	}

	void ShowHelp(Graphics g) {
		g.setColor(Color.black);

		g.drawString(" Keys available:", 200 - LeftBorder, 102);
		g.drawString("Roll Box Up:     ", 200 - LeftBorder, 118);
		g.drawString("Roll Box Down:   ", 200 - LeftBorder, 128);
		g.drawString("Figure Left:     ", 200 - LeftBorder, 138);
		g.drawString("Figure Right:    ", 200 - LeftBorder, 148);
		g.drawString("Level High/Low: +/-", 200 - LeftBorder, 158);
		g.drawString("Drop Figure:   space", 200 - LeftBorder, 168);
		g.drawString("Pause:           P", 200 - LeftBorder, 180);
		g.drawString("Quit:     Esc or Q", 200 - LeftBorder, 190);
	}

	void ShowLevel(Graphics g) {
		g.setColor(Color.black);
		g.clearRect(LeftBorder + 100, 390, 100, 20);
		g.drawString("Level: " + model.level, LeftBorder + 100, 400);
	}

	void ShowScore(Graphics g) {
		g.setColor(Color.black);
		g.clearRect(LeftBorder, 390, 100, 20);
		g.drawString("Score: " + model.score, LeftBorder, 400);
	}

	public void start() {
		_gr.setColor(Color.black);

		// setBackground (new Color(180,180,180));

		if (thr == null) {
			thr = new Thread(this);
			thr.start();
		}
	}

	public void stop() {
		if (thr != null) {
			thr.stop();
			thr = null;
		}
	}
	


}
