package core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SaveData implements Serializable {

	private static final long serialVersionUID = 1L;

	//public static final String SAVE_FILE_NAME = "saveData30.ser";
	//public static final String SAVE_FILE_NAME = "saveData32.ser";
	//public static final String SAVE_FILE_NAME = "save_file.ser";
	public static final String SAVE_FILE_NAME = "SaveData.ser";
	
	private int numCompleted;
	private final Map<String, Integer> bestScores;
	
	public SaveData() {
		numCompleted = 0;
		bestScores = new HashMap<>();
		for (String levelName: LevelBuilder.LEVEL_ORDER.values()) {
			bestScores.put(levelName, -1);
		}
	}
	
	public int getNumCompleted() {
		return numCompleted;
	}

	public void incNumCompleted() {
		numCompleted++;
	}
	
	public Map<String, Integer> getBestScores() {
		return bestScores;
	}
	
	public int getBestScore(int levelNum) {
		String levelName = LevelBuilder.LEVEL_ORDER.get(levelNum);
		if (!bestScores.containsKey(levelName)) {
			bestScores.put(levelName, -1);
		}
		return bestScores.get(levelName);
	}
	
	public void setBestScore(int levelNum, int bestScore) {
		bestScores.put(LevelBuilder.LEVEL_ORDER.get(levelNum), bestScore);
	}
	
	public void deleteData() {
		numCompleted = 0;
		for (String levelName: bestScores.keySet()) {
			bestScores.put(levelName, -1);
		}
	}
}
