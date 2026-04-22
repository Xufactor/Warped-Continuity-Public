package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gui.Canvas;
import other.Pair;

public class Player {

	private static final int HEAD_RADIUS = 8;
	
	private Level level;
	
	// ratio of the player's size in action mode to its size in screen mode
	private int sizeMultiplier;
	private Pair<Integer, Integer> screenModeCenter;
	private Pair<Integer, Integer> actionModeCenter;
	private final Pair<Integer, Integer> canvasCenter;
	
	private final int blockSize; 
	
	// boundary points of the player
	private int leftX;
	private int rightX;
	private int topY;
	private int bottomY;
	
	private boolean isOnGround;
	// y velocity
	private int dy;
	// y acceleration
	private int ddy;
	
	private String stance;
	// player changes stance when it is blocked from the left or right
	
	public Pair<Integer, Integer> deathLocation;
	
	public Player(int x, int y, int canvasCenterX, int canvasCenterY, Canvas canvas) {
		//this.screenModeCenter = new Pair(x, y);
		this.actionModeCenter = new Pair<>(x, y);
		this.canvasCenter = new Pair<>(canvasCenterX, canvasCenterY); 
		
		this.blockSize = Math.min(canvas.getWidth(), canvas.getHeight())/ Screen.ROWS;
		
		this.isOnGround = false;
		this.dy = 10;
		this.ddy = 0;
		this.stance = "center";
	}
	
	public void setLevel(Level level) {
		this.level = level;
		Board board = level.getBoard();
		this.sizeMultiplier = Math.max(board.getRows(), board.getCols());
		amCenterToSmCenter();
	}
	
	public Level getLevel() {
		return this.level;
	}
	
	public int getSizeMultiplier() {
		return this.sizeMultiplier;
	}
	
	public Pair<Integer, Integer> getScreenModeCenter() {
		return this.screenModeCenter;
	}
	
	public void setScreenModeCenter(Pair<Integer, Integer> screenModeCenter) {
		this.screenModeCenter = screenModeCenter;
	}
	
	public Pair<Integer, Integer> getActionModeCenter() {
		return this.actionModeCenter;
	}
	
	public void setActionModeCenter(Pair<Integer, Integer> actionModeCenter) {
		this.actionModeCenter = actionModeCenter;
	}
	
	public boolean getIsOnGround() {
		return this.isOnGround;
	}
	
	public void setIsOnGround(boolean isOnGround) {
		this.isOnGround = isOnGround;
	}
	public int getDY() {
		return this.dy;
	}
	
	public void setDY(int dy) {
		this.dy = dy;
	}
	
	public int getDDY() {
		return this.ddy;
	}
	
	public void setDDY(int ddy) {
		this.ddy = ddy;
	}
	
	public void setStance(String stance) {
		this.stance = stance;
	}
	
	public void smCenterToAmCenter() {
		Board board = level.getBoard();
		int x = screenModeCenter.getX();
		int y = screenModeCenter.getY();
		actionModeCenter = new Pair<>(sizeMultiplier * (x - board.getStartX()), sizeMultiplier * (y - board.getStartY()));
		updateBoundaryBox();
	}
	
	public void amCenterToSmCenter() {
		Board board = level.getBoard();
		int x = actionModeCenter.getX();
		int y = actionModeCenter.getY();
		screenModeCenter = new Pair<>(x / sizeMultiplier + board.getStartX(), y / sizeMultiplier + board.getStartY());
		//updateBoundaryBox();
	}
	
	public void updateBoundaryBox() {
		int x = actionModeCenter.getX();
		int y = actionModeCenter.getY();
		leftX = x - 8;
		rightX = x + 8;
		if (level.getNormalGravity()) {
			topY = y - HEAD_RADIUS;
			bottomY = y + HEAD_RADIUS + 32;
		}
		else {
			topY = y - HEAD_RADIUS - 32;
			bottomY = y + HEAD_RADIUS;
		}
	}
	
	public void flip() {
		int x = actionModeCenter.getX();
		int y = actionModeCenter.getY();
		if (level.getNormalGravity()) {
			actionModeCenter = new Pair<>(x, y - 32);
			updateBoundaryBox();
		}
		else {
			actionModeCenter = new Pair<>(x, y + 32);
			updateBoundaryBox();
		}
	}

	public void moveLeft() {
		int x = actionModeCenter.getX();
		int y = actionModeCenter.getY();
		actionModeCenter = new Pair<>(x - 4, y);
		updateBoundaryBox();
	}
	
	public void moveRight() {
		int x = actionModeCenter.getX();
		int y = actionModeCenter.getY();
		actionModeCenter = new Pair<>(x + 4, y);
		updateBoundaryBox();
	}
	
	public void moveBackLeft() {
		int x = actionModeCenter.getX();
		int y = actionModeCenter.getY();
		actionModeCenter = new Pair<>(rightX / blockSize * blockSize - 1 - rightX + x, y);
		updateBoundaryBox();
	}
	
	public void moveBackRight() {
		int x = actionModeCenter.getX();
		int y = actionModeCenter.getY();
		//actionModeCenter = new Pair<>((int) Math.floor((float) leftX / blockSize) * blockSize + blockSize + x - leftX, y);
		actionModeCenter = new Pair<>((int) Math.floor((float) leftX / blockSize) * blockSize + blockSize + x - leftX + 1, y);
		//actionModeCenter = new Pair<>(leftX / blockSize * blockSize + blockSize + x - leftX + 1, y);
		updateBoundaryBox();
	}
	
	public Set<String> moveVertically(Canvas canvas) {
		int x = actionModeCenter.getX();
		int y = actionModeCenter.getY();
		
		// new location
		actionModeCenter = new Pair<>(x, y + dy);
		updateBoundaryBox();
		
		Set<String> collisions = detectCollisions(canvas);
		boolean normalGravity = level.getNormalGravity();
		// check if between screens
		if (isBetweenScreens(canvas) && !screensMatch(canvas)) {
			// fell to death
			if ((normalGravity && dy > 0) || (!normalGravity && dy < 0)) {
				collisions.add("death");
				return collisions;
			}
			// hit screen above
			else {
				actionModeCenter = new Pair<>(x, y);
				updateBoundaryBox();
				fall();
			}
		}
		// hit something
		else if (collisions.contains("blocked") || collisions.contains("-1") || collisions.contains("-2") || collisions.contains("-3") ||
				collisions.contains("blankscreen")) {
			// landed
			if ((normalGravity && dy > 0) || (!normalGravity && dy < 0)) {
				if (collisions.contains(("blankscreen"))) {
					collisions.add("death");
					return collisions;
				}
				int newY;
				int blockHeight = canvas.getHeight() / Screen.ROWS;
				if (normalGravity) {
					//newY = (y + dy) / 10 * 10 - 1;
					newY = ((y + dy + 40) / blockHeight) * blockHeight - 41;
				}
				else {
					//newY = (y + dy + 10) / 10 * 10 + 1;
					newY = (y + dy - 41) / blockHeight * blockHeight + blockHeight + 41;
				}
				actionModeCenter = new Pair<>(x, newY);
				updateBoundaryBox();
				land();	
			}
			// hit something above
			else {
				actionModeCenter = new Pair<>(x, y);
				updateBoundaryBox();
				fall();
			}
		}
		// continue to move vertically
		else {
			isOnGround = false;
			if (normalGravity) {
				dy = Math.min(10, dy + ddy);
			}
			else {
				dy = Math.max(-10, dy + ddy);
			}
		}
		return collisions;
	}
	
	public void jump() {
		isOnGround = false;
		if (level.getNormalGravity()) {
			dy = -20;
			ddy = 1;
		}
		else {
			dy = 20;
			ddy = -1;
		}
	}
	
	public void fall() {
		isOnGround = false;
		if (level.getNormalGravity()) {
			dy = 10;
		}
		else {
			dy = -10;
		}
		ddy = 0;
	}
	
	public void land() {
		isOnGround = true;
		if (level.getNormalGravity()) {
			dy = 10;
		}
		else {
			dy = -10;
		}
		ddy = 0;
	}

	public Set<String> detectCollisions(Canvas canvas) {
		Board board = level.getBoard();
	    String l1 = board.coordinateToCollision(canvas, leftX, topY);
	    String l2 = board.coordinateToCollision(canvas, leftX, bottomY);
	    String l3 = board.coordinateToCollision(canvas, rightX, topY);
	    String l4 = board.coordinateToCollision(canvas, rightX, bottomY);
	    
	    Set<String> collisionSet = new HashSet<>(Arrays.asList(new String[] {l1, l2, l3, l4}));
	    return collisionSet;
	}
	
	
	public boolean isBetweenScreens(Canvas canvas) {
		// uses action mode coordinates
		int canvasWidth = canvas.getWidth();
    	int canvasHeight = canvas.getHeight();
    	return !(topY / canvasHeight == bottomY / canvasHeight && 
    			leftX / canvasWidth == rightX / canvasWidth);	
	}
	
	public boolean screensMatch(Canvas canvas) {
		// uses action mode coordinates
		int canvasWidth = canvas.getWidth();
    	int canvasHeight = canvas.getHeight();
    	Board board = level.getBoard();
    	
    	if (topY < 0 || bottomY >= canvasHeight * board.getRows()) {
    		return false;
    	}
    	if (leftX < 0 || rightX >= canvasWidth * board.getCols()) {
    		return false;
    	}
    	
    	Screen[][] screens = board.getScreens();
    	Map<Screen, List<Screen>> adjLists = board.getAdjLists();
    	
    	int screenRow1 = topY / canvasHeight;
    	int screenCol1 = leftX / canvasWidth;
    	Screen screen1 = screens[screenRow1][screenCol1];
    	
    	int screenRow2 = topY / canvasHeight;
    	int screenCol2 = rightX / canvasWidth;
    	Screen screen2 = screens[screenRow2][screenCol2];
    	if (!screen1.equals(screen2) && !adjLists.get(screen1).contains(screen2)) {
    		return false;
    	}
    	
    	int screenRow3 = bottomY / canvasHeight;
    	int screenCol3 = rightX / canvasWidth;
    	Screen screen3 = screens[screenRow3][screenCol3];
    	if (!screen2.equals(screen3) && !adjLists.get(screen2).contains(screen3)) {
    		return false;
    	}
   
    	int screenRow4 = bottomY / canvasHeight;
    	int screenCol4 = leftX / canvasWidth;
    	Screen screen4 = screens[screenRow4][screenCol4];
    	if (!screen3.equals(screen4) && !adjLists.get(screen3).contains(screen4)) {
    		return false;
    	}
    	if (!screen4.equals(screen1) && !adjLists.get(screen4).contains(screen1)) {
    		return false;
    	}
    	
    	return true;
	}
	
	public void die(Canvas canvas) {
		Board board = level.getBoard();
		amCenterToSmCenter();
		deathLocation = new Pair<>(screenModeCenter.getX(), screenModeCenter.getY());
		level.resetStatuses();
		
		Pair<Integer, Integer> startScreenLocation = board.getStartScreenLocation();
	      
        int playerX = level.initialPlayerX() + startScreenLocation.getY() * canvas.getWidth();
		int playerY = level.initialPlayerY() + startScreenLocation.getX() * canvas.getHeight();
		actionModeCenter = new Pair<>(playerX, playerY);
		amCenterToSmCenter();
		updateBoundaryBox();
	}
	
	public void drawActionMode(Graphics g) {
		int headX = canvasCenter.getX();
		int headY = canvasCenter.getY();
		if (level.getNormalGravity()) {
			drawPlayerNormal(g, headX, headY, stance, 1);
		}
		else {
			drawPlayerReverse(g, headX, headY, stance, 1);
		}
	}
	
	public void drawScreenMode(Graphics g) {
		int headX = screenModeCenter.getX();
		int headY = screenModeCenter.getY();
		if (level.getNormalGravity()) {
			drawPlayerNormal(g, headX, headY, stance, sizeMultiplier);
		}
		else {
			drawPlayerReverse(g, headX, headY, stance, sizeMultiplier);
		}
	}
	
	public void drawPlayerNormal(Graphics g, int headX, int headY, String stance, int sizeMultiplier) {
		g.setColor(Color.BLACK);
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3 / sizeMultiplier));
        
		// head
        int radius = HEAD_RADIUS / sizeMultiplier;
		g2.fillOval(headX - radius, headY - radius, 2 * radius, 2 * radius);
		// body
        g2.drawLine(headX, headY + radius, headX, headY + radius + 16 / sizeMultiplier);
        // legs
        g2.drawLine(headX, headY + radius + 16 / sizeMultiplier, headX + 8 / sizeMultiplier, headY + radius + 32 / sizeMultiplier);
        g2.drawLine(headX, headY + radius + 16 / sizeMultiplier, headX - 8 / sizeMultiplier, headY + radius + 32 / sizeMultiplier);
		// arms
		switch (stance) {
			case "center" -> {
                g2.drawLine(headX - 8 / sizeMultiplier, headY + radius + 14 / sizeMultiplier, headX, headY + radius + 2 / sizeMultiplier);
                g2.drawLine(headX + 8 / sizeMultiplier, headY + radius + 14 / sizeMultiplier, headX, headY + radius + 2 / sizeMultiplier);
            }
			case "left" -> {
                g2.drawLine(headX - 8 / sizeMultiplier, headY + radius + 2 / sizeMultiplier, headX, headY + radius + 2 / sizeMultiplier);
                g2.drawLine(headX - 8 / sizeMultiplier, headY + radius + 14 / sizeMultiplier, headX, headY + radius + 2 / sizeMultiplier);
            }
			case "right" -> {
                g2.drawLine(headX + 8 / sizeMultiplier, headY + radius + 2 / sizeMultiplier, headX, headY + radius + 2 / sizeMultiplier);
                g2.drawLine(headX + 8 / sizeMultiplier, headY + radius + 14 / sizeMultiplier, headX, headY + radius + 2 / sizeMultiplier);
            }
			default -> {}
		}
	}
	
	private void drawPlayerReverse(Graphics g, int headX, int headY, String stance, int sizeMultiplier) {
		g.setColor(Color.BLACK);
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3 / sizeMultiplier));
        
		// head
        int radius = HEAD_RADIUS / sizeMultiplier;
		g2.fillOval(headX - radius, headY - radius, 2 * radius, 2 * radius);
		// body
        g2.drawLine(headX, headY - radius, headX, headY - radius - 16 / sizeMultiplier);
        // legs
        g2.drawLine(headX, headY - radius - 16 / sizeMultiplier, headX + 8 / sizeMultiplier, headY - radius - 32 / sizeMultiplier);
        g2.drawLine(headX, headY - radius - 16 / sizeMultiplier, headX - 8 / sizeMultiplier, headY - radius - 32 / sizeMultiplier);
		// arms
		switch (stance) {
			case "center" -> {
                g2.drawLine(headX - 8 / sizeMultiplier, headY - radius - 14 / sizeMultiplier, headX, headY - radius - 2 / sizeMultiplier);
            	g2.drawLine(headX + 8 / sizeMultiplier, headY - radius - 14 / sizeMultiplier, headX, headY - radius - 2 / sizeMultiplier);
            }
			case "left" -> {
                g2.drawLine(headX - 8 / sizeMultiplier, headY - radius - 2 / sizeMultiplier, headX, headY - radius - 2 / sizeMultiplier);
                g2.drawLine(headX - 8 / sizeMultiplier, headY - radius - 14 / sizeMultiplier, headX, headY - radius - 2 / sizeMultiplier);
            }
			case "right" -> {
                g2.drawLine(headX + 8 / sizeMultiplier, headY - radius - 2 / sizeMultiplier, headX, headY - radius - 2 / sizeMultiplier);
                g2.drawLine(headX + 8 / sizeMultiplier, headY - radius - 14 / sizeMultiplier, headX, headY - radius - 2 / sizeMultiplier);
            }
			default -> {}
		}
		//g2.drawLine(headX - 12 / sizeMultiplier, headY - radius - 16 / sizeMultiplier, headX, headY - radius);
        //g2.drawLine(headX + 12 / sizeMultiplier, headY - radius - 16 / sizeMultiplier, headX, headY - radius);
	}
}
