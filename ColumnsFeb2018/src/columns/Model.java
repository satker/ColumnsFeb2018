package columns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

	static final int Depth = 15;
	static final int Width = 7;

	List<ModelListener> listeners = new ArrayList<>();

	public Model(ModelListener... listeners) {
		this.listeners.addAll(Arrays.asList(listeners));
	}

	public void register(ModelListener listener) {
		listeners.add(listener);
	}

	public void unregister(ModelListener listener) {
		listeners.remove(listener);
	}

	int level;

	long score;

	int newField[][];

	int oldField[][];

	Figure fig;

	int tripletsCollected;
	boolean NoChanges;
	long DropBonusScore;

	boolean allCellsHaveSameColor(int a, int b, int c, int d, int i, int j) {
		return (newField[j][i] == newField[a][b])
				&& (newField[j][i] == newField[c][d]);
	}

	void CheckNeighbours(List<GameEvent> events, int a, int b, int c, int d,
			int i, int j) {
		if (allCellsHaveSameColor(a, b, c, d, i, j)) {
			oldField[a][b] = 0;
			oldField[j][i] = 0;
			oldField[c][d] = 0;
			events.add(new ClearCellsEvent(a, b, c, d, i, j));
			NoChanges = false;
			score += (level + 1) * 10;
			tripletsCollected = tripletsCollected + 1;
		}
	}

	void dropFigure() {
		int zz;
		int oldY = fig.y;
		if (fig.y < Model.Depth - 2) {
			zz = Model.Depth;
			while (newField[fig.x][zz] > 0)
				zz--;
			DropBonusScore = (long) ((((level + 1)
					* (Model.Depth * 2 - fig.y - zz) * 2) % 5) * 5);
			fig.y = zz - 2;
		}
		fireFigureShift(0, fig.y - oldY);
	}

	boolean isFieldFull() {
		int i;
		for (i = 1; i <= Model.Width; i++) {
			if (newField[i][3] > 0)
				return true;
		}
		return false;
	}

	void packField() {
		int i, j, n;
		for (i = 1; i <= Model.Width; i++) {
			n = Model.Depth;
			for (j = Model.Depth; j > 0; j--) {
				if (oldField[i][j] > 0) {
					newField[i][n] = oldField[i][j];
					n--;
				}
			}
			for (j = n; j > 0; j--)
				newField[i][j] = 0;
		}
	}

	void pasteFigure() {
		newField[fig.x][fig.y] = fig.colors[1];
		newField[fig.x][fig.y + 1] = fig.colors[2];
		newField[fig.x][fig.y + 2] = fig.colors[3];
	}

	void testField(List<GameEvent> events) {
		int i, j;
		for (i = 1; i <= Model.Depth; i++) {
			for (j = 1; j <= Model.Width; j++) {
				oldField[j][i] = newField[j][i];
			}
		}
		for (i = 1; i <= Model.Depth; i++) {
			for (j = 1; j <= Model.Width; j++) {
				if (newField[j][i] > 0) {
					CheckNeighbours(events, j, i - 1, j, i + 1, i, j);
					CheckNeighbours(events, j - 1, i, j + 1, i, i, j);
					CheckNeighbours(events, j - 1, i - 1, j + 1, i + 1, i, j);
					CheckNeighbours(events, j + 1, i - 1, j - 1, i + 1, i, j);
				}
			}
		}
	}

	public void init() {
		for (int i = 0; i < Model.Width + 1; i++) {
			for (int j = 0; j < Model.Depth + 1; j++) {
				newField[i][j] = 0;
				oldField[i][j] = 0;
			}
		}
		level = 0;
		score = 0L;
		tripletsCollected = 0;
	}

	public void nextRound() {
		fig = new Figure();
		fireNextRound();
	}

	private void fireNextRound() {
		listeners.forEach(l -> l.nextRound());
	}

	public boolean mayFigureMoveDown() {
		return (fig.y < Model.Depth - 2) && (newField[fig.x][fig.y + 3] == 0);
	}

	public void moveFigureDown() {
		if (!mayFigureMoveDown()) {
			finishRound();
			return;
		}
		fig.y++;
		fireFigureShift(0, 1);
	}

	private void fireFigureShift(int xShift, int yShift) {
		listeners.forEach(l -> l.figureHasShifted(xShift, yShift));
	}

	public void moveLeft() {
		if (!((fig.x > 1) && (newField[fig.x - 1][fig.y + 2] == 0))) {
			return; // guard condition
		}
		fig.x--;
		fireFigureShift(-1, 0);
	}

	public void moveRight() {
		if (!((fig.x < Model.Width) && (newField[fig.x + 1][fig.y + 2] == 0))) {
			return;
		}
		fig.x++;
		fireFigureShift(1, 0);
	}

	private void finishRound() {
		pasteFigure();
		List<GameEvent> events = new ArrayList<>();
		do {
			NoChanges = true;
			testField(events);
			if (!NoChanges) {
				events.add(new GameEvent.Pause() {
				});
				packField();
				events.add(new GameEvent.DrawField() {
				});
				score += DropBonusScore;
				events.add(new GameEvent.ShowScore() {
				});
				if (tripletsCollected >= Game.FigToDrop) {
					tripletsCollected = 0;
					if (level < Game.MaxLevel)
						level++;
					events.add(new GameEvent.ShowLevel() {
					});
				}
			}
		} while (!NoChanges);
		fireFinishRound(events);
	}

	private void fireFinishRound(List<GameEvent> events) {
		listeners.forEach(l -> l.finishRound(events));
	}

}
