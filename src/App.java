import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class App extends JFrame implements KeyListener {
	public static final int KB_UP = 0b0000000001;
	public static final int KB_RIGHT = 0b0000000010;
	public static final int KB_DOWN = 0b0000000100;
	public static final int KB_LEFT = 0b0000001000;
	public static final int KB_Z = 0b0000010000;
	public static final int KB_D = 0b0000100000;
	public static final int KB_S = 0b0001000000;
	public static final int KB_Q = 0b0010000000;
	public static final int KB_ESCAPE = 0b0100000000;
	public static final int KB_SPACE = 0b1000000000;
	static final long PROCC_REVOVERY = 16;
	public static final int BLOC_SIZE = 1;

	private Gfx gfx;
	private Thread m_thread;
	private int[][] map;
	private Player player;
	private Flag kbStatus;

	App() {
		gfx = new Gfx();
		setTitle("Test raycast");
		add(gfx);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		addKeyListener(this);
		System.setProperty("sun.java2d.opengl", "True");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		pack();

		kbStatus = new Flag();
		player = new Player();
		map = new int[][] { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, };

		// THREAD GAME
		m_thread = new Thread() {
			public void run() {
				long timeRegulator;
				long fpsCheck = 0;
				int fpsCount = 0;
				int latencySum = 0;

				while (true) {
					fpsCheck = timeRegulator = System.currentTimeMillis();
					procc();
					// affichage
					blast();

					// WAIT
					timeRegulator = System.currentTimeMillis() - timeRegulator;
					if (timeRegulator > PROCC_REVOVERY)
						timeRegulator = 0;
					else
						timeRegulator = PROCC_REVOVERY - timeRegulator;

					try {

						Thread.sleep(timeRegulator);

					} catch (InterruptedException ex) {

					}
					// affichage fps
					fpsCheck = System.currentTimeMillis() - fpsCheck;
					latencySum += fpsCheck;
					if (fpsCount++ == 30) {
						fpsCount = 0;
						setTitle("Test raycast | " + (int) (1000 / (latencySum / 30.0)) + " fps");
						latencySum = 0;
					}
				}
			};

		};

		m_thread.start();

	}

	private void procc() {
		Point<Double> pPrev = new Point<Double>(player.position.x, player.position.y);
		if (kbStatus.isBitSet(KB_Z))
			player.goForward();
		if (kbStatus.isBitSet(KB_D))
			player.goRight();
		if (kbStatus.isBitSet(KB_S))
			player.goBackward();
		if (kbStatus.isBitSet(KB_Q))
			player.goLeft();
		if (kbStatus.isBitSet(KB_RIGHT))
			player.turnRight();
		if (kbStatus.isBitSet(KB_LEFT))
			player.turnLeft();

		if (map[player.position.x.intValue()][player.position.y.intValue()] != 0) {
			player.position=pPrev;
			//player.position.x -= (player.position.x - pPrev.x);
			//player.position.y = pPrev.y;
		}
	}

	public static void main(String[] args) {
		new App();
	}

	void blast() {
		// blast
		gfx.blastMap(map, player);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP: // UP
			kbStatus.bitSet(KB_UP);
			break;

		case KeyEvent.VK_RIGHT: // RIGHT
			kbStatus.bitSet(KB_RIGHT);
			break;

		case KeyEvent.VK_DOWN: // DOWN
			kbStatus.bitSet(KB_DOWN);
			break;

		case KeyEvent.VK_LEFT: // LEFT
			kbStatus.bitSet(KB_LEFT);
			break;

		case KeyEvent.VK_Z: // z
			kbStatus.bitSet(KB_Z);
			break;

		case KeyEvent.VK_D: // d
			kbStatus.bitSet(KB_D);
			break;

		case KeyEvent.VK_S: // s
			kbStatus.bitSet(KB_S);
			break;

		case KeyEvent.VK_Q: // q
			kbStatus.bitSet(KB_Q);
			break;

		case KeyEvent.VK_SPACE: // space
			kbStatus.bitSet(KB_SPACE);
			break;

		case KeyEvent.VK_ESCAPE:
			kbStatus.bitSet(KB_ESCAPE);
			break;

		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP: // UP
			kbStatus.bitClr(KB_UP);
			break;

		case KeyEvent.VK_RIGHT: // RIGHT
			kbStatus.bitClr(KB_RIGHT);
			break;

		case KeyEvent.VK_DOWN: // DOWN
			kbStatus.bitClr(KB_DOWN);
			break;

		case KeyEvent.VK_LEFT: // LEFT
			kbStatus.bitClr(KB_LEFT);
			break;

		case KeyEvent.VK_Z: // z
			kbStatus.bitClr(KB_Z);
			break;

		case KeyEvent.VK_D: // d
			kbStatus.bitClr(KB_D);
			break;

		case KeyEvent.VK_S: // s
			kbStatus.bitClr(KB_S);
			break;

		case KeyEvent.VK_Q: // q
			kbStatus.bitClr(KB_Q);
			break;

		case KeyEvent.VK_SPACE: // space
			kbStatus.bitClr(KB_SPACE);
			break;

		case KeyEvent.VK_ESCAPE:
			kbStatus.bitClr(KB_ESCAPE);
			break;

		default:
			break;
		}
	}

}
