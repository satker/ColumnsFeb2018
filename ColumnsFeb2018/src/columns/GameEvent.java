package columns;

public interface GameEvent {
	
	interface ShowScore extends GameEvent {}
	interface Pause extends GameEvent {}
	interface DrawField extends GameEvent {}
	interface ShowLevel extends GameEvent {}

}
