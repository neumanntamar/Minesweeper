package minesweeper;

import java.awt.*;
import javax.swing.*;

/**
 * BottomStrip
 * The strip at the bottom with the timer and the number of mines remaining
 */
public class BottomStrip extends JComponent
{
	private static final long serialVersionUID = 1L;
	private static JLabel mineLabel;
	private static JLabel timerLabel;
	private int amount;
	private Label space;
	private GameData gameData = GameData.getInstance();

	public BottomStrip()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER));

		//set up the timer image
		JLabel timeIcon = new JLabel();
		ImageIcon ticon = new ImageIcon(getClass().getResource("/minesweeper/images/timer.png"));
		Image timg = ticon.getImage();
		Image timeimg = timg.getScaledInstance(43, 43, Image.SCALE_SMOOTH);
		ticon = new ImageIcon(timeimg);
		timeIcon.setIcon(ticon);

		//the default value for the timer should be 000
		timerLabel = new JLabel("000");
		timerLabel.setFont(new Font("Calibri", Font.PLAIN, 32));

		this.add(timeIcon);
		this.add(timerLabel);

		//space between timer and bombs remaining
		amount = (gameData.getColumns() * 43) / 9;
		space = new Label(String.format("%" + (amount) + "s", ""));
		this.add(space);

		// set up mines and mine image
		mineLabel = new JLabel(gameData.getTotalMines() + " ");
		JLabel mineIcon = new JLabel();
		ImageIcon icon = new ImageIcon(getClass().getResource("/minesweeper/images/mine.png"));
		Image img = icon.getImage();
		Image mineimg = img.getScaledInstance(47, 47, Image.SCALE_SMOOTH);
		icon = new ImageIcon(mineimg);
		mineIcon.setIcon(icon);
		mineLabel.setFont(new Font("Calibri", Font.PLAIN, 32)); 

		this.add(mineLabel);
		this.add(mineIcon);
	}

	/**
	 * Reset the bottom strip when the game is reset
	 */
	public void reset()
	{
		amount = (gameData.getColumns() * 43) / 9;
		space.setText(String.format("%" + (amount) + "s", ""));
	}

	/**
	 * Change the value of the timer label each time the timer changes
	 * @param seconds the number of seconds that passed to set to the label
	 */
	public static void setTimerLabel(String seconds)
	{
		BottomStrip.timerLabel.setText(seconds);
	}

	/**
	 * Change the number of mines on the mines remaining label
	 * @param mines remaining mines
	 */
	public static void editMines(int mines)
	{
		mineLabel.setText(mines + " ");
	}

}
