package Pieces;
import java.awt.Graphics;

import javax.swing.ImageIcon;

import Game.*;

public class Queen extends Piece{

	public Queen(boolean isBlack) {
		super(isBlack);
	}

	public void drawYourself(Graphics g, int positionX, int positionY, int squareWidth) {
		if(isBlack)
		{
			ImageIcon basri = new ImageIcon("BQueen.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
		else
		{
			ImageIcon basri = new ImageIcon("WQueen.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
	}

	public boolean canMove(int x, int y) {
		Bishop temp = new Bishop(isBlack);
		Rook temp1 = new Rook(isBlack);

		return temp.canMove(x, y) || temp1.canMove(x, y);
	}

	public boolean canCapture(int x, int y) {

		return canMove(x,y);
	}

}
