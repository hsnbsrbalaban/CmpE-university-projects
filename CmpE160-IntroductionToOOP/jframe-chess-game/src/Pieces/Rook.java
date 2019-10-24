package Pieces;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import Game.*;

//Kale
public class Rook extends Piece{

	public Rook(boolean isBlack) {
		super(isBlack);
	}
	public void drawYourself(Graphics g, int positionX, int positionY, int squareWidth) {
		if(isBlack) {
			ImageIcon basri = new ImageIcon("BRook.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
		else {
			ImageIcon basri = new ImageIcon("WRook.png");
			g.drawImage(basri.getImage(), positionX, positionY, squareWidth, squareWidth, null);
		}
	}
	public boolean canMove(int x, int y) {
		return (x==0 || y==0);
	}
	public boolean canCapture(int x, int y) {
		return canMove(x,y);
	}
//	public boolean pathCheck(int x, int y, int selectedX, int selectedY){	
//		if(y==0){
//			int min = Math.min(selectedX, selectedX+x);
//			int max = Math.max(selectedX, selectedX+x);
//
//
//			for(int i=min+1; i<max; i++){
//				if(MyFrame.pieces[i][selectedY] != null){
//					return false;
//				}
//			}
//		}
//		else if(x==0){
//			int min = Math.min(selectedY, selectedY+y);
//			int max = Math.max(selectedY, selectedY+y);
//
//			for(int i=min+1; i<max; i++){
//				if(MyFrame.pieces[selectedX][i] != null){
//					return false;
//				}
//			}
//		}
//		return true;
//	}

}
