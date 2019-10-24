package Pieces;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import Game.Piece;

//At
public class Knight extends Piece{

	public Knight(boolean isBlack) {
		super(isBlack);
	}

	public void drawYourself(Graphics g, int positionX, int positionY, int squareWidth) {

		if(isBlack)
		{
			ImageIcon basri = new ImageIcon("BKnight.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
		else
		{
			ImageIcon basri = new ImageIcon("WKnight.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
	}

	public boolean canMove(int x, int y) {
		return (Math.abs(x)==2 && Math.abs(y)==1) || (Math.abs(x)==1 && Math.abs(y)==2);
	}

	public boolean canCapture(int x, int y) {

		return canMove(x,y);
	}
}