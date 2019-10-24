package Pieces;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import Game.Piece;

//Fil
public class Bishop extends Piece{

	public Bishop(boolean isBlack) {
		super(isBlack);
	}
	public void drawYourself(Graphics g, int positionX, int positionY, int squareWidth) {
		if(isBlack)
		{
			ImageIcon basri = new ImageIcon("BBishop.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
		else
		{
			ImageIcon basri = new ImageIcon("WBishop.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
	}

	public boolean canMove(int x, int y) {
		return (Math.abs(x)==Math.abs(y));
	}

	public boolean canCapture(int x, int y) {
		return canMove(x,y);
	}
	
//	public boolean pathCheck(int x, int y, int selectedX, int selectedY){
//		
//		if(x>0 && y>0){
//			for(int i=1; i<x; i++){
//				if(ChessFrame.pieces[selectedX+i][selectedY+i] != null){
//					return false;
//				}
//			}
//		}
//		else if(x>0 && y<0){
//			for(int i=1; i<x; i++){
//				if(ChessFrame.pieces[selectedX+i][selectedY-i] != null){
//					return false;
//				}
//			}
//		}
//		else if(x<0 && y>0){
//			for(int i=1; i<y; i++){
//				if(ChessFrame.pieces[selectedX-i][selectedY+i] != null){
//					return false;
//				}
//			}
//		}
//		else{
//			for(int i=1; i<Math.abs(x); i++){
//				if(ChessFrame.pieces[selectedX-i][selectedY-i] != null){
//					return false;
//				}
//			}
//		}
//		return true;
//	}
}
