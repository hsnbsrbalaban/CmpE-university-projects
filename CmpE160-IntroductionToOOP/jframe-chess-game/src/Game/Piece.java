package Game;
import java.awt.Color;
import java.awt.Graphics;

public abstract class Piece {
	public boolean firstMove = true;
	/**
	 * Indicates this is a black or white piece
	 */
	protected boolean isBlack;
	
	/**
	 * creates a piece
	 * @param isBlack the piece becomes black if true
	 */
	
	public Piece(boolean isBlack)
	{
		this.isBlack = isBlack;
	}

	
	/**
	 * Draws this piece to the screen using the given 
	 * graphics object at the given location on the screen knowing that
	 * it should fit inside a square with given width. 
	 * @param g graphics object to use
	 * @param positionX x of the top-left corner of the square 
	 * @param positionY y of the top-left corner of the square
	 * @param squareWidth width of the square. 
	 */
	public abstract void drawYourself(Graphics g, int positionX, int positionY, int squareWidth);
	
	/**
	 * indicates whether this piece can move on the board in a single step
	 * by given x and y values (relative to its current position, not absolute)
	 * @param x horizontal component of the movement
	 * @param y vertical component of the movement
	 * @return true if the piece can move by x and y squares in one step
	 */
	public abstract boolean canMove(int x, int y);
	
	/**
	 * indicates whether this piece can capture another piece on the board with
	 * given x and y values (relative to its current position, not absolute)
	 * @param x horizontal distance of the piece to capture
	 * @param y vertical distance of the piece to capture
	 * @return true if the piece can capture
	 */
	public abstract boolean canCapture(int x, int y);
	
	public Color getColor(){
		return (isBlack ? Color.black : Color.white);
	}
	
}
