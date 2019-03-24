package minesweeper;

import minesweeper.Minesweeper;
import java.awt.*;
import javax.swing.*;

public class GridComponent extends JComponent
{
	private static final long serialVersionUID = 1L;
	private GameData gameData = GameData.getInstance();
	private Minesweeper minesweeper;

	public GridComponent()
	{
		minesweeper = new Minesweeper();

		// create a grid for each cell
		setLayout(new GridLayout(gameData.getRows(), gameData.getColumns()));

		minesweeper.setImages();
		buildButtons();
	}

	/**
	 * creates a grid of buttons for the board
	 */
	private void buildButtons()
	{
		for (int i = 0; i < gameData.getRows(); i++)
		{
			for (int j = 0; j < gameData.getColumns(); j++)
			{
				Cell currentBtn = minesweeper.buildButton(i, j);
				this.add(currentBtn); // add the button to the grid layout
			}
		}
	}

	/**
	 * resets the grid on a new game
	 */
	public void reset()
	{
		setLayout(new GridLayout(gameData.getRows(), gameData.getColumns()));
		//check if the size of the board is the same (same level)
		boolean sameSize = minesweeper.reset();

		if (!sameSize)
		{
			for (int i = 0; i < minesweeper.getSquaresRows(); i++)
			{
				for (int j = 0; j < minesweeper.getSquaresColumns(i); j++)
				{
					this.remove(minesweeper.getButton(i, j));
				}
			}

			minesweeper.resetSize();
			buildButtons();
		}
	}

}
