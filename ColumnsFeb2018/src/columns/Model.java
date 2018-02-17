package columns;

public class Model {

	static final int Depth = 15;
	static final int Width = 7;
	
	int level; 

	long score;

	int newField[][];

	int oldField[][];
	
	Figure fig;
	
	int tripletsCollected;

	boolean allCellsHaveSameColor(Game game, int a, int b, int c, int d, int i, int j) {
		return (newField[j][i] == newField[a][b])
				&& (newField[j][i] == newField[c][d]);
	}

}
