package core;

import java.awt.*;
import javax.swing.*;

import gui.Canvas;

public abstract class Game {

    protected JFrame frame;
    protected Canvas canvas;

    public Game(JFrame frame, Canvas canvas) {
    	this.frame = frame;
    	this.canvas = canvas;
    }
    
    public JFrame getFrame() {
    	return this.frame;
    }
    
    public void setFrame(JFrame frame) {
    	this.frame = frame;
    }
    
    public Canvas getCanvas() {
    	return this.canvas;
    }
    
    public void setCanvas(Canvas canvas) {
    	this.canvas = canvas;
    }
    
    public abstract void run();
    
	public abstract void saveGame();
	
    public abstract void draw(Graphics g);
}