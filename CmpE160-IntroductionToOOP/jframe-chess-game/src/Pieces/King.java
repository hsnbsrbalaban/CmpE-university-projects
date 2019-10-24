package Pieces;
import java.awt.Graphics;

import javax.swing.ImageIcon;

import Game.*;

//Şah
public class King extends Piece{

	public King(boolean isBlack) {
		super(isBlack);
	}

	public void drawYourself(Graphics g, int positionX, int positionY, int squareWidth) {
		if(isBlack)
		{
			ImageIcon basri = new ImageIcon("BKing.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
		else
		{
			ImageIcon basri = new ImageIcon("WKing.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
	}

	public boolean canMove(int x, int y) {
		return ((Math.abs(x)==1 || Math.abs(x)==0) && (Math.abs(y)==1 || Math.abs(y)==0));
	}

	public boolean canCapture(int x, int y) {

		return canMove(x,y);
	}
	
}
