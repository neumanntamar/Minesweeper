package minesweeper;

import javax.swing.*;
import java.awt.*;
import Enums.ButtonType;

/**
 * Cell
 * The cells of the grid layout
 * Extends JButton so that the cells can be clicked
 */
public class Cell extends JButton
{
	private static final long serialVersionUID = 1L;
	
	//the index of the button in the array
	private int xindex;
	private int yindex;
	
	//what the button has behind it
	private ButtonType type;
	private ImageIcon img;
	
	public Cell(int x, int y)
	{
		//change color of cell - gradient color
        setContentAreaFilled(false);
        
		xindex = x;
		yindex = y;
		type = ButtonType.BLANK;	//all squares should be initialized to blank
	}

	/**
	 * Set the color of the cell to a gradient color
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		final Graphics2D g2 = (Graphics2D) g.create();
		g2.setPaint(new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, getHeight()), Color.LIGHT_GRAY));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.dispose();

		super.paintComponent(g);
	}	
	
	/**
	 * Getters and setters
	 */
	public int getXindex()
	{
		return xindex;
	}

	public int getYindex()
	{
		return yindex;
	}
	
	public ButtonType getButtonType()
	{
		return type;
	}
	
	public void setButtonType(ButtonType type)
	{
		this.type = type;
	}
	
	public ImageIcon getImg()
	{
		return img;
	}
	
	public void setImg(ImageIcon img)
	{
		this.img = img;
	}

}
