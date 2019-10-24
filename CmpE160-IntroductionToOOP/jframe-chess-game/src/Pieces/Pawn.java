package Pieces;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import Game.Piece;

public class Pawn extends Piece{

	public Pawn(boolean isBlack) {
		super(isBlack);
	}

	public void drawYourself(Graphics g, int positionX, int positionY, int squareWidth) {		

		if(isBlack)
		{
			ImageIcon basri = new ImageIcon("BPawn.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
		else
		{
			ImageIcon basri = new ImageIcon("WPawn.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}

	}

	public boolean canMove(int x, int y) {

		if(firstMove){
			if(isBlack){
				if((y == 2 || y == 1) && x == 0){
					firstMove = false;
					return true;
				}
				else return false;
			}
			else{
				if((y == -2 || y == -1) && x == 0){
					firstMove = false;
					return true;
				}
				else return false;
			}
		}
		//		else{
		if(isBlack){
			if(y==1 && x==0)
				return true;
			else return false;
		}
		else{
			if(y==-1 && x==0)
				return true;
			else return false;
		}

	}

	//	}

	public boolean canCapture(int x, int y) {

		if(firstMove){
			firstMove = false;
		}

		if(isBlack)
		{
			if((x == -1 || x == 1) && y == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if((x == -1 || x == 1) && y == -1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	//	public boolean pathCheck(int selectedX, int selectedY, boolean isBlack){
	//		if(isBlack)
	//			return ChessFrame.pieces[selectedX][selectedY+1] == null;
	//		else
	//			return ChessFrame.pieces[selectedX][selectedY-1] == null;
	//	}
}
