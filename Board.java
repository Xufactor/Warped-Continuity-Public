package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.Canvas;
import other.Draw;
import other.Pair;

public class Board {

	// dimensions of the screens, most of the levels are 2x2
    private final int rows;
    private final int cols;

    private Player player;
    
    private Screen[][] screens;
    private Pair<Integer, Integer> blankScreenLocation;
	// two screens are in each other's adjacency lists if they are adjacent and their borders are matching
    private Map<Screen, List<Screen>> adjLists;
	// number of times the screens have been shifted, used for scoring
    private int numOfScreenShifts;
    
	// length of the screens in screen mode
    private final int screenSize;
	// starting x and y coordinates for drawing the screens in screen mode
    private final int startX;
    private final int startY;
    
	// the screen that has a click block in it, if there is one
    private Screen clickBlockScreen;
    
    public Board(int rows, int cols, Pair<Integer, Integer> blankScreenLocation, int screenSize, int startX, int startY) {
        this.rows = rows;
        this.cols = cols;
 
        this.blankScreenLocation = blankScreenLocation;
		this.numOfScreenShifts = 0;
		
        this.screenSize = screenSize;
        this.startX = startX;
        this.startY = startY;
    }
    
    public int getRows() {
    	return this.rows;
    }
    
    public int getCols() {
    	return this.cols;
    }
    
	public Player getPlayer() {
		return this.player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
    
	public Screen[][] getScreens() {
		return this.screens;
	}
	
	public void setScreens(Screen[][] screens) {
		this.screens = screens;
		createAdjLists();
	}
	
	public Map<Screen, List<Screen>> getAdjLists() {
		return this.adjLists;
	}
	
	public int getNumOfScreenShifts() {
		return this.numOfScreenShifts;
	}
	
	public int getStartX() {
		return this.startX;
	}
	
	public int getStartY() {
		return this.startY;
	}
	
	public int getScreenSize() {
		return this.screenSize;
	}
	
	public Screen getClickBlockScreen() {
		return this.clickBlockScreen;
	}
	
	public void setClickBlockScreen(Screen clickBlockScreen) {
		this.clickBlockScreen = clickBlockScreen;
	}
	
	public Pair<Integer, Integer> getStartScreenLocation() {
		for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Screen screen = screens[row][col];
                if (screen.isStartScreen()) {
                	return new Pair<>(row, col);
                }
            }
        }
		return null;
	}
	
    public void createAdjLists() {
        adjLists = new HashMap<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Screen screen = screens[row][col];
                List<Screen> neighbors = new ArrayList<>();

                if (row != 0) {
                    Screen top = screens[row - 1][col];
                    if (Screen.isTopBottomFit(top, screen)) {
                        neighbors.add(top);
                    }
                }
                if (row != rows - 1) {
                    Screen bottom = screens[row + 1][col];
                    if (Screen.isTopBottomFit(screen, bottom)) {
                        neighbors.add(bottom);
                    }
                }
                if (col != 0) {
                    Screen left = screens[row][col - 1];
                    if (Screen.isLeftRightFit(left, screen)) {
                        neighbors.add(left);
                    }
                }
                if (col != cols - 1) {
                    Screen right = screens[row][col + 1];
                    if (Screen.isLeftRightFit(screen, right)) {
                        neighbors.add(right);
                    }
                }

                adjLists.put(screen, neighbors);
            }
        }
    }
    
    public String coordinateToCollision(Canvas canvas, int x, int y) {
    	int canvasWidth = canvas.getWidth();
    	int canvasHeight = canvas.getHeight();
   
    	if (x < 0) {
    		return "blocked";
    	}
    	else if (y < 0) {
    		boolean normalGravity = player.getLevel().getNormalGravity();
    		if (normalGravity) {
    			return "blocked";
    		}
    		else {
    			return "death";
    		}
    	}
    	int screenRow = y / canvasHeight;
    	int screenCol = x / canvasWidth;
    	if (screenRow >= rows) {
    		boolean normalGravity = player.getLevel().getNormalGravity();
    		if (normalGravity) {
    			return "death";
    		}
    		else {
    			return "blocked";
    		}
    	}
    	else if (screenCol >= cols) {
    		return "blocked";
    	}
    	else {
    		Screen screen = screens[screenRow][screenCol];
    		if (screen.isBlank()) {
    			return "blankscreen";
    		}
    		else {
    			int blockRow = (y - screenRow * canvasHeight) / (canvasHeight / Screen.ROWS);
            	int blockCol = (x - screenCol * canvasWidth) / (canvasWidth / Screen.COLS);
            	return Integer.toString(screen.getLabels()[blockRow][blockCol]);
    		}
    	}	
    }
    
	public void shiftScreenLeft(Canvas canvas) {
		int row = blankScreenLocation.getX();
		int col = blankScreenLocation.getY();
		if (col != cols - 1) {
			Screen temp = screens[row][col];
			screens[row][col] = screens[row][col + 1];
			screens[row][col + 1] = temp;
			blankScreenLocation = new Pair<>(row, col + 1);
			createAdjLists();
			shiftPlayer(canvas, row, col + 1, -screenSize, 0);
			numOfScreenShifts++;
			if (player.deathLocation != null) {
				player.deathLocation = null;
				resetScreens();
			}
		}
	}
	
	public void shiftScreenRight(Canvas canvas) {
		int row = blankScreenLocation.getX();
		int col = blankScreenLocation.getY();
		if (col != 0) {
			Screen temp = screens[row][col];
			screens[row][col] = screens[row][col - 1];
			screens[row][col - 1] = temp;
			blankScreenLocation = new Pair<>(row, col - 1);
			createAdjLists();
			shiftPlayer(canvas, row, col - 1, screenSize, 0);
			numOfScreenShifts++;
			if (player.deathLocation != null) {
				player.deathLocation = null;
				resetScreens();
			}
		}
	}
	public void shiftScreenDown(Canvas canvas) {
		int row = blankScreenLocation.getX();
		int col = blankScreenLocation.getY();
		if (row != 0) {
			Screen temp = screens[row][col];
			screens[row][col] = screens[row - 1][col];
			screens[row - 1][col] = temp;
			blankScreenLocation = new Pair<>(row - 1, col);
			createAdjLists();
			shiftPlayer(canvas, row - 1, col, 0, screenSize);
			numOfScreenShifts++;
			if (player.deathLocation != null) {
				player.deathLocation = null;
				resetScreens();
			}
		}
	}
	
	public void shiftScreenUp(Canvas canvas) {
		int row = blankScreenLocation.getX();
		int col = blankScreenLocation.getY();
		if (row != rows - 1) {
			Screen temp = screens[row][col];
			screens[row][col] = screens[row + 1][col];
			screens[row + 1][col] = temp;
			blankScreenLocation = new Pair<>(row + 1, col);
			createAdjLists();
			shiftPlayer(canvas, row + 1, col, 0, -screenSize);
			numOfScreenShifts++;
			if (player.deathLocation != null) {
				player.deathLocation = null;
				resetScreens();
			}
		}
	}
	
	private void shiftPlayer(Canvas canvas, int checkRow, int checkCol, int dx, int dy) {
		int actionX = player.getActionModeCenter().getX();
		int actionY = player.getActionModeCenter().getY();
		int playerRow = actionY / canvas.getHeight();
		int playerCol = actionX / canvas.getWidth();
		if (playerRow == checkRow && playerCol == checkCol) {
			int screenX = player.getScreenModeCenter().getX();
			int screenY = player.getScreenModeCenter().getY();
			player.setScreenModeCenter(new Pair<>(screenX + dx, screenY + dy));
		}
		player.smCenterToAmCenter();
	}
	
	public void resetScreens() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Screen screen = screens[i][j];
				if (!screen.isBlank()) {
					screen.resetLabels();
				}
			}
		}
		
        if (clickBlockScreen != null) {
        	clickBlockScreen.removeClickBlock();
        	clickBlockScreen.setClickBlockLocation(null);
        	clickBlockScreen = null;
        }
        
        createAdjLists();
	}
	
    public void drawScreenMode(Graphics g, Canvas canvas, boolean keyFound, boolean clickOrbFound) {
    	int blockSize = screenSize / Screen.ROWS;
    	for (int row = 0; row < rows; row++) {
    		for (int col = 0; col < cols; col++) {
    			Screen screen = screens[row][col];
    			screen.draw(g, row, col, screenSize, startX, startY, blockSize, keyFound, clickOrbFound);
    		}
    	}
    	// draw the borders between screens
    	drawBorders(g, screenSize, startX, startY, Math.max(rows, cols));
    	// draw the player
        player.drawScreenMode(g);
        
        Pair<Integer, Integer> deathLocation = player.deathLocation;
        if (deathLocation != null) {
        	Draw.drawX(g, deathLocation.getX(), deathLocation.getY(), blockSize, Color.BLACK);
        }
    }
    
    public void drawActionMode(Graphics g, Canvas canvas, boolean keyFound, boolean clickOrbFound) {
    	int canvasWidth = canvas.getWidth();
    	int canvasHeight = canvas.getHeight();
    	
    	int actionScreenSize = Math.min(canvasWidth, canvasHeight);
    	int blockSize = actionScreenSize / Screen.ROWS;
    	
    	int actionStartX = -player.getActionModeCenter().getX() + canvasWidth / 2;
    	int actionStartY = -player.getActionModeCenter().getY() + canvasHeight / 2;
    	
    	for (int row = 0; row < rows; row++) {
    		for (int col = 0; col < cols; col++) {
    			Screen screen = screens[row][col];
    				screen.draw(g, row, col, actionScreenSize, actionStartX, actionStartY, blockSize, keyFound, clickOrbFound);
    		}
    	}
    	// draw the borders between screens
    	drawBorders(g, actionScreenSize, actionStartX, actionStartY, 1);
    	// draw the player
    	player.drawActionMode(g);
    }
    
	private void drawBorders(Graphics g, int screenSize, int startX, int startY, int multiplier) {
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(Math.round(8.0 / multiplier)));
        for (int i = 0; i <= rows; i++) {
			g2.drawLine(startX, startY + i * screenSize, startX + cols * screenSize, startY + i * screenSize);
        }
        for (int j = 0; j <= cols; j++) {
        	g2.drawLine(startX + j * screenSize, startY, startX + j * screenSize, startY + rows * screenSize);
        }
	}
}