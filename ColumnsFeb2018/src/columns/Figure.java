package columns;
import java.applet.*;
import java.awt.*;
import java.util.*;


class Figure {
	static int x=Model.Width/2+1, y=1, colors[]=new int[4];
	static Random r = new Random();

	Figure()
	{
		x = Model.Width/2+1;
		y = 1;
		colors[0] = 0;
		colors[1] = (int)(Math.abs(r.nextInt())%7+1);
		colors[2] = (int)(Math.abs(r.nextInt())%7+1);
		colors[3] = (int)(Math.abs(r.nextInt())%7+1);
	}
}