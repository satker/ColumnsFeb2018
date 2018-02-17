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

}
