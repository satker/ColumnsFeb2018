package columns;

public interface ModelListener {

	void clearMatchedCells(int a, int b, int c, int d, int i, int j);

	void nextRound();

	void figureHasShifted(int xShift, int yShift);

}
