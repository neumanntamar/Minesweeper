package minesweeper;

import minesweeper.BottomStrip;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;

public class MinesweeperJFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static GameData gameData = GameData.getInstance();
	private static Container container;
	private static int bottom;
	private static int side;
	private static GridComponent grid;
	private static BottomStrip below;
	private static AudioInputStream audioIn;
	private static Clip clip;

	public static void main(String[] args)
	{
		grid = new GridComponent();
		below = new BottomStrip();
		MinesweeperJFrame frame = new MinesweeperJFrame();
		
		frame.setVisible(true);
	}

	public MinesweeperJFrame()
	{
		setTitle("Minesweeper");
		setSize(1330, 826);

		// window closes when click x
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// the user can't resize the screen - ensures the buttons stay squares
		setResizable(false);

		// centers the window on the screen
		setLocationRelativeTo(null);

		// set icon image
		ImageIcon image = new ImageIcon(getClass().getResource("/minesweeper/images/icon.png"));
		setIconImage(image.getImage());

		// create container
		container = getContentPane();

		// set the boarder for dif amounts based on the amount of buttons
		bottom = 688 - (gameData.getRows() * 43);
		side = 1330 - (gameData.getColumns() * 43);

		((JComponent) container).setBorder(BorderFactory.createEmptyBorder(40, side / 2, bottom, side / 2));
		container.setLayout(new BorderLayout());

		container.add(grid, BorderLayout.CENTER); // add grid
		container.add(below, BorderLayout.SOUTH); // add number of mines remaining

		if(gameData.getSound())
		{
			try
			{
				// Open an audio input stream.
				URL soundFile = getClass().getResource("/minesweeper/sound/background.au");
				audioIn = AudioSystem.getAudioInputStream(soundFile);
				// Get a sound clip resource.
				clip = AudioSystem.getClip();
				// Open audio clip and load samples from the audio input stream.
				clip.open(audioIn);
				clip.loop(Integer.MAX_VALUE);
			}
			catch (Exception e)
			{
				JOptionPane.showOptionDialog(null, "There is an error with the sound. Please restart the game.", "Error",
						JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
						null, null);
			}
		}
                
		setJMenuBar(createMenu());
	}

	/**
	 * Reset the game
	 * every class needs to be reset
	 */
	public static void reset()
	{
		// reset the grid
		grid.reset();
		gameData = GameData.getInstance();

		// reset the borders
		bottom = 688 - (gameData.getRows() * 43);
		side = 1330 - (gameData.getColumns() * 43);
		((JComponent) container).setBorder(BorderFactory.createEmptyBorder(40, side / 2, bottom, side / 2));

		// reset the remaining mines
		below.reset();
		BottomStrip.editMines(gameData.getTotalMines());
		
		//reset the timer
		GameTimer timer = GameTimer.getInstance();
		timer.stopTimer();
		timer.resetTimer();
	}

	/**
	 * create a menu bar to be added to the frame
	 * @return the menu bar
	 */
	private JMenuBar createMenu()
	{
		JMenuBar menuBar;
		JMenuItem menuNew, menuExit, sound, beginner, intermediate, advanced, custom;
		JMenu menu, selectLevel;

		menuBar = new JMenuBar();
		menu = new JMenu("Menu");

		//Create New Game option on the menu
		menuNew = new JMenuItem("New Game");
		menuNew.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { reset(); }
		});
		menu.add(menuNew);

		//Create Select Level options on the menu
		selectLevel = new JMenu("Select Level");		
		beginner = new JMenuItem("Beginner");
		intermediate = new JMenuItem("Intermediate");
		advanced = new JMenuItem("Advanced");
		custom = new JMenuItem("Custom");
		selectLevel.add(beginner);
		selectLevel.add(intermediate);
		selectLevel.add(advanced);
		selectLevel.add(custom);

		LevelActionListener listener = new MinesweeperJFrame.LevelActionListener();
		beginner.addActionListener(listener);
		intermediate.addActionListener(listener);
		advanced.addActionListener(listener);
		custom.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				SpinnerModel rows = new SpinnerNumberModel(8, 8, 16, 1);
				JSpinner rowspinner = new JSpinner(rows);
				SpinnerModel columns = new SpinnerNumberModel(8, 8, 30, 1);
				JSpinner columnspinner = new JSpinner(columns);
				SpinnerModel mines = new SpinnerNumberModel(10, 1, 200, 1);
				JSpinner minespinner = new JSpinner(mines);				
				
				Object[] message = { "Rows:", rowspinner, "Columns:", columnspinner, "Mines:", minespinner };
				int clicked = JOptionPane.showConfirmDialog(null, message, "Custom Level", JOptionPane.PLAIN_MESSAGE,
						JOptionPane.PLAIN_MESSAGE, null);
				
				if (clicked == 0)
				{
					//if the number of mines if too high - automatically set it to the highest it could be
					if((int)mines.getValue() > ((int)rows.getValue() * (int)columns.getValue()) / 3)
						mines.setValue((int)rows.getValue() * (int)columns.getValue() / 3);
					
					//change level
					GameData gameData = GameData.getInstance();
					gameData.changeLevel((int)rows.getValue(), (int)columns.getValue(), (int)mines.getValue());
					reset();
				}
			}
		});
		menu.add(selectLevel);

		// add sound options to menu
		ImageIcon image = new ImageIcon(getClass().getResource("/minesweeper/images/sound.png"));
		ImageIcon soundImage = new ImageIcon(image.getImage().getScaledInstance(80, 50, Image.SCALE_SMOOTH));

		sound = new JMenuItem("Sound Options");
		sound.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				Object[] options = { "yes", "no" };
				Object clicked = JOptionPane.showInputDialog(null, "Play background music?                    ",
						"Sound Options", JOptionPane.QUESTION_MESSAGE, soundImage, options, null);
				if (clicked != null)
				{
					if (clicked.equals("yes"))
					{
						clip.start();
						clip.loop(Integer.MAX_VALUE);
						gameData.setSound(true);
					}
					else
					{
						clip.stop();
						gameData.setSound(false);
					}
				}
			}
		});
		menu.add(sound);

		// Exit game for menu
		menuExit = new JMenuItem("Exit");
		menuExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { System.exit(0); }
		});
		menu.add(menuExit);

		menuBar.add(menu);
		return menuBar;
	}

	/**
	 * action listener for levels
	 */
	public static class LevelActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JMenuItem menuItem = (JMenuItem) e.getSource();
			String level = menuItem.getText();

			GameData gameData = GameData.getInstance();
			if (level.equals("Beginner"))
			{			
				gameData.changeLevel(9, 9, 10);
			}
			else if (level.equals("Intermediate"))
			{
				gameData.changeLevel(16, 16, 40);
			}
			else
			{
				gameData.changeLevel(16, 30, 99);
			}
			reset();
		}
	}
}
