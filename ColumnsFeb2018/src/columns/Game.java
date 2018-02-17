package columns;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;

public class Game implements Runnable {
	
	static final int SL = 25; 
	static final int MaxLevel = 7,
			TimeShift = 250, 
			FigToDrop = 33, 
			MinTimeShift = 200, 
			LeftBorder = 2,
			TopBorder = 2;

	Color MyStyles[] = { Color.black, Color.cyan, Color.blue, Color.red,
			Color.green, Color.yellow, Color.pink, Color.magenta, Color.black };

	int i, j, ii, k, ch;

	
	long DScore, tc;
	Font fCourier;
	boolean NoChanges = true, KeyPressed = false;
	Graphics _gr;

	Thread thr = null;
	private Model model;
	private GameService service;

	public Game(GameService service) {
		this.service = service;
		
	}

	void CheckNeighbours(int a, int b, int c, int d, int i, int j) {
		if ((model.$NewField[j][i] == model.$NewField[a][b])
				&& (model.$NewField[j][i] == model.$NewField[c][d])) {
			model.$OldField[a][b] = 0;
			DrawBox(a, b, 8);
			model.$OldField[j][i] = 0;
			DrawBox(j, i, 8);
			model.$OldField[c][d] = 0;
			DrawBox(c, d, 8);
			NoChanges = false;
			model.$Score += (model.$Level + 1) * 10;
			k++;
		}
		;
	}

	void Delay(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
		;
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

	void DrawField(Graphics g) {
		int i, j;
		for (i = 1; i <= Model.$Depth; i++) {
			for (j = 1; j <= Model.$Width; j++) {
				DrawBox(j, i, model.$NewField[j][i]);
			}
		}
	}

	void DrawFigure(Figure f) {
		DrawBox(f.x, f.y, f.colors[1]);
		DrawBox(f.x, f.y + 1, f.colors[2]);
		DrawBox(f.x, f.y + 2, f.colors[3]);
	}

	void DropFigure(Figure f) {
		int zz;
		if (f.y < Model.$Depth - 2) {
			zz = Model.$Depth;
			while (model.$NewField[f.x][zz] > 0)
				zz--;
			DScore = (((model.$Level + 1) * (Model.$Depth * 2 - f.y - zz) * 2) % 5) * 5;
			f.y = zz - 2;
		}
	}

	boolean FullField() {
		int i;
		for (i = 1; i <= Model.$Width; i++) {
			if (model.$NewField[i][3] > 0)
				return true;
		}
		return false;
	}

	void HideFigure(Figure f) {
		DrawBox(f.x, f.y, 0);
		DrawBox(f.x, f.y + 1, 0);
		DrawBox(f.x, f.y + 2, 0);
	}

	public void init() {
		model = new Model();
		model.$NewField = new int[Model.$Width + 2][Model.$Depth + 2];
		model.$OldField = new int[Model.$Width + 2][Model.$Depth + 2];
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

	void PackField() {
		int i, j, n;
		for (i = 1; i <= Model.$Width; i++) {
			n = Model.$Depth;
			for (j = Model.$Depth; j > 0; j--) {
				if (model.$OldField[i][j] > 0) {
					model.$NewField[i][n] = model.$OldField[i][j];
					n--;
				}
			}
			;
			for (j = n; j > 0; j--)
				model.$NewField[i][j] = 0;
		}
	}

	public void paint(Graphics g) {
		// ShowHelp(g);

		g.setColor(Color.black);

		ShowLevel(g);
		ShowScore(g);
		DrawField(g);
		DrawFigure(model.$Fig);
	}

	void PasteFigure(Figure f) {
		model.$NewField[f.x][f.y] = f.colors[1];
		model.$NewField[f.x][f.y + 1] = f.colors[2];
		model.$NewField[f.x][f.y + 2] = f.colors[3];
	}

	@Override
	public void run() {
		for (i = 0; i < Model.$Width + 1; i++) {
			for (j = 0; j < Model.$Depth + 1; j++) {
				model.$NewField[i][j] = 0;
				model.$OldField[i][j] = 0;
			}
		}
		model.$Level = 0;
		model.$Score = (long) 0;
		j = 0;
		k = 0;
		_gr.setColor(Color.black);
		service.requestFocus();

		do {
			tc = System.currentTimeMillis();
			new Figure();
			DrawFigure(model.$Fig);
			while ((model.$Fig.y < Model.$Depth - 2)
					&& (model.$NewField[model.$Fig.x][model.$Fig.y + 3] == 0)) {
				if ((int) (System.currentTimeMillis()
						- tc) > (MaxLevel - model.$Level) * TimeShift
								+ MinTimeShift) {
					tc = System.currentTimeMillis();
					HideFigure(model.$Fig);
					model.$Fig.y++;
					DrawFigure(model.$Fig);
				}
				DScore = 0;
				do {
					Delay(50);
					if (KeyPressed) {
						KeyPressed = false;
						switch (ch) {
						case Event.LEFT:
							if ((model.$Fig.x > 1) && (model.$NewField[model.$Fig.x - 1][model.$Fig.y
									+ 2] == 0)) {
								HideFigure(model.$Fig);
								model.$Fig.x--;
								DrawFigure(model.$Fig);
							}
							break;
						case Event.RIGHT:
							if ((model.$Fig.x < Model.$Width)
									&& (model.$NewField[model.$Fig.x + 1][model.$Fig.y
											+ 2] == 0)) {
								HideFigure(model.$Fig);
								model.$Fig.x++;
								DrawFigure(model.$Fig);
							}
							break;
						case Event.UP:
							i = model.$Fig.colors[1];
							model.$Fig.colors[1] = model.$Fig.colors[2];
							model.$Fig.colors[2] = model.$Fig.colors[3];
							model.$Fig.colors[3] = i;
							DrawFigure(model.$Fig);
							break;
						case Event.DOWN:
							i = model.$Fig.colors[1];
							model.$Fig.colors[1] = model.$Fig.colors[3];
							model.$Fig.colors[3] = model.$Fig.colors[2];
							model.$Fig.colors[2] = i;
							DrawFigure(model.$Fig);
							break;
						case ' ':
							HideFigure(model.$Fig);
							DropFigure(model.$Fig);
							DrawFigure(model.$Fig);
							tc = 0;
							break;
						case 'P':
						case 'p':
							while (!KeyPressed) {
								HideFigure(model.$Fig);
								Delay(500);
								DrawFigure(model.$Fig);
								Delay(500);
							}
							tc = System.currentTimeMillis();
							break;
						case '-':
							if (model.$Level > 0)
								model.$Level--;
							k = 0;
							ShowLevel(_gr);
							break;
						case '+':
							if (model.$Level < MaxLevel)
								model.$Level++;
							k = 0;
							ShowLevel(_gr);
							break;
						}
					}
				} while ((int) (System.currentTimeMillis()
						- tc) <= (MaxLevel - model.$Level) * TimeShift
								+ MinTimeShift);
			}
			;
			PasteFigure(model.$Fig);
			do {
				NoChanges = true;
				TestField();
				if (!NoChanges) {
					Delay(500);
					PackField();
					DrawField(_gr);
					model.$Score += DScore;
					ShowScore(_gr);
					if (k >= FigToDrop) {
						k = 0;
						if (model.$Level < MaxLevel)
							model.$Level++;
						ShowLevel(_gr);
					}
				}
			} while (!NoChanges);
		} while (!FullField());
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
		g.drawString("Level: " + model.$Level, LeftBorder + 100, 400);
	}

	void ShowScore(Graphics g) {
		g.setColor(Color.black);
		g.clearRect(LeftBorder, 390, 100, 20);
		g.drawString("Score: " + model.$Score, LeftBorder, 400);
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

	void TestField() {
		int i, j;
		for (i = 1; i <= Model.$Depth; i++) {
			for (j = 1; j <= Model.$Width; j++) {
				model.$OldField[j][i] = model.$NewField[j][i];
			}
		}
		for (i = 1; i <= Model.$Depth; i++) {
			for (j = 1; j <= Model.$Width; j++) {
				if (model.$NewField[j][i] > 0) {
					CheckNeighbours(j, i - 1, j, i + 1, i, j);
					CheckNeighbours(j - 1, i, j + 1, i, i, j);
					CheckNeighbours(j - 1, i - 1, j + 1, i + 1, i, j);
					CheckNeighbours(j + 1, i - 1, j - 1, i + 1, i, j);
				}
			}
		}
	}
	


}
