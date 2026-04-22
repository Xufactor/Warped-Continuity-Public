package core;

import java.awt.Graphics;

import gui.Canvas;
import other.Pair;

public abstract class Level {
	
    private Board board;
    private boolean isScreenMode;
    private boolean actionMusicPlaying;
    
    private boolean keyFound;
    private boolean clickOrbFound;
    private boolean normalGravity;
    
	public Level(Canvas canvas) {
		this.isScreenMode = true;
        this.normalGravity = true;
        buildLevel(canvas);
	}
	
	private void buildLevel(Canvas canvas) {
		int rows = getRows();
		int cols = getCols();
		Pair<Integer, Integer> blankScreenLocation = initialBlankScreenLocation(rows, cols);
		
		int canvasWidth = canvas.getWidth();
		int canvasHeight = canvas.getHeight();
		
		int screenSize = Math.min(canvasHeight / rows, canvasWidth / cols);
        int startX = canvasWidth / 2 - screenSize * cols / 2;
        int startY = canvasHeight / 2 - screenSize * rows / 2;
        
        board = new Board(rows, cols, blankScreenLocation, screenSize, startX, startY);
        
		board.setScreens(makeScreens(canvas));
        
        Pair<Integer, Integer> startScreenLocation = board.getStartScreenLocation();
      
        int playerX = initialPlayerX() + startScreenLocation.getY() * canvasWidth;
		int playerY = initialPlayerY() + startScreenLocation.getX() * canvasHeight;
        Player player = new Player(playerX, playerY, canvasWidth / 2, canvasHeight / 2, canvas);
		board.setPlayer(player);
		player.setLevel(this);
		//player.amCenterToSmCenter();
	}
	
	protected abstract int initialPlayerX();
	
	protected abstract int initialPlayerY();
	
	protected abstract int getRows();
	
	protected abstract int getCols();
	
	protected abstract Screen[][] makeScreens(Canvas canvas);
	
	protected Pair<Integer, Integer> initialBlankScreenLocation(int rows, int cols) {
		return new Pair<>(rows - 1, cols - 1);
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	public boolean isScreenMode() {
		return this.isScreenMode;
	}
	
	public boolean getIsScreenMode() {
		return this.isScreenMode;
	}
	
	public boolean getActionMusicPlaying() {
		return actionMusicPlaying;
	}
	
	public void setActionMusicPlaying(boolean b) {
		actionMusicPlaying = b;
	}
	
	public void setIsScreenMode(boolean isScreenMode) {
		this.isScreenMode = isScreenMode;
	}
	
	public boolean getKeyFound() {
		return this.keyFound;
	}
	
	public boolean getClickOrbFound() {
		return this.clickOrbFound;
	}
	
	public boolean getNormalGravity() {
		return this.normalGravity;
	}
	
	public void setNormalGravity(boolean normalGravity) {
		this.normalGravity = normalGravity;
	}
	
	public void foundKey() {
		keyFound = true;
	}
	
	public void foundClickOrb() {
		clickOrbFound = true;
	}
	
    public void resetStatuses() {
        keyFound = false;
        clickOrbFound = false;
        normalGravity = true;   
        board.getPlayer().fall();
    }
    
    public void drawActionMode(Graphics g, Canvas canvas) {
    	board.drawActionMode(g, canvas, keyFound, clickOrbFound);
    }
    
    public void drawScreenMode(Graphics g, Canvas canvas) {
    	board.drawScreenMode(g, canvas, keyFound, clickOrbFound);
    }
}