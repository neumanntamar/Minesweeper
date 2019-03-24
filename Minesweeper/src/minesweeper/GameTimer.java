package minesweeper;

import java.awt.event.*;
import javax.swing.Timer;

/**
 * GameTimer
 * Creates a timer which is on the bottom strip
 * Uses the singelton pattern so that every class can have access to the same timer 
 */
public class GameTimer
{
	private static GameTimer gameTimer = null;
	private int elapsedSeconds;
	private Timer timer;

	/**
	 * Private constructor can only be called from inside the class
	 * makes sure only one instance of a timer
	 */
	private GameTimer()
	{
		elapsedSeconds = 0;
		timer = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (elapsedSeconds < 999)
				{
					elapsedSeconds++;
					BottomStrip.setTimerLabel(String.format("%03d", elapsedSeconds) + " ");
				}
			}
		});
	}
	
	/**
	 * Called instead of a constructor
	 * @return an instance of timer
	 * If an instance wasn't created yet, it creats a new one
	 * If an instance was already created, it returns that instance
	 */
	public static GameTimer getInstance()
	{
		if(gameTimer == null)
		{
			gameTimer = new GameTimer();
		}
		
		return gameTimer;	
	}

	/**
	 * Resets the timer by stopping it and setting the label to 000
	 */
	public void resetTimer()
	{
		stopTimer();
		BottomStrip.setTimerLabel("000");
	}

	/**
	 * Starts the timer
	 * sets the seconds passed back to zero
	 */
	public void startTimer()
	{
		elapsedSeconds = 0;
		timer.start();
	}

	/**
	 * stops the timer
	 */
	public void stopTimer()
	{
		timer.stop();
	}

}
