package core;

import java.util.HashMap;
import java.util.Map;

import gui.Canvas;
import levels.Basic;
import levels.BasicDeadEnd;
import levels.BasicOpenSpace;
import levels.BasicStaircases;
import levels.BasicTunnel;
import levels.BorderMatch;
import levels.BorderMatchStaircase;
import levels.ClickOrb;
import levels.ClickOrbJumpSlide;
import levels.ClickOrbLowCeiling;
import levels.ClickOrbLowCeiling2;
import levels.ClickOrbNotNeeded;
import levels.ClickOrbRisingChain;
import levels.ClickOrbTraverse;
import levels.DiagonalPath;
import levels.FallingLanes;
import levels.FallingPair;
import levels.FewPlatformsNoJumpSlide;
import levels.FourByFour1;
import levels.GravityAndClickOrb;
import levels.GravityAvoidTheWells;
import levels.GravityOrbSuperJumpSlide;
import levels.GravityPlatforming;
import levels.GravityPlayground;
import levels.GravitySoftLock;
import levels.JumpSlideDiagonalPath;
import levels.JumpSlideFewPlatforms;
import levels.JumpSlideStaircases;
import levels.RemovableBlocksBuild;
import levels.RemovableBlocksJumpSlide;
import levels.RemovableBlocksSquare;
import levels.Roof;
import levels.SuperJumpSlide;
import levels.ThreeByThreeSlider;
import levels.TwoByThree1;
import levels.TwoByThree2;
import levels.VerticalClimb;
import levels.VerticalClimb2;

public class LevelBuilder {
	
	public static final  Map<Integer, String> LEVEL_ORDER = new HashMap<Integer, String>() {
	{
	    put(1, "Basic");
	    put(2, "BasicDeadEnd");
	    put(3, "BasicStaircases");
	    put(4, "BasicTunnel");
	    put(5, "BasicOpenSpace");
	    put(6, "BorderMatch");
	    put(7, "BorderMatchStaircase");
	    put(8, "TwoByThree1");
	    put(9, "TwoByThree2");
	    put(10, "FourByFour1");
	    put(11, "DiagonalPath");
	    put(12, "FallingLanes");
	    put(13, "Roof");
	    put(14, "VerticalClimb");
	    put(15, "FallingPair");
	    put(16, "JumpSlideFewPlatforms");
	    put(17, "JumpSlideStaircases");
	    put(18, "VerticalClimb2");
	    put(19, "GravitySoftLock");
	    put(20, "GravityAvoidTheWells");
	    put(21, "GravityPlatforming");
	    put(22, "ClickOrb");
	    put(23, "ClickOrbRisingChain");
	    put(24, "RemovableBlocksSquare");
	    put(25, "ClickOrbTraverse");
	    put(26, "ClickOrbLowCeiling");
	    put(27, "JumpSlideDiagonalPath");
	    put(28, "RemovableBlocksJumpSlide");
	    put(29, "ClickOrbNotNeeded");
	    put(30, "ClickOrbJumpSlide");
	    put(31, "ClickOrbLowCeiling2");
	    put(32, "GravityPlayground");
	    put(33, "GravityAndClickOrb");
	    put(34, "SuperJumpSlide");
	    put(35, "ThreeByThreeSlider");
	    put(36, "RemovableBlocksBuild");
	    put(37, "GravityOrbSuperJumpSlide");
	    //put(38, "FewPlatformsNoJumpSlide");
	}};

	public static Level buildLevel(Canvas canvas, int n) {
		String levelName = LEVEL_ORDER.get(n);
		return switch (levelName) {
			case "Basic" -> new Basic(canvas);
			case "BasicDeadEnd" -> new BasicDeadEnd(canvas);
			case "BasicStaircases" -> new BasicStaircases(canvas);
			case "BasicTunnel" -> new BasicTunnel(canvas);
			case "BasicOpenSpace" -> new BasicOpenSpace(canvas);
			case "BorderMatch" -> new BorderMatch(canvas);
			case "BorderMatchStaircase" -> new BorderMatchStaircase(canvas);
			case "DiagonalPath" -> new DiagonalPath(canvas);
			case "FallingLanes" -> new FallingLanes(canvas);
			case "VerticalClimb" -> new VerticalClimb(canvas);
			case "VerticalClimb2" -> new VerticalClimb2(canvas);
			case "Roof" -> new Roof(canvas);
			case "TwoByThree1" -> new TwoByThree1(canvas);
			case "TwoByThree2" -> new TwoByThree2(canvas);
			case "FourByFour1" -> new FourByFour1(canvas);
			case "FallingPair" -> new FallingPair(canvas);
			case "JumpSlideFewPlatforms" -> new JumpSlideFewPlatforms(canvas);
			case "JumpSlideStaircases" -> new JumpSlideStaircases(canvas);
			case "GravitySoftLock" -> new GravitySoftLock(canvas);
			case "GravityAvoidTheWells" -> new GravityAvoidTheWells(canvas);
			case "GravityPlatforming" -> new GravityPlatforming(canvas);
			case "ClickOrb" -> new ClickOrb(canvas);
			case "ClickOrbRisingChain" -> new ClickOrbRisingChain(canvas);
			case "ClickOrbTraverse" -> new ClickOrbTraverse(canvas);
			case "RemovableBlocksSquare" -> new RemovableBlocksSquare(canvas);
			case "ClickOrbLowCeiling" -> new ClickOrbLowCeiling(canvas);
			case "JumpSlideDiagonalPath" -> new JumpSlideDiagonalPath(canvas);
			case "RemovableBlocksJumpSlide" -> new RemovableBlocksJumpSlide(canvas);
			case "ClickOrbNotNeeded" -> new ClickOrbNotNeeded(canvas);
			case "ClickOrbJumpSlide" -> new ClickOrbJumpSlide(canvas);
			case "ClickOrbLowCeiling2" -> new ClickOrbLowCeiling2(canvas);
			case "GravityPlayground" -> new GravityPlayground(canvas);
			case "GravityAndClickOrb" -> new GravityAndClickOrb(canvas);
			case "SuperJumpSlide" -> new SuperJumpSlide(canvas);
			case "ThreeByThreeSlider" -> new ThreeByThreeSlider(canvas);
			case "RemovableBlocksBuild" -> new RemovableBlocksBuild(canvas);
			case "GravityOrbSuperJumpSlide" -> new GravityOrbSuperJumpSlide(canvas);
			case "FewPlatformsNoJumpSlide" -> new FewPlatformsNoJumpSlide(canvas);
			default -> null;
		};
	}
}

