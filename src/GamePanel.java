import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 30;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 140;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten = 0;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Button continueButton;
	Button exitButton;

	GamePanel() {
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}

	public void startGame() {
		drawNewApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		if (running) {
			Toolkit t = Toolkit.getDefaultToolkit();
			Image apple = t.getImage("src/img/apple.png");
			g.drawImage(apple, appleX, appleY, UNIT_SIZE, UNIT_SIZE, this);

			for (int i = 0; i < bodyParts; i++) {
				g.setColor(new Color(50, 220, 50));
				g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);
			}
			drawScoreBoard(g);
		} else
			drawGameOverScreen(g);
	}

	public void drawScoreBoard(Graphics g) {
		g.setColor(Color.red);
		g.setFont(new Font("Elephant", Font.PLAIN, 20));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, 40);
	}

	public void drawNewApple() {
		// draw apple in random cell
		Random random = new Random();
		appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
	}

	public void moveSnake() {
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		switch (direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}

	public void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			drawNewApple();
		}
	}

	public void checkCollisions() {
		// check if head collides with body
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0]) == y[i]) {
				running = false;
			}
		}
		// check if head touches left screen border
		if (x[0] < 0) {
			running = false;
		}

		// check if head touches right screen border
		if (x[0] > SCREEN_WIDTH) {
			running = false;
		}

		// check if head touches top screen border
		if (y[0] < 0) {
			running = false;
		}

		// check if head touches bottom screen border
		if (y[0] > SCREEN_HEIGHT) {
			running = false;
		}

		if (!running)
			timer.stop();
	}

	public void drawGameOverScreen(Graphics g) {
		g.setColor(Color.red);
		g.setFont(new Font("Elephant", Font.BOLD, SCREEN_WIDTH / 10));
		// position text in the middle of the screen
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("GAME OVER", (SCREEN_WIDTH - metrics.stringWidth("GAME OVER")) / 2, SCREEN_HEIGHT / 2);
		drawScoreBoard(g);
		continueButton = new Button("Continue");
		this.add(continueButton);
		continueButton.setFont(new Font("Elephant", Font.PLAIN, SCREEN_WIDTH / 20));
		continueButton.setBounds(50, SCREEN_HEIGHT / 2 + 100, 200, 50);
		continueButton.setVisible(true);
		continueButton.addActionListener(this);
		exitButton = new Button("Exit");
		this.add(exitButton);
		exitButton.setFont(new Font("Elephant", Font.PLAIN, SCREEN_WIDTH / 20));
		exitButton.setBounds(SCREEN_WIDTH - 250, SCREEN_HEIGHT / 2 + 100, 200, 50);
		exitButton.setVisible(true);
		exitButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			moveSnake();
			checkApple();
			checkCollisions();
		}
		if (e.getSource() == continueButton) {
			new GameFrame();
		}
		if (e.getSource() == exitButton) {
			System.exit(0);
		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {

			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}
	}
}
