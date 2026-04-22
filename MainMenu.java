package core;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

import gui.Canvas;
import scene.DemoInfoScene;
import scene.HintsScene;
import scene.InstructionsScene;
import scene.LevelSelectScene;

public class MainMenu {

	private JPanel menuPanel;
	
	public MainMenu(WarpedContinuity game, Canvas canvas, boolean demo) {
		createMenuPanel(game, canvas, demo);
	}
	
	private void createMenuPanel(WarpedContinuity game, Canvas canvas, boolean demo) {
		menuPanel = new JPanel();
		int rows;
		if (demo) {
			rows = 4;
		}
		else {
			rows = 3;
		}
		menuPanel.setLayout(new GridLayout(rows, 1));
		canvas.add(menuPanel);
		menuPanel.setLocation(canvas.getWidth() * 3 / 8, canvas.getHeight() * 3 / 10);
		menuPanel.setSize(canvas.getWidth() / 4, canvas.getHeight() / 4);
		menuPanel.setVisible(false);
		addButtons(game, demo);
	}
	
	public void addButtons(WarpedContinuity game, boolean demo) {
		menuPanel.add(createPlayButton(game));
		menuPanel.add(createInstructionsButton(game));
		menuPanel.add(createHintsButton(game));
		if (demo) {
			menuPanel.add(createDemoInfoButton(game));
		}
	}
	
	private JButton createPlayButton(WarpedContinuity game) {
		JButton button = new JButton("Play");
		button.addActionListener((ActionEvent e) -> {
			game.setCurScene(new LevelSelectScene(game.isDemo(), game.getLevelSelectMenu())); 
			hideButtons();
		});		
		return button;
	}
	
	private JButton createInstructionsButton(WarpedContinuity game) {
		JButton button = new JButton("Instructions");
		button.addActionListener((ActionEvent e) -> {
			game.setCurScene(new InstructionsScene()); 
			hideButtons();
		});
		return button;
	}
	
	private JButton createHintsButton(WarpedContinuity game) {
		JButton button = new JButton("Hints");
		button.addActionListener((ActionEvent e) -> {
			game.setCurScene(new HintsScene());
			hideButtons();
		});
		return button;
	}
	
	private JButton createDemoInfoButton(WarpedContinuity game) {
		JButton button = new JButton("About Demo");
		button.addActionListener((ActionEvent e) -> {
			game.setCurScene(new DemoInfoScene());
			hideButtons();
		});
		return button;
	}
	
	public void showButtons() {
		menuPanel.setVisible(true);
	}
	
	public void hideButtons() {
		menuPanel.setVisible(false);
	}
}
