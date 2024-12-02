package hw2;

import api.PlayerPosition;
import api.BallType;
import static api.PlayerPosition.*;
import static api.BallType.*;

/**
 * Class that models the game of three-cushion billiards.
 * 
 * @author Cooper Kuhlemeier
 */
public class ThreeCushion {

	/*
	 * Has the player started their shot by hitting the cueBall?
	 */
	private boolean shotStarted;
	
	/*
	 * If the player has not started their shot then the inning has not started.
	 */
	private boolean inningStarted;
	
	/*
	 * Keeps track of what the inning of the game is.
	 */
	private int inningNumber;
	
	/*
	 * Keeps track of which player will be attempting to score points during that inning.
	 */
	private PlayerPosition inningPlayer;
	
	/*
	 * Keeps track of what color the cueBall is for the current inningPlayer.
	 */
	private BallType cueBall;
	
	/*
	 * Keeps track of whether or not this shot is the breakShot.
	 */
	private boolean breakShot;
	
	/*
	 * Keeps track of Player A's score.
	 */
	private int playerAScore;
	
	/*
	 * Keeps track of Player B's score.
	 */
	private int playerBScore;
	
	/*
	 * Keeps track of whether or not the cueBall has been hit.
	 */
	private boolean cueBallHit;
	
	/*
	 * Keeps track of whether or not the cueBall has hit the red ball.
	 */
	private boolean cueHitRed;
	
	/*
	 * Keeps track of whether or not the cueBall has hit the opposite players cueBall.
	 */
	private boolean cueHitOpposite;
	
	/*
	 * Keeps track of how many times the side cushions have been impacted during a shot.
	 */
	private int cushionImpact;
	
	/*
	 * Keeps track of the color of the cueBall opposite of the current inningPlayer. 
	 */
	private BallType oppositeCue;
	
	/*
	 * Keeps track of how many times a ball or cushion have been struck during a shot.
	 */
	private int strikeCount;
	
	/*
	 * Holds a true false value for whether or not a foul has been called during current inning.
	 */
	private boolean noFoulCalled;
	
	/*
	 * Holds a true false value for whether or not a bank shot has been achieved on shot attempt.
	 */
	private boolean bankShot;
	
	/*
	 * Helps track whether or not conditions will be satisfied for a bank shot.
	 */
	private boolean bankIsOpen;
	
	/*
	 * Holds a true false value for whether or not a point was scored on current shot attempt.
	 */
	private boolean pointScored;
	
	/**
	 * Variable used to help call a foul if a player hits a ball before shot has ended.
	 */
	private boolean shotNotEnded;
	
	/**
	 * Holds the value of whoever won the lag.
	 */
	private PlayerPosition lagWinner;
	
	/**
	 * Sets the required number of points to win.
	 */
	private int pointsToWin;
	
	/**
	 * Holds a true false value showing whether the lagWinner has chosen.
	 */
	private boolean lagChosen;
	
	/*
	 * Creates a new game of three-cushion billiards with a given lag winner
	 * and the predetermined number of points required to win the game.
	 * The inning count starts at 1. 
	 * lagWinner is either player A or player B
	 * pointsToWin is the number of points needed to reach the end of the game
	 */
	public ThreeCushion(PlayerPosition lagWinner, int pointsToWin) {
		this.lagWinner = lagWinner;
		this.pointsToWin = pointsToWin;
		inningNumber = 1;
		noFoulCalled = true;
	}
	
	/*
	 * Indicates the given ball has impacted the given cushion.
	 */
	public void cueBallImpactCushion() {
		if (shotStarted == true) {
			cushionImpact += 1;
		}
		
		if (breakShot == true) {
			if ((cushionImpact > 0 && cueHitRed == false)) {
				foul();
			}
		}
		
		if (cushionImpact >= 3 && (cueHitRed == false && cueHitOpposite == false)) {
			bankIsOpen = true;
		}
	}
	
	/*
	 *  Indicates the player's cue ball has struck the given ball.
	 */
	public void cueBallStrike(BallType ball) {
		if (cueBallHit == true && shotStarted == true) {
			strikeCount = 1;
			
			if (ball == RED && cueHitOpposite == false ) {
				cueHitRed = true;
				strikeCount += 1;
				if (cushionImpact == 0 || cushionImpact >= 3) {
					bankIsOpen = true;
				}
			} else if (ball == oppositeCue && cueHitRed == false ) {
				cueHitOpposite = true;
				strikeCount += 1;
				if (cushionImpact == 0 || cushionImpact >= 3) {
					bankIsOpen = true;
				}
			} else if ((ball == RED && cueHitOpposite == true && cushionImpact < 3) ||
					   (ball == oppositeCue && cueHitRed == true && cushionImpact < 3)) {
				foul();
			} 
			
			if (breakShot == true) {
				if ((cueHitOpposite == true && cueHitRed == false)) {
					foul();
				}
			}
			if ((strikeCount >= 2 && cushionImpact >= 3)){
				strikeCount += cushionImpact;
				if ((ball == RED && cueHitOpposite == true) ||
					(ball == oppositeCue && cueHitRed == true)) {
					pointScored = true;
				}
			}
			if (bankIsOpen == true && cushionImpact >= 3 && 
				(ball == RED || ball == oppositeCue)) {
				bankShot = true;
				pointScored = true;
			}
			
		}
	}
	
	/*
	 * Indicates the cue stick has struck the given ball.
	 */
	public void cueStickStrike(BallType ball) {
		if (shotNotEnded == true) {
			foul();
		}
		shotStarted = true;
		inningStarted = true;
		if (ball == cueBall) {
			cueBallHit = true;
			shotNotEnded = true;
		} else {
			cueBallHit = false;
			foul();
		}
	}
	
	/*
	 * Indicates that all balls have stopped motion.
	 */
	public void endShot() {
		if (inningStarted == true && isGameOver() == false && lagChosen == true) {
			shotNotEnded = false;
			
			if (pointScored == false && noFoulCalled == true) {
				foul();
			} else if (pointScored == true) {
				if (inningPlayer == PLAYER_A) {
					playerAScore += 1;
				} else if (inningPlayer == PLAYER_B) {
					playerBScore += 1;
				}
			}
			cueHitRed = false;
			cueHitOpposite = false;
			cushionImpact = 0;
			strikeCount = 0;
			bankShot = false;
			bankIsOpen = false;
			noFoulCalled = true;
		}
	}
	
	/*
	 *  A foul immediately ends the player's inning, even if the current shot has not ended.
	 */
	public void foul() {
		if (isGameOver() == false && lagChosen == true) {
			shotStarted = false;
			breakShot = false;
			inningStarted = false;
			inningNumber += 1;
			if (cueBall == WHITE) {
				cueBall = YELLOW;
				oppositeCue = WHITE;
			} else if (cueBall == YELLOW) {
				cueBall = WHITE;
				oppositeCue = YELLOW;
			} 
			if (inningPlayer == PLAYER_A) {
				inningPlayer = PLAYER_B;
			} else if (inningPlayer == PLAYER_B) {
				inningPlayer = PLAYER_A;
			}
			noFoulCalled = false;
			pointScored = false;
		}
	}
	
	/*
	 * Gets the cue ball of the current player.
	 */
	public BallType getCueBall() {
		return cueBall;
	}
	
	/*
	 * Gets the inning number.
	 */
	public int getInning() {
		return inningNumber;
	}
	
	/*
	 * Gets the current player.
	 */
	public PlayerPosition getInningPlayer() {
		return inningPlayer;
	}
	
	/*
	 * Gets the number of points scored by player A.
	 */
	public int getPlayerAScore() {
		return playerAScore;
	}
	
	/*
	 * Gets the number of points scored by player B.
	 */
	public int getPlayerBScore() {
		return playerBScore;
	}
	
	/*
	 * Returns true if and only if the most recently completed shot was a bank shot.
	 */
	public boolean isBankShot() {
		return bankShot;
	}
	
	/*
	 * Returns true if and only if this is the break shot. (first shot of the game)
	 */
	public boolean isBreakShot() {
		return breakShot;
	}
	
	/*
	 * Returns true if the game is over. (A player has reached the designated points to win)
	 */
	public boolean isGameOver() {
		if (playerAScore >= pointsToWin || playerBScore >= pointsToWin) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Returns true if the shooting player has taken their first shot of the inning.
	 */
	public boolean isInningStarted() {
		if (shotStarted == true) {
			inningStarted = true;
		} else {
			inningStarted = false;
		}
		
		if (isGameOver() == true) {
			inningStarted = false;
		}
		
		return inningStarted;
	}
	
	/*
	 * Returns true if a shot has been taken. (see cueStickStrike, but not ended (see endShot()))
	 */
	public boolean isShotStarted() {
		return shotStarted;
	}
	
	/*
	 * Sets whether the player that won the lag chooses to break, (take first shot)
	 * or choose the other player to break.
	 */
	public void lagWinnerChooses(boolean selfBreak, BallType cueBall) {
		if (selfBreak == true) {
			inningPlayer = lagWinner;
		} else {
			if (lagWinner == PLAYER_A) {
				inningPlayer = PLAYER_B;
			} else {
				inningPlayer = PLAYER_A;
			}
		}
		
		if (cueBall.equals(WHITE)&& selfBreak == true) {
			this.cueBall = WHITE;
			oppositeCue = YELLOW;
		} else if (cueBall.equals(YELLOW) && selfBreak == true) {
			this.cueBall = YELLOW;
			oppositeCue = WHITE;
		} else if (cueBall.equals(WHITE) && selfBreak == false) {
			this.cueBall = YELLOW;
			oppositeCue = WHITE;
		} else if (cueBall.equals(YELLOW) && selfBreak == false) {
			this.cueBall = WHITE;
			oppositeCue = YELLOW;
		}
		
		breakShot = true;
		lagChosen = true;
	}

	/**
	 * Returns a one-line string representation of the current game state. The
	 * format is:
	 * <p>
	 * <tt>Player A*: X Player B: Y, Inning: Z</tt>
	 * <p>
	 * The asterisks next to the player's name indicates which player is at the
	 * table this inning. The number after the player's name is their score. Z is
	 * the inning number. Other messages will appear at the end of the string.
	 * 
	 * @return one-line string representation of the game state
	 */
	public String toString() {
		String fmt = "Player A%s: %d, Player B%s: %d, Inning: %d %s%s";
		String playerATurn = "";
		String playerBTurn = "";
		String inningStatus = "";
		String gameStatus = "";
		if (getInningPlayer() == PLAYER_A) {
			playerATurn = "*";
		} else if (getInningPlayer() == PLAYER_B) {
			playerBTurn = "*";
		}
		if (isInningStarted()) {
			inningStatus = "started";
		} else {
			inningStatus = "not started";
		}
		if (isGameOver()) {
			gameStatus = ", game result final";
		}
		return String.format(fmt, playerATurn, getPlayerAScore(), playerBTurn, getPlayerBScore(), getInning(),
				inningStatus, gameStatus);
	}
}
