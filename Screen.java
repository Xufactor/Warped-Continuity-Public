package core;

import java.awt.Color;
import java.awt.Graphics;

import gui.Canvas;
import other.Pair;
import things.ClickOrb;
import things.Door;
import things.Key;
import things.NormalGravityWell;
import things.ReverseGravityWell;

public class Screen
{
    /*
        [0] = new EmptyBlock(),
        [-1] = new WallBlock(),
        [-2] = new ToggleWallBlock(),
        [-3] = new VanishingWallBlock(),
        [1] = new DoorBlock(),
        [2] = new KeyBlock(),
        [3] = new normalGravityBlock(),
        [4] = new reverseGravityBlock(),
    */

    public static final int ROWS = 10;
    public static final int COLS = 10;

    private final boolean isBlank;
    private final boolean isStartScreen;
    private int[][] defaultLabels;
    private int[][] labels;

    private Pair<Integer, Integer> clickBlockLocation;
    
	public Screen() {
		this.isBlank = true;
        this.labels = null;
        this.isStartScreen = false;
	}
	
	public Screen(int[][] labels) {
		this.isBlank = false;
		this.defaultLabels = copyLabels(labels);
        this.labels = copyLabels(labels);
        this.isStartScreen = false;
	}
	
	public Screen(int[][] labels, boolean isStartScreen) {
		this.isBlank = false;
		this.defaultLabels = copyLabels(labels);
        this.labels = copyLabels(labels);
        this.isStartScreen = true;
	}
	
    public int[][] getLabels() {
        return this.labels;
    }
    
    public boolean isBlank() {
    	return this.isBlank;
    }

    public boolean isStartScreen() {
    	return this.isStartScreen;
    }
    
    public Pair<Integer, Integer> getClickBlockLocation() {
    	return this.clickBlockLocation;
    }
    
	public void setClickBlockLocation(Pair<Integer, Integer> clickBlockLocation) {
		this.clickBlockLocation = clickBlockLocation;
	}
    
	public boolean createClickBlock(Player player, Canvas canvas, int blockRow, int blockCol) {
		if (labels[blockRow][blockCol] == 0 && labels[blockRow][blockCol + 1] == 0 &&
				labels[blockRow + 1][blockCol] == 0 && labels[blockRow + 1][blockCol + 1] == 0) {
			labels[blockRow][blockCol] = -2;
			labels[blockRow][blockCol + 1] = -2;
			labels[blockRow + 1][blockCol] = -2;
			labels[blockRow + 1][blockCol + 1] = -2;
			if (player.detectCollisions(canvas).contains("-2")) {
				labels[blockRow][blockCol] = 0;
				labels[blockRow][blockCol + 1] = 0;
				labels[blockRow + 1][blockCol] = 0;
				labels[blockRow + 1][blockCol + 1] = 0;
				return false;
			}
			return true;
		}
		return false;
	}
	
	public void attemptToRemoveBlock(int blockRow, int blockCol) { 
		if (labels[blockRow][blockCol] == -3 && labels[blockRow][blockCol + 1] == -3 &&
				labels[blockRow + 1][blockCol] == -3 && labels[blockRow + 1][blockCol + 1] == -3) {
			labels[blockRow][blockCol] = 0;
			labels[blockRow][blockCol + 1] = 0;
			labels[blockRow + 1][blockCol] = 0;
			labels[blockRow + 1][blockCol + 1] = 0;
		}
	}
	
	public void removeClickBlock() {
		int blockRow = clickBlockLocation.getX();
		int blockCol = clickBlockLocation.getY();
		labels[blockRow][blockCol] = 0;
		labels[blockRow][blockCol + 1] = 0;
		labels[blockRow + 1][blockCol] = 0;
		labels[blockRow + 1][blockCol + 1] = 0;
	}
	
    public static boolean isTopBottomFit(Screen top, Screen bottom) {
        if (top.isBlank() || bottom.isBlank()) {
            return false;
        }
        int[][] topLabels = top.getLabels();
        int[][] bottomLabels = bottom.getLabels();
        for (int col = 0; col < COLS; col++) {
            int topLabel = topLabels[ROWS - 1][col];
            int bottomLabel = bottomLabels[0][col];
            if ((topLabel < 0 && bottomLabel >= 0) || (topLabel >= 0 && bottomLabel < 0)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLeftRightFit(Screen left, Screen right) {
        if (left.isBlank() || right.isBlank()) {
            return false;
        }
        int[][] leftLabels = left.getLabels();
        int[][] rightLabels = right.getLabels();
        for (int row = 0; row < ROWS; row++) {
            int leftLabel = leftLabels[row][COLS - 1];
            int rightLabel = rightLabels[row][0];
            if ((leftLabel < 0 && rightLabel >= 0) || (leftLabel >= 0 && rightLabel < 0)) {
                return false;
            }
        }
        return true;
    }
    
    private int[][] copyLabels(int[][] labels) {
    	int[][] newLabels = new int[ROWS][COLS];
    	for (int i = 0; i < ROWS; i++) {
            System.arraycopy(labels[i], 0, newLabels[i], 0, COLS);
    	}
    	return newLabels;
    }
    
    public void resetLabels() {
    	labels = copyLabels(defaultLabels);
    }

	public void draw(Graphics g, int row, int col, int screenSize, int startX, int startY, int blockSize, boolean keyFound, boolean clickOrbFound) {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				int label = labels[i][j];
				switch (label) {
					case -1 -> {
						g.setColor(Color.BLACK);
						g.fillRect(startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, blockSize, blockSize);
					}
					case -2 -> {
						g.setColor(Color.BLUE);
						g.fillRect(startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, blockSize, blockSize);
					}
					case 1 -> Door.draw(g, startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, blockSize);
					case 2 -> Key.draw(g, keyFound, startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, blockSize);
					case 3 -> NormalGravityWell.draw(g, startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, blockSize);
					case 4 -> ReverseGravityWell.draw(g, startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, blockSize);
					case 5 -> ClickOrb.draw(g, clickOrbFound, startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, blockSize);
					default -> {}
				}
			}
		}
		for (int i = 0; i < ROWS; i += 2) {
			for (int j = 0; j < COLS; j += 2) {
				int label = labels[i][j];
				if (label == -3) {
					Color goldenrod = new Color(218 ,165 ,32);
					g.setColor(goldenrod);
					g.fillRect(startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, 2 * blockSize, 2 * blockSize);
				
					g.setColor(Color.BLACK);
					g.drawRect(startX + col * screenSize + j * blockSize, startY + row * screenSize + i * blockSize, 2 * blockSize, 2 * blockSize);
				}
			}
		}
	}
}