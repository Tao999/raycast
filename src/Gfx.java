import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Gfx extends JPanel {
	public static final String TEXTURE_PATH = "gfx/texture.png";
	public static final String GROUND_PATH = "gfx/sol.png";
	public static final int TILE_SIZE = 32;

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final double FOV = Math.PI / 180 * 90;

	private BufferedImage buf;
	private BufferedImage texture;

	Gfx() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		buf = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		try {
			texture = ImageIO.read(new File(TEXTURE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void blastMap(int[][] map, Player p) {
		Graphics g = buf.getGraphics();
		// dessin de l'air
		g.setColor(new Color(25, 25, 25));
		g.fillRect(0, 0, WIDTH, HEIGHT / 2);
		g.setColor(new Color(75, 75, 75));
		g.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT);
		// raycasting
		for (int i = 0; i < WIDTH; i++) {
			double angleRay = (i - WIDTH / 2) * (FOV / WIDTH);
			// double cameraX = 2 * p.position.x / (double) WIDTH - 1;
			double rayDirX = Math.cos(p.angle + angleRay) + Math.cos(p.angle);
			double rayDirY = Math.sin(p.angle + angleRay) + Math.sin(p.angle);
			// Map position
			int mapX = p.position.x.intValue();
			int mapY = p.position.y.intValue();
			// length of ray from current position to next x or y-side
			double sideDistX;
			double sideDistY;
			// Length of ray from one side to next in map
			double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
			double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
			double perpWallDist;
			// Direction to go in x and y
			int stepX, stepY;
			boolean hit = false;// was a wall hit
			int side = 0;// was the wall vertical or horizontal
			// Figure out the step direction and initial distance to a side
			if (rayDirX < 0) {
				stepX = -1;
				sideDistX = (p.position.x - mapX) * deltaDistX;
			} else {
				stepX = 1;
				sideDistX = (mapX + 1.0 - p.position.x) * deltaDistX;
			}
			if (rayDirY < 0) {
				stepY = -1;
				sideDistY = (p.position.y - mapY) * deltaDistY;
			} else {
				stepY = 1;
				sideDistY = (mapY + 1.0 - p.position.y) * deltaDistY;
			}
			// Loop to find where the ray hits a wall
			while (!hit) {
				// Jump to next square
				if (sideDistX < sideDistY) {
					sideDistX += deltaDistX;
					mapX += stepX;
					side = 0;
				} else {
					sideDistY += deltaDistY;
					mapY += stepY;
					side = 1;
				}
				// Check if ray has hit a wall
				if (map[mapX / App.BLOC_SIZE][mapY / App.BLOC_SIZE] > 0)
					hit = true;
			}
			// Calculate distance to the point of impact
			if (side == 0)
				perpWallDist = Math.abs((mapX - p.position.x + (1 - stepX) / 2) / rayDirX);
			else
				perpWallDist = Math.abs((mapY - p.position.y + (1 - stepY) / 2) / rayDirY);
			// Now calculate the height of the wall based on the distance from the camera
			int lineHeight;
			if (perpWallDist > 0) {
				lineHeight = Math.abs((int) (HEIGHT / (perpWallDist * Math.cos(angleRay / Math.sqrt(2)))));
			} else
				lineHeight = HEIGHT;
			// calculate lowest and highest pixel to fill in current stripe
			int drawStart = -lineHeight / 2 + HEIGHT / 2;

			double wallX;// Exact position of where wall was hit
			int mult;
			if (side == 1) {// If its a y-axis wall
				if (p.position.y > mapY)
					mult = -1;
				else
					mult = 1;
				wallX = TILE_SIZE
						- mult * (p.position.x + ((mapY - p.position.y + (1 - stepY) / 2) / rayDirY) * rayDirX);
			} else {// X-axis wall
				if (p.position.x > mapX)
					mult = 1;
				else
					mult = -1;
				wallX = TILE_SIZE
						- mult * (p.position.y + ((mapX - p.position.x + (1 - stepX) / 2) / rayDirX) * rayDirY);
			}
			Point<Integer> pTexture = getPointTexture(map[mapX / App.BLOC_SIZE][mapY / App.BLOC_SIZE]);
			BufferedImage dstImage = new BufferedImage(1, TILE_SIZE, BufferedImage.TYPE_INT_RGB);
			dstImage.getGraphics().drawImage(
					texture.getSubimage((int) ((wallX * TILE_SIZE) % TILE_SIZE + pTexture.x), pTexture.y, 1, TILE_SIZE),
					0, 0, this);

			float darkMultiplicateur = (float) (perpWallDist*-0.07+1);
			if (darkMultiplicateur < 0)
				darkMultiplicateur=0;
			RescaleOp op = new RescaleOp(darkMultiplicateur, 0.0f, null);// éclaircir de 10%
			g.drawImage(op.filter(dstImage, null), i, drawStart, 1, lineHeight, this);

			// dessin du crosshair
			g.setColor(new Color(255, 255, 255));
			g.drawOval(WIDTH / 2 - 2, HEIGHT / 2 - 2, 4, 4);
		}
		// call blast
		paint(getGraphics());
	}

	public Point<Integer> getPointTexture(int id) {
		Point<Integer> p = new Point<Integer>(0, 0);
		switch (id) {
		case 1:
			p.x = 0;
			p.x = 0;
			break;
		case 2:
			p.x = 32;
			p.y = 0;
			break;
		case 3:
			p.x = 64;
			p.y = 0;
			break;
		case 4:
			p.x = 96;
			p.y = 0;
			break;

		default:
			p.x = 0;
			p.x = 0;
			break;
		}
		return p;
	}

	public void paint(Graphics g) {
		// blast
		g.drawImage(buf, 0, 0, this);
	}
}
