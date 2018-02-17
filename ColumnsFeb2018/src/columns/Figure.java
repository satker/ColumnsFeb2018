package columns;

import java.util.Random;

class Figure {
	static Random r = new Random();

	int x;
	int y;
	int colors[] = new int[4];

	Figure() {
		x = Model.Width / 2 + 1;
		y = 1;
		colors[0] = 0;
		colors[1] = (int) (Math.abs(r.nextInt()) % 7 + 1);
		colors[2] = (int) (Math.abs(r.nextInt()) % 7 + 1);
		colors[3] = (int) (Math.abs(r.nextInt()) % 7 + 1);
	}
}