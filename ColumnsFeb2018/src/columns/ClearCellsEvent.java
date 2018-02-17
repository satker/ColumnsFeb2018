package columns;

public class ClearCellsEvent implements GameEvent {
	

	int[] coords;

	public ClearCellsEvent(int... coords) {
		this.coords = coords;
	}

}
