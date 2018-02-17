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
	long DScore;

	boolean allCellsHaveSameColor(int a, int b, int c, int d, int i, int j) {
		return (newField[j][i] == newField[a][b])
				&& (newField[j][i] == newField[c][d]);
	}

	void CheckNeighbours(int a, int b, int c, int d, int i, int j) {
		if (allCellsHaveSameColor(a, b, c, d, i, j)) {
			oldField[a][b] = 0;
			oldField[j][i] = 0;
			oldField[c][d] = 0;
			fireClearMatchedCells(a, b, c, d, i, j);
			NoChanges = false;
			score += (level + 1) * 10;
			tripletsCollected = tripletsCollected + 1;
		}
	}

	public void fireClearMatchedCells(int a, int b, int c, int d, int i,
			int j) {
		listeners.forEach(l -> l.clearMatchedCells(a, b, c, d, i, j));
	}

	void dropFigure() {
		int zz;
		if (fig.y < Model.Depth - 2) {
			zz = Model.Depth;
			while (newField[fig.x][zz] > 0)
				zz--;
			DScore = (long) ((((level + 1) * (Model.Depth * 2 - fig.y - zz) * 2) % 5) * 5);
			fig.y = zz - 2;
		}
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

	void testField() {
		int i, j;
		for (i = 1; i <= Model.Depth; i++) {
			for (j = 1; j <= Model.Width; j++) {
				oldField[j][i] = newField[j][i];
			}
		}
		for (i = 1; i <= Model.Depth; i++) {
			for (j = 1; j <= Model.Width; j++) {
				if (newField[j][i] > 0) {
					CheckNeighbours(j, i - 1, j, i + 1, i, j);
					CheckNeighbours(j - 1, i, j + 1, i, i, j);
					CheckNeighbours(j - 1, i - 1, j + 1, i + 1, i, j);
					CheckNeighbours(j + 1, i - 1, j - 1, i + 1, i, j);
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
	}

}
