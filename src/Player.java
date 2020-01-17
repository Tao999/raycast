
public class Player {
	public final static double SPAWN_X = 1;
	public final static double SPAWN_Y = 5.5;
	public final static double SPEED = 0.05;
	public final static double SPEED_ROTATION = 0.03;

	public Point<Double> position;
	public double angle; // radian

	Player() {
		position = new Point<Double>(SPAWN_X, SPAWN_Y);
		angle = 0;
	}

	public void turnLeft() {
		angle -= SPEED_ROTATION;
		if (angle < -Math.PI)
			angle += Math.PI*2;
	}

	public void turnRight() {
		angle += SPEED_ROTATION;
		if (angle > Math.PI)
			angle -= Math.PI*2;
	}

	public void goForward() {
		position.x += Math.cos(angle) * (double)SPEED;
		position.y += Math.sin(angle) * (double)SPEED;
	}

	public void goBackward() {
		position.x -= Math.cos(angle) * (double)SPEED;
		position.y -= Math.sin(angle) * (double)SPEED;
	}
	
	public void goLeft() {
		position.x += Math.sin(angle) * (double)SPEED;
		position.y -= Math.cos(angle) * (double)SPEED;
	}

	public void goRight() {
		position.x -= Math.sin(angle) * (double)SPEED;
		position.y += Math.cos(angle) * (double)SPEED;
	}
	
}
