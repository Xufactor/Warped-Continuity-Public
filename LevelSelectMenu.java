package core;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicArrowButton;

import gui.Canvas;
import scene.LevelScene;

public class LevelSelectMenu {

	private static final int LEVELS_PER_PANEL = 25;
	private static final int PANEL_COLS = 5;

	private final WarpedContinuity game;
	private final Canvas canvas;
	private final SaveData saveData;
	
	private JPanel[] levelPanels;
	private JPanel lastPageLastLevelPanel;
	private int page;
	private JButton[] levelButtons;
	private JButton leftArrowButton;
	private JButton rightArrowButton;
	private JButton deleteSaveDataButton;
	
	public LevelSelectMenu(WarpedContinuity game, Canvas canvas, boolean demo) {
		this.game = game;
		this.canvas = canvas;
		this.saveData = game.getSaveData();
		
		this.levelButtons = new JButton[WarpedContinuity.NUM_OF_LEVELS + 1];
		
		if (demo) {
			createDemoLevelPanel();
			//this.rows = 1;
		}
		else {
			createLevelPanels();
			//this.rows = (WarpedContinuity.NUM_OF_LEVELS - 1) / cols + 1;
		}
		
		this.page = 0;
		createLeftArrowButton();
		createRightArrowButton();
		
		createDeleteSaveDataButton();
		
		//this.completions = new boolean[WarpedContinuity.NUM_OF_LEVELS + 1];
	}
	
	public SaveData getSaveData() {
		return saveData;
	}
	
	public JButton[] getLevelButtons() {
		return levelButtons;
	}

	public void setLevelButtons(JButton[] levelButtons) {
		this.levelButtons = levelButtons;
	}
	
	private void createLevelPanels() {
		levelPanels = new JPanel[(WarpedContinuity.NUM_OF_LEVELS - 1) / LEVELS_PER_PANEL + 1];
		int start, end;
		for (int panelNum = 0; panelNum < levelPanels.length - 1; panelNum++) {
			start = panelNum * LEVELS_PER_PANEL + 1;
			end = (panelNum + 1) * LEVELS_PER_PANEL;
			//end = Math.min((panelNum + 1) * LEVELS_PER_PANEL, highest);
			//JPanel levelPanel = createLevelPanel(end - start + 1);
			JPanel levelPanel = createLevelPanel(LEVELS_PER_PANEL);
			levelPanels[panelNum] = levelPanel;
			createLevelButtons(levelPanel, start, end);
		}
		int highest = WarpedContinuity.NUM_OF_LEVELS - WarpedContinuity.NUM_OF_LEVELS % PANEL_COLS;
		start = (levelPanels.length - 1) * LEVELS_PER_PANEL + 1;
		end = Math.min(levelPanels.length * LEVELS_PER_PANEL, highest);
		JPanel lastPageFirstLevelPanel = createLevelPanel(end - start + 1);
		levelPanels[levelPanels.length - 1] = lastPageFirstLevelPanel;
		createLevelButtons(lastPageFirstLevelPanel, start, end);
		
		lastPageLastLevelPanel = createLastPageLastLevelPanel();
		createLevelButtons(lastPageLastLevelPanel, highest + 1, WarpedContinuity.NUM_OF_LEVELS);
	}
	
	private void createDemoLevelPanel() {
		levelPanels = new JPanel[1];
		JPanel levelPanel = createLevelPanel(5);
		levelPanels[0] = levelPanel;
		createDemoLevelButtons(levelPanel);
	}
	
	private JPanel createLevelPanel(int numLevels) {
		JPanel levelPanel = new JPanel();
		int rows = (numLevels - 1) / PANEL_COLS + 1;
		levelPanel.setLayout(new GridLayout(rows, PANEL_COLS));
		
		canvas.add(levelPanel);
		levelPanel.setLocation(canvas.getWidth() / 8, canvas.getHeight() / 4);
		levelPanel.setSize(canvas.getWidth() * 3 / 4, canvas.getHeight() * rows / 16);
		levelPanel.setVisible(false);
		
		return levelPanel;
	}
	
	private JPanel createLastPageLastLevelPanel() {
		JPanel levelPanel = new JPanel();
		
		int numLevels = WarpedContinuity.NUM_OF_LEVELS % PANEL_COLS;
		levelPanel.setLayout(new GridLayout(1, numLevels));
		
		canvas.add(levelPanel);
		int row = (WarpedContinuity.NUM_OF_LEVELS % LEVELS_PER_PANEL - WarpedContinuity.NUM_OF_LEVELS % PANEL_COLS) / PANEL_COLS;
		levelPanel.setLocation(canvas.getWidth() / 8, (int) (canvas.getHeight() * (1 / 4.0 + row / 16.0)));
		levelPanel.setSize(canvas.getWidth() * 3 / 4 * numLevels / PANEL_COLS, canvas.getHeight() / 16);
		
		levelPanel.setVisible(false);
		
		return levelPanel;
	}
	
	private void createLevelButtons(JPanel levelPanel, int start, int end) {
		for (int levelNum = start; levelNum <= end; levelNum++) {		
			JButton levelButton = createLevelButton(levelNum);
			levelButtons[levelNum] = levelButton;
			levelPanel.add(levelButton);
		}
	}
	
	private void createDemoLevelButtons(JPanel levelPanel) {
		for (int levelNum: new int[] {1, 2, 3, 6, 15}) {
			JButton levelButton = createLevelButton(levelNum);
			levelButtons[levelNum] = levelButton;
			levelPanel.add(levelButton);
		}
	}
	
	private JButton createLevelButton(int levelNum) {
		JButton levelButton = new JButton();
		setLevelButtonText(levelButton, levelNum, saveData.getBestScore(levelNum));

		levelButton.addActionListener((ActionEvent e) -> {
			game.loadLevel(levelNum);
			game.setCurLevelNumber(levelNum);
			game.setCurScene(new LevelScene());
			hideButtons();
		});
		
		return levelButton;
	}
	
	private void createLeftArrowButton() {
		leftArrowButton = new BasicArrowButton(BasicArrowButton.WEST);
		canvas.add(leftArrowButton);
		leftArrowButton.setLocation(canvas.getWidth() / 8, canvas.getHeight() * 19 / 32);
		leftArrowButton.setSize(canvas.getWidth() / 16, canvas.getHeight() / 16);
		leftArrowButton.setVisible(false);

		leftArrowButton.addActionListener((ActionEvent e) -> {
			if (page != 0) {
				hideButtons();
				page--;
				showButtons();
			}
		});
	}
	
	private void createRightArrowButton() {
		rightArrowButton = new BasicArrowButton(BasicArrowButton.EAST);
		canvas.add(rightArrowButton);
		rightArrowButton.setLocation((int) (canvas.getWidth() * (7.0 / 8 - 1.0 / 16)), canvas.getHeight() * 19 / 32);
		rightArrowButton.setSize(canvas.getWidth() / 16, canvas.getHeight() / 16);
		rightArrowButton.setVisible(false);

		rightArrowButton.addActionListener((ActionEvent e) -> {
			if (page != levelPanels.length - 1) {
				hideButtons();
				page++;
				showButtons();
			}
		});
	}
	
	private void createDeleteSaveDataButton() {
		deleteSaveDataButton = new JButton("Delete Save Data");
		canvas.add(deleteSaveDataButton);
		deleteSaveDataButton.setLocation(canvas.getWidth() * 3 / 8, canvas.getHeight() * 21 / 25);
		deleteSaveDataButton.setSize(canvas.getWidth() / 4, canvas.getHeight() / 15);
		deleteSaveDataButton.setVisible(false);
		
		/*deleteSaveDataButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				int input = JOptionPane.showConfirmDialog(
					    canvas,
					    "Are you sure you want to delete save data?",
					    "Delete Save Data",
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.PLAIN_MESSAGE);
				if (input == JOptionPane.YES_OPTION) {
					saveData.deleteData();
					resetLevelButtons();
					game.saveGame();
				}
				canvas.requestFocus();
			};
		});*/

		deleteSaveDataButton.addActionListener((ActionEvent e) -> {
			int input = JOptionPane.showConfirmDialog(
				canvas,
				"Are you sure you want to delete save data?",
				"Delete Save Data",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE);
			if (input == JOptionPane.YES_OPTION) {
				saveData.deleteData();
				resetLevelButtons();
				game.saveGame();
			}
			canvas.requestFocus();			
		});
	}
	
	public void resetLevelButtons() {
		for (int i = 1; i < levelButtons.length; i++) {
			JButton levelButton = levelButtons[i];
			if (levelButton != null) {
				levelButton.setText(String.valueOf(i));
			}
		}
	}
	
	public void setLevelButtonText(JButton levelButton, int levelNumber, int score) {
		String scoreDisplay;
		if (score == -1) {
			scoreDisplay = "";
		}
		else if (score <= 99) {
			scoreDisplay = " (" + String.valueOf(score) + ')';
		}
		else {
			scoreDisplay = " (*)";
		}
		levelButton.setText(String.valueOf(levelNumber) + scoreDisplay);
	}
	
	public void showButtons() {
		/*for (int levelNum = 1; levelNum <= WarpedContinuity.NUM_OF_LEVELS; levelNum++) {
			levelButtons[levelNum].setVisible(true);
		}*/
		levelPanels[page].setVisible(true);
		if (page == levelPanels.length - 1 && lastPageLastLevelPanel != null) {
			lastPageLastLevelPanel.setVisible(true);
		}
		if (page != 0) {
			leftArrowButton.setVisible(true);
		}
		if (page != levelPanels.length - 1) {
			rightArrowButton.setVisible(true);
		}
		deleteSaveDataButton.setVisible(true);
	}
	
	public void hideButtons() {
		/*for (int levelNum = 1; levelNum <= WarpedContinuity.NUM_OF_LEVELS; levelNum++) {
			levelButtons[levelNum].setVisible(false);
		}*/
		levelPanels[page].setVisible(false);
		if (lastPageLastLevelPanel != null) {
			lastPageLastLevelPanel.setVisible(false);
		}
		leftArrowButton.setVisible(false);
		rightArrowButton.setVisible(false);
		deleteSaveDataButton.setVisible(false);
	}
}