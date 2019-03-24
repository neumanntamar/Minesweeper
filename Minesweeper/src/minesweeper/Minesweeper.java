package minesweeper;

import minesweeper.MinesweeperJFrame;
import minesweeper.BottomStrip;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import Enums.ButtonType;
import java.net.URL;

public class Minesweeper
{
	private HashMap<String, ImageIcon> images;
	private GameData gameData;
	private GameTimer timer;
	
	// number of buttons so know when the player won
	private int countButtons;

	// used so that the user can't click on a bomb their first turn
	private boolean isFirst;

	// the grid of squares - they're buttons so you could click them
	private Cell squares[][];

	public Minesweeper()
	{
		images = new HashMap<String, ImageIcon>();
		gameData = GameData.getInstance();
		squares = new Cell[gameData.getRows()][gameData.getColumns()];
		countButtons = 0;
		isFirst = true;
		timer = GameTimer.getInstance();
	}

	/**
	 * Resizes all images to fit the squares on the board
	 * Then adds them all to a HashMap
	 */
	public void setImages()
	{
		// create array of images
		images.put("zero", new ImageIcon(getClass().getResource("/minesweeper/images/zero.png")));
		images.put("one", new ImageIcon(getClass().getResource("/minesweeper/images/one.png")));
		images.put("two", new ImageIcon(getClass().getResource("/minesweeper/images/two.png")));
		images.put("three", new ImageIcon(getClass().getResource("/minesweeper/images/three.png")));
		images.put("four", new ImageIcon(getClass().getResource("/minesweeper/images/four.png")));
		images.put("five", new ImageIcon(getClass().getResource("/minesweeper/images/five.png")));
		images.put("six", new ImageIcon(getClass().getResource("/minesweeper/images/six.png")));
		images.put("seven", new ImageIcon(getClass().getResource("/minesweeper/images/seven.png")));
		images.put("eight", new ImageIcon(getClass().getResource("/minesweeper/images/eight.png")));
		images.put("mine", new ImageIcon(getClass().getResource("/minesweeper/images/mineboard.png")));
		images.put("xmine", new ImageIcon(getClass().getResource("/minesweeper/images/xmine.png")));
		images.put("flag", new ImageIcon(getClass().getResource("/minesweeper/images/flag.png")));
		images.put("hit-mine", new ImageIcon(getClass().getResource("/minesweeper/images/hit_bomb.png")));

		Image imgTemp;
		for (String key : images.keySet())
		{
			//the flag image is the only one that should be smaller than the button, and not be a square
			if (images.get(key).equals(images.get("flag")))
			{
				imgTemp = images.get(key).getImage().getScaledInstance(25, 30, Image.SCALE_SMOOTH);
			}
			else
			{
				imgTemp = images.get(key).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			}
			images.put(key, new ImageIcon(imgTemp));
		}
	}

	/**
	 * Build an array of buttons, each with an event handler
	 * @param x The row of the buttons location on the board
	 * @param y The column of the buttons location on the board
	 * @return The new button that was created
	 */
	public Cell buildButton(int x, int y)
	{
		squares[x][y] = new Cell(x, y);

		squares[x][y].addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				Cell button = (Cell) e.getSource();

				// checks if the button was clicked with right or left 
				if (SwingUtilities.isLeftMouseButton(e))
					leftMouseClick(button);
				if (SwingUtilities.isRightMouseButton(e))
					rightMouseClick(button);
			}

			// methods needed for interface that don't want to do anything
			public void mouseExited(MouseEvent arg0){}
			public void mousePressed(MouseEvent arg0){}			
			public void mouseReleased(MouseEvent arg0){}
			public void mouseEntered(MouseEvent arg0){}
		});

		return squares[x][y];
	}

	/**
	 * Randomly put bombs in the grid
	 * @param firstButton - The first button that was clicked should never have a bomb
	 */
	private void setMines(Cell firstButton)
	{
		Random rand = new Random();
		int x, y;
		for (int i = 0; i < gameData.getTotalMines();)
		{
			//choose random coordinate
			x = rand.nextInt(gameData.getRows());
			y = rand.nextInt(gameData.getColumns());
			
			//check that the random coordinate is blank before setting it to a bomb
			if (squares[x][y].getButtonType() == ButtonType.BLANK && !squares[x][y].equals(firstButton))
			{
				squares[x][y].setButtonType(ButtonType.MINE);
				squares[x][y].setImg(images.get("mine"));
				i++;
			}
		}
	}

	/**
	 * Count the number of bombs each cell is touching and set the image to that cell
	 */
	private void setNumbers()
	{

		ButtonType type = ButtonType.BLANK;
		ImageIcon icon = null;
		Cell current;

		// if the cell is blank, check the cells around it
		for (int i = 0; i < gameData.getRows(); i++)
		{
			for (int j = 0; j < gameData.getColumns(); j++)
			{
				int numMines = 0;
				if (squares[i][j].getButtonType() != ButtonType.MINE)
				{

					// 1 cell up
					current = getActiveCell(i - 1, j);
					if (current != null && current.getButtonType() == ButtonType.MINE)
						numMines++;

					// 1 cell up to the left
					current = getActiveCell(i - 1, j - 1);
					if (current != null && current.getButtonType() == ButtonType.MINE)
						numMines++;

					// 1 cell up to the right
					current = getActiveCell(i - 1, j + 1);
					if (current != null && current.getButtonType() == ButtonType.MINE)
						numMines++;

					// 1 cell to the left
					current = getActiveCell(i, j - 1);
					if (current != null && current.getButtonType() == ButtonType.MINE)
						numMines++;

					// 1 cell down to the left
					current = getActiveCell(i + 1, j - 1);
					if (current != null && current.getButtonType() == ButtonType.MINE)
						numMines++;

					// 1 cell down
					current = getActiveCell(i + 1, j);
					if (current != null && current.getButtonType() == ButtonType.MINE)
						numMines++;

					// 1 cell down to the right
					current = getActiveCell(i + 1, j + 1);
					if (current != null && current.getButtonType() == ButtonType.MINE)
						numMines++;

					// 1 cell to the right
					current = getActiveCell(i, j + 1);
					if (current != null && current.getButtonType() == ButtonType.MINE)
						numMines++;

					// set the button type and image of the cell based on the surrounding number of bombs
					switch (numMines)
					{
						case 1:
							icon = images.get("one");
							type = ButtonType.ONE;
							break;
						case 2:
							icon = images.get("two");
							type = ButtonType.TWO;
							break;
						case 3:
							icon = images.get("three");
							type = ButtonType.THREE;
							break;
						case 4:
							icon = images.get("four");
							type = ButtonType.FOUR;
							break;
						case 5:
							icon = images.get("five");
							type = ButtonType.FIVE;
							break;
						case 6:
							icon = images.get("six");
							type = ButtonType.SIX;
							break;
						case 7:
							icon = images.get("seven");
							type = ButtonType.SEVEN;
							break;
						case 8:
							icon = images.get("eight");
							type = ButtonType.EIGHT;
							break;
						default:
							icon = images.get("zero");
							type = ButtonType.BLANK;

					}
					squares[i][j].setButtonType(type);
					squares[i][j].setImg(icon);
				}
			}
		}
	}

	/**
	 * Determine what should be done when a button is clicked with the left mouse button
	 * @param button The button that was clicked.
	 */
	public void leftMouseClick(Cell button)
	{
		//check the button wasn't already clicked
		if (button.isEnabled())
		{
			//set the bombs and timer after the first click
			if (isFirst)
			{
				isFirst = false;
				timer.startTimer();
				setMines(button);
				setNumbers();
			}
			//if mine is clicked and not flagged
			if (button.getButtonType() == ButtonType.MINE && !images.get("flag").equals(button.getIcon()))
			{
				if(gameData.getSound())
				{
					try
					{
						// Open an audio input stream.     
	                    URL soundFile = getClass().getResource("/minesweeper/sound/mine.au");
						AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
						// Get a sound clip resource.
						Clip clip = AudioSystem.getClip();
						// Open audio clip and load samples from the audio input stream.
						clip.open(audioIn);
						clip.start();
					}
					catch (Exception e)
					{
						JOptionPane.showOptionDialog(null, "There is an error with the sound. Please restart the game.", "Error",
								JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
								null, null);
					}
				}
				button.setImg(images.get("hit-mine"));

				timer.stopTimer();
				gameOver();
			}

			// if the button has a flag, ignore left click
			else if (!images.get("flag").equals(button.getIcon()))
			{
				//if button is blank, flip over all surround blank buttons and cells directly around them
				if (button.getButtonType() == ButtonType.BLANK)
				{
					Stack<Cell> buttonStack = new Stack<Cell>();
					Cell currentButton;

					buttonStack.push(button);

					while (!buttonStack.isEmpty())
					{
						currentButton = buttonStack.pop();

						if (currentButton != null && currentButton.isEnabled())
						{
							// only flip the button if it is not a flag
							if (!images.get("flag").equals(currentButton.getIcon()))
								flipButton(currentButton);
							int i = currentButton.getXindex();
							int j = currentButton.getYindex();

							if (currentButton.getButtonType() == ButtonType.BLANK)
							{
								// left
								buttonStack.push(getActiveCell(i - 1, j));

								// left, up
								buttonStack.push(getActiveCell(i - 1, j - 1));

								// left, down
								buttonStack.push(getActiveCell(i - 1, j + 1));

								// up
								buttonStack.push(getActiveCell(i, j - 1));

								// right, up
								buttonStack.push(getActiveCell(i + 1, j - 1));

								// right
								buttonStack.push(getActiveCell(i + 1, j));

								// right, down
								buttonStack.push(getActiveCell(i + 1, j + 1));

								// down
								buttonStack.push(getActiveCell(i, j + 1));
							}
						}
					}
				}
				else
				{
					flipButton(button);
				}
			}
		}
		//if all buttons other than mines are flipped over win the game
		if (isFinished())
		{
			timer.stopTimer();

			ImageIcon image = new ImageIcon(getClass().getResource("/minesweeper/images/win.png"));
			ImageIcon win = new ImageIcon(image.getImage().getScaledInstance(100, 70, Image.SCALE_SMOOTH));

			int clicked = JOptionPane.showOptionDialog(null, "Congratulations you won!", "Game won",
					JOptionPane.CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, win,
					new String[] { "New Game", "Cancel" }, null);
			if (clicked == 0)
			{
				MinesweeperJFrame.reset();

			}
		}
	}

	/**
	 * Method to see if the game was finished.
	 * @return If the game was finished.
	 */
	private boolean isFinished()
	{
		return countButtons == (gameData.getRows() * gameData.getColumns() - gameData.getTotalMines());
	}

	/**
	 * Flips over all the mines and shows a dialog box to either
	 * restart or cancel the round
	 */
	private void gameOver()
	{
		for (int i = 0; i < gameData.getRows(); i++)
		{
			for (int j = 0; j < gameData.getColumns(); j++)
			{
				squares[i][j].setEnabled(false);

				if (images.get("flag").equals(squares[i][j].getIcon())
						&& squares[i][j].getButtonType() != ButtonType.MINE)
				{
					squares[i][j].setDisabledIcon(images.get("xmine"));
					squares[i][j].setIcon(images.get("xmine"));

				}

				else if (images.get("flag").equals(squares[i][j].getIcon())
						&& squares[i][j].getButtonType() == ButtonType.MINE)
				{
					squares[i][j].setDisabledIcon(images.get("flag"));
					squares[i][j].setIcon(images.get("flag"));

				}

				else if (squares[i][j].getButtonType() == ButtonType.MINE)
				{
					squares[i][j].setDisabledIcon(squares[i][j].getImg());
					squares[i][j].setIcon(squares[i][j].getImg());
				}

			}
		}
		ImageIcon image = new ImageIcon(getClass().getResource("/minesweeper/images/gameOver.png"));
		ImageIcon Lose = new ImageIcon(image.getImage().getScaledInstance(300, 100, Image.SCALE_SMOOTH));

		int clicked = JOptionPane.showOptionDialog(null, null, "Game Over", JOptionPane.CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, Lose, new String[] { "New Game", "Cancel" }, null);
		if (clicked == 0)
		{
			MinesweeperJFrame.reset();
		}
	}

	/**
	 * toggel flag on right click
	 */
	private void rightMouseClick(Cell button)
	{
		// only allow a button to be clicked if it was not disabled already on left click.
		if (button.isEnabled())
		{
			if (images.get("flag").equals(button.getIcon())) 
			{
				button.setIcon(null);
				BottomStrip.editMines(gameData.addRemainingMines(1));
			}
			else if (gameData.getRemainingMines() > 0)
			{
				button.setIcon(images.get("flag"));
				BottomStrip.editMines(gameData.addRemainingMines(-1));
			}
		}
	}

	/**
	 * Flip over a button
	 * @param button The button to flip over
	 */
	private void flipButton(Cell button)
	{
		countButtons++;
		// disable the button once it is flipped over
		button.setEnabled(false);
		// set the image of the button
		button.setIcon(button.getImg());
		// also set the disabled image so it is not grayed out.
		button.setDisabledIcon(button.getImg());
	}

	/**
	 * Get a cell if it is enabled
	 * @param x The row of the button
	 * @param y The column of the button
	 * @return The button that is enabled or null if it is disabled
	 */
	private Cell getActiveCell(int x, int y)
	{
		if (x >= 0 && x < gameData.getRows() && y >= 0 && y < gameData.getColumns() && squares[x][y].isEnabled())
			return squares[x][y];
		return null;
	}

	/**
	 * Reset the board
	 * @return True if the new board is the same size or false if it is different
	 */
	public boolean reset()
	{
		countButtons = 0;
		isFirst = true;

		if (gameData.getRows() == squares.length && gameData.getColumns() == squares[0].length)
		{
			// go through the buttons and reset the images and button types
			for (int i = 0; i < squares.length; i++)
			{
				for (int j = 0; j < squares[i].length; j++)
				{
					squares[i][j].setButtonType(ButtonType.BLANK);
					squares[i][j].setIcon(null);
					squares[i][j].setEnabled(true);
				}
			}

			// reset the number of mines used.
			gameData.resetRemainingMines();

			return true;
		}
		return false;
	}
	
	/**
	 * Reset the size of the squares grid
	 */
	public void resetSize()
	{
		squares = new Cell[gameData.getRows()][gameData.getColumns()];
	}

	/**
	 * getters and setters
	 */
	public int getSquaresRows()
	{
		return squares.length;
	}

	public int getSquaresColumns(int row)
	{
		return squares[row].length;
	}

	/**
	 * Get a button
	 * @param x The row coordinate of the button
	 * @param y The column coordinate of the button
	 * @return The cell at the given coordinates. 
	 */
	public Cell getButton(int x, int y)
	{
		return squares[x][y];
	}
}
