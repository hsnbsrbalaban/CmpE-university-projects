package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Pieces.Bishop;
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Queen;
import Pieces.Rook;

@SuppressWarnings("serial")
public class MyFrame extends JFrame implements MouseListener, ActionListener{

	Color turnCheck = Color.white;
	Color selectedColor;

	public static final int SQUARE_WIDTH = 50;

	public static final int BOARD_MARGIN = 50;

	int selectedSquareX = -1;
	int selectedSquareY = -1;
	int targetedSquareX = -1;
	int targetedSquareY = -1;

	public Piece pieces[][] = new Piece[8][8];

	Stack<UndoMove> undoStack = new Stack<UndoMove>();

	String threats[] = new String[16];

	JPanel buttons = new JPanel();
	JButton undoButton = new JButton("Undo");
	JButton saveButton = new JButton("Save");

	public MyFrame(){

		undoButton.setBounds(BOARD_MARGIN+8*SQUARE_WIDTH, BOARD_MARGIN-43, 40, 20);
		saveButton.setBounds(BOARD_MARGIN-40, BOARD_MARGIN-43, 40, 20);
		buttons.setBounds(0, 0, 300, 300);

		undoButton.addActionListener(this);
		saveButton.addActionListener(this);

		buttons.setLayout(null);
		buttons.add(saveButton);
		buttons.add(undoButton);
		add(buttons);

		initializeChessBoard();
		setTitle("Chess Game");
		setSize(SQUARE_WIDTH*8+BOARD_MARGIN*2, SQUARE_WIDTH*8+BOARD_MARGIN*2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addMouseListener(this);

	}

	public void initializeChessBoard() {
		for(int i = 0; i<8; i++)
		{
			for(int j = 0; j<8; j++)
			{
				if(j==0){
					if(i==0 || i==7)
						pieces[i][j] = new Rook(true);
					if(i==1 || i==6)
						pieces[i][j] = new Knight(true);
					if(i==2 || i==5)
						pieces[i][j] = new Bishop(true);
					if(i==3)
						pieces[i][j] = new Queen(true);
					if(i==4)
						pieces[i][j] = new King(true);
				}
				else if(j==1)
					pieces[i][j] = new Pawn(true);

				else if(j==6)
					pieces[i][j] = new Pawn(false);

				else if(j==7){
					if(i==0 || i==7)
						pieces[i][j] = new Rook(false);
					if(i==1 || i==6)
						pieces[i][j] = new Knight(false);
					if(i==2 || i==5)
						pieces[i][j] = new Bishop(false);
					if(i==3)
						pieces[i][j] = new Queen(false);
					if(i==4)
						pieces[i][j] = new King(false);
				}
			}
		}
	}

	public void paint(Graphics g){
		super.paint(g);

		for(int i=0; i<2; i++){
			for(int j=0; j<8; j++){
				if(i==0){
					g.drawString((char)('A'+j)+"", BOARD_MARGIN+(SQUARE_WIDTH*(j+1))-SQUARE_WIDTH/2, BOARD_MARGIN-5);
					g.drawString((char)('A'+j)+"", BOARD_MARGIN+(SQUARE_WIDTH*(j+1))-SQUARE_WIDTH/2, BOARD_MARGIN+SQUARE_WIDTH*8+15);
				}
				else{
					g.drawString(8-j+"", BOARD_MARGIN-15, BOARD_MARGIN+(SQUARE_WIDTH*(j+1))-SQUARE_WIDTH/2);
					g.drawString(8-j+"", BOARD_MARGIN+SQUARE_WIDTH*8+5, BOARD_MARGIN+(SQUARE_WIDTH*(j+1))-SQUARE_WIDTH/2);
				}
			}
		}

		//print the board's squares
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){

				if((i%2==0 && j%2==0)||(i%2==1 && j%2==1)){
					g.setColor(Color.PINK);
					g.fillRect(BOARD_MARGIN+SQUARE_WIDTH*j, 
							BOARD_MARGIN+SQUARE_WIDTH*i, SQUARE_WIDTH, SQUARE_WIDTH);

				}
				else{
					g.setColor(Color.CYAN);
					g.fillRect(BOARD_MARGIN+SQUARE_WIDTH*j, 
							BOARD_MARGIN+SQUARE_WIDTH*i, SQUARE_WIDTH, SQUARE_WIDTH);
				}
			}
		}
		//print the pieces
		for(int i = 0; i<8; i++)
		{
			for(int j = 0; j<8; j++)
			{
				if(pieces[i][j] != null)
				{
					pieces[i][j].drawYourself(g, i*SQUARE_WIDTH+BOARD_MARGIN, 
							j*SQUARE_WIDTH+BOARD_MARGIN, SQUARE_WIDTH);
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		//		System.out.println(selectedSquareX + " " + selectedSquareY);
	}

	public void mousePressed(MouseEvent e) {
		//calculate which square is selected
		selectedSquareX = (e.getX()-BOARD_MARGIN)/SQUARE_WIDTH;
		selectedSquareY = (e.getY()-BOARD_MARGIN)/SQUARE_WIDTH;
		//if mouse pressed inside the board
		if(selectedSquareX >= 0 && selectedSquareY >= 0 && selectedSquareX < 8 && selectedSquareY < 8){
			//if the square is not empty
			if(pieces[selectedSquareX][selectedSquareY] != null) {
				selectedColor = pieces[selectedSquareX][selectedSquareY].getColor();
			}
		}

	}

	public void mouseReleased(MouseEvent e) {
		//calculate which square is targeted
		targetedSquareX = (e.getX()-BOARD_MARGIN)/SQUARE_WIDTH;
		targetedSquareY = (e.getY()-BOARD_MARGIN)/SQUARE_WIDTH;

		char fromX = (char) ('a'+selectedSquareX);
		int fromY = 8-selectedSquareY;
		char toX = (char) ('a'+targetedSquareX);
		int toY = 8-targetedSquareY;

		String from = (char)fromX+""+fromY;
		String to = (char)toX+""+toY;

		if(selectedSquareX>=0 && selectedSquareY>=0 && selectedSquareX<8 && selectedSquareY<8 &&
				targetedSquareX>=0 && targetedSquareY>=0 && targetedSquareX<8 && targetedSquareY<8){
			//if the move is castling 
			if(pieces[selectedSquareX][selectedSquareY] instanceof King && 
					pieces[targetedSquareX][targetedSquareY] instanceof Rook)
			{

				if(targetedSquareX==0)
					castling(false);
				else 
					castling(true);
			}
			//			else if(pieces[selectedSquareX][selectedSquareY] instanceof Pawn){
			//				if(pieces[selectedSquareX][selectedSquareY].getColor() == Color.white && targetedSquareY==0 ||
			//						pieces[selectedSquareX][selectedSquareY].getColor() == Color.black && targetedSquareY==7){
			//					promotion(selectedSquareX, selectedSquareY, pieces[selectedSquareX][selectedSquareY].getColor());
			//				}
			//			}
			else
				move(from,to);
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == undoButton){
			undo();
		}
		if(e.getSource() == saveButton){
			save("game.txt");
		}

	}

	public boolean move(String from, String to){

		selectedSquareX = from.charAt(0)-'a';
		selectedSquareY = 8-Integer.parseInt(from.substring(1));
		targetedSquareX = to.charAt(0)-'a';
		targetedSquareY = 8-Integer.parseInt(to.substring(1));

		int diffX = targetedSquareX - selectedSquareX;
		int diffY = targetedSquareY - selectedSquareY;

		//if the given coordinates are inside the board
		if(selectedSquareX>=0 && selectedSquareY>=0 && selectedSquareX<8 && selectedSquareY<8 &&
				targetedSquareX>=0 && targetedSquareY>=0 && targetedSquareX<8 && targetedSquareY<8){
			//if the selected piece is not null
			if(pieces[selectedSquareX][selectedSquareY] != null){
				Color fromColor = pieces[selectedSquareX][selectedSquareY].getColor();
				//if the turnCheck is true
				if(turnCheck == fromColor){
					//if there is a piece in targeted coordinates
					if(pieces[targetedSquareX][targetedSquareY] != null){
						Color toColor = pieces[targetedSquareX][targetedSquareY].getColor();
						//if toColor and fromColor are different
						if(fromColor != toColor){
							//if the selected piece can move to targeted piece and capture the piece inside
							if(pieces[selectedSquareX][selectedSquareY].canCapture(diffX, diffY) && 
									pathCheck(selectedSquareX, selectedSquareY, targetedSquareX, targetedSquareY)){
								//if it is a promotion
								if(pieces[selectedSquareX][selectedSquareY] instanceof Pawn && (targetedSquareY == 0 || targetedSquareY == 7)){
									boolean c = (fromColor==Color.white ? false : true);
									undoStack.push(new UndoMove(true, pieces[targetedSquareX][targetedSquareY], selectedSquareX+""+selectedSquareY,
											targetedSquareX+""+targetedSquareY));
									pieces[targetedSquareX][targetedSquareY] = new Queen(c);
									pieces[selectedSquareX][selectedSquareY] = null;

								}
								else{
									undoStack.push(new UndoMove(true, selectedSquareX+""+selectedSquareY, targetedSquareX+""+targetedSquareY,
											pieces[targetedSquareX][targetedSquareY]));

									pieces[targetedSquareX][targetedSquareY] = pieces[selectedSquareX][selectedSquareY];
									pieces[selectedSquareX][selectedSquareY] = null;
								}
								if(isInCheck()){
									//									System.out.println("geçersiz hamle! şah tehdit altında");
									undo();
									if(turnCheck==Color.white){
										turnCheck = Color.black;
										//										System.out.println("sıra hala siyahta");
									}
									else{
										turnCheck = Color.white;
										//										System.out.println("sıra hala beyazda");
									}
								}
								else{
									if(turnCheck==Color.white){
										turnCheck = Color.black;
									}
									else{
										turnCheck = Color.white;
									}
									if(isInCheck()){
										if(isCheckmate()){
											JFrame gameOver = new JFrame();
											JLabel label = new JLabel();
											gameOver.setTitle("END OF THE GAME!");
											gameOver.setSize(1000, 200);
											gameOver.setDefaultCloseOperation(EXIT_ON_CLOSE);
											label.setText("GAME OVER!");
											gameOver.add(label);
											gameOver.setVisible(true);
										}
									}
									repaint();
									return true;
								}
							}
						}
					}
					//if the targeted piece is empty
					else{
						if(pieces[selectedSquareX][selectedSquareY].canMove(diffX, diffY) && 
								pathCheck(selectedSquareX, selectedSquareY, targetedSquareX, targetedSquareY)){
							//if it is a promotion
							if(pieces[selectedSquareX][selectedSquareY] instanceof Pawn && (targetedSquareY == 0 || targetedSquareY == 7)){
								boolean c = (fromColor==Color.white ? false : true);
								undoStack.push(new UndoMove(true, null, selectedSquareX+""+selectedSquareY,
										targetedSquareX+""+targetedSquareY));
								pieces[targetedSquareX][targetedSquareY] = new Queen(c);
								pieces[selectedSquareX][selectedSquareY] = null;

							}
							else{

								undoStack.push(new UndoMove(true, selectedSquareX+""+selectedSquareY, targetedSquareX+""+targetedSquareY));

								pieces[targetedSquareX][targetedSquareY] = pieces[selectedSquareX][selectedSquareY];
								pieces[selectedSquareX][selectedSquareY] = null;
							}

							if(isInCheck()){
								undo();
								if(turnCheck==Color.white){
									turnCheck = Color.black;
								}
								else{
									turnCheck = Color.white;
									if(isCheckmate()){
										JFrame gameOver = new JFrame();
										JLabel label = new JLabel();
										gameOver.setTitle("END OF THE GAME!");
										gameOver.setSize(1000, 200);
										gameOver.setDefaultCloseOperation(EXIT_ON_CLOSE);
										label.setText("GAME OVER!");
										gameOver.add(label);
										gameOver.setVisible(true);
									}
								}

							}
							else{
								if(turnCheck==Color.white){
									turnCheck = Color.black;
								}
								else{
									turnCheck = Color.white;
								}
								if(isInCheck()){
									if(isCheckmate()){
										JFrame gameOver = new JFrame();
										JLabel label = new JLabel();
										gameOver.setTitle("END OF THE GAME!");
										gameOver.setSize(1000, 200);
										gameOver.setDefaultCloseOperation(EXIT_ON_CLOSE);
										label.setText("GAME OVER!");
										gameOver.add(label);
										gameOver.setVisible(true);
									}
								}
								repaint();
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public void save(String fileName) {
		PrintStream p1 = null;
		try {
			p1 = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if(turnCheck == Color.white)
			p1.println("white");
		else
			p1.println("black");

		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if(pieces[j][i] != null){
					if(pieces[j][i].getColor().equals(Color.white)){
						p1.print("white-");
					}
					else
						p1.print("black-");


					//					if(pieces[j][i] instanceof Rook)
					//						p1.print("rook-");
					//					else if(pieces[j][i] instanceof Pawn)
					//						p1.print("pawn-");
					//					else if(pieces[j][i] instanceof Knight)
					//						p1.print("knight-");
					//					else if(pieces[j][i] instanceof Bishop)
					//						p1.print("bishop-");
					//					else if(pieces[j][i] instanceof King)
					//						p1.print("king-");
					//					else if(pieces[j][i] instanceof Queen)
					//						p1.print("queen-");

					p1.print(pieceKind(j,i)+"-");

					char one = (char)('a'+j);
					int two = 8-i;

					p1.print((char)one+""+two);
					p1.println();
				}

			}
		}

	}

	public String at(String pos){
		String pieceName = "";

		int x = pos.charAt(0)-'a';

		int y = 8-Integer.parseInt(pos.substring(1));

		if(pieces[x][y]!=null){
			if(pieces[x][y].getColor() == Color.white)
				pieceName += "white-";
			else
				pieceName += "black-";

			pieceName += pieceKind(x,y);
		}

		return pieceName;
	}

	public String pieceKind(int x, int y){
		if(pieces[x][y] instanceof Pawn)
			return "pawn";
		else if(pieces[x][y] instanceof King)
			return "king";
		else if(pieces[x][y] instanceof Bishop)
			return "bishop";
		else if(pieces[x][y] instanceof Knight)
			return "knight";
		else if(pieces[x][y] instanceof Rook)
			return "rook";
		else if(pieces[x][y] instanceof Queen)
			return "queen";
		else 
			return "";
	}

	public boolean castling(boolean isKingSide) {
		if(turnCheck == Color.black && isKingSide){
			if(pieces[5][0]==null && pieces[6][0]==null){
				pieces[6][0]=pieces[4][0];
				pieces[5][0]=pieces[7][0];
				pieces[4][0]=null;
				pieces[7][0]=null;

				undoStack.push(new UndoMove(true, true ,pieces[5][0].isBlack));

				turnCheck = Color.white;
			}
		}
		else if(turnCheck == Color.black && !isKingSide){
			if(pieces[1][0]==null && pieces[2][0]==null && pieces[3][0]==null){
				pieces[2][0]=pieces[4][0];
				pieces[3][0]=pieces[0][0];
				pieces[4][0]=null;
				pieces[0][0]=null;

				undoStack.push(new UndoMove(true, false ,pieces[3][0].isBlack));

				turnCheck = Color.white;
			}
		}
		else if(turnCheck == Color.white && isKingSide){
			if(pieces[5][7]==null && pieces[6][7]==null){
				pieces[6][7]=pieces[4][7];
				pieces[5][7]=pieces[7][7];
				pieces[4][7]=null;
				pieces[7][7]=null;

				undoStack.push(new UndoMove(true, true ,pieces[5][7].isBlack));

				turnCheck = Color.black;
			}
		}
		else{
			if(pieces[1][7]==null && pieces[2][7]==null && pieces[3][7]==null){
				pieces[2][7]=pieces[4][7];
				pieces[3][7]=pieces[0][7];
				pieces[4][7]=null;
				pieces[0][7]=null;

				undoStack.push(new UndoMove(true, false ,pieces[3][7].isBlack));

				turnCheck = Color.black;
			}

		}
		repaint();
		return false;
	}

	public boolean pathCheck(int x1, int y1, int x2, int y2){
		//if the piece is a Pawn
		if(pieces[x1][y1] instanceof Pawn){
			if(pieces[x1][y1].isBlack){
				if(Math.abs(y2-y1)==2)
					return pieces[x1][y1+1] == null;
				else
					return true;
			}
			else{
				if(Math.abs(y2-y1)==2)
					return pieces[x1][y1-1] == null;
				else
					return true;
			}
		}
		//if the piece is a Bishop
		else if(pieces[x1][y1] instanceof Bishop){
			if(x2-x1>0 && y2-y1>0){
				for(int i=1; i<x2-x1; i++){
					if(pieces[x1+i][y1+i] != null){
						return false;
					}
				}
			}
			else if(x2-x1>0 && y2-y1<0){
				for(int i=1; i<x2-x1; i++){
					if(pieces[x1+i][y1-i] != null){
						return false;
					}
				}
			}
			else if(x2-x1<0 && y2-y1>0){
				for(int i=1; i<y2-y1; i++){
					if(pieces[x1-i][y1+i] != null){
						return false;
					}
				}
			}
			else{
				for(int i=1; i<Math.abs(x2-x1); i++){
					if(pieces[x1-i][y1-i] != null){
						return false;
					}
				}
			}
			return true;
		}
		//if the piece is a Rook
		else if(pieces[x1][y1] instanceof Rook){
			if(y2-y1==0){
				int min = Math.min(x1, x2);
				int max = Math.max(x1, x2);


				for(int i=min+1; i<max; i++){
					if(pieces[i][y1] != null){
						return false;
					}
				}
			}
			else if(x2-x1==0){
				int min = Math.min(y1, y2);
				int max = Math.max(y1, y2);

				for(int i=min+1; i<max; i++){
					if(pieces[x1][i] != null){
						return false;
					}
				}
			}
			return true;
		}
		//if the piece is a Queen
		if(pieces[x1][y1] instanceof Queen){
			if(x2-x1>0 && y2-y1>0){
				for(int i=1; i<x2-x1; i++){
					if(pieces[x1+i][y1+i] != null){
						return false;
					}
				}
			}
			else if(x2-x1>0 && y2-y1<0){
				for(int i=1; i<x2-x1; i++){
					if(pieces[x1+i][y1-i] != null){
						return false;
					}
				}
			}
			else if(x2-x1<0 && y2-y1>0){
				for(int i=1; i<y2-y1; i++){
					if(pieces[x1-i][y1+i] != null){
						return false;
					}
				}
			}
			else if(x2-x1<0 && y2-y1<0){
				for(int i=1; i<Math.abs(x2-x1); i++){
					if(pieces[x1-i][y1-i] != null){
						return false;
					}
				}
			}

			if(y2-y1==0){
				int min = Math.min(x1, x2);
				int max = Math.max(x1, x2);


				for(int i=min+1; i<max; i++){
					if(pieces[i][y1] != null){
						return false;
					}
				}
			}
			else if(x2-x1==0){
				int min = Math.min(y1, y2);
				int max = Math.max(y1, y2);

				for(int i=min+1; i<max; i++){
					if(pieces[x1][i] != null){
						return false;
					}
				}
			}
			return true;
		}

		return true;
	}

	public boolean isInCheck(){
		int toX = 0;
		int toY = 0;
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if(pieces[j][i] instanceof King && pieces[j][i].getColor()==turnCheck){
					toX = j;
					toY = i;
				}
			}
		}
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if(pieces[j][i] != null && pieces[j][i].getColor() != turnCheck){
					if(pieces[j][i].canMove(toX-j, toY-i) && pathCheck(j,i,toX,toY)){
						return true;
					}
				}
			}
		}
		return false;
	}

	public void undo(){
		if(!(undoStack.isEmpty())){
			UndoMove lastMove = undoStack.pop(); 

			if(lastMove.isCastle){
				if(lastMove.isKingSide && lastMove.selectedIsBlack){
					pieces[4][0] = pieces[6][0];
					pieces[7][0] = pieces[5][0];
					pieces[6][0] = null;
					pieces[5][0] = null;

				}
				else if(!lastMove.isKingSide && lastMove.selectedIsBlack){
					pieces[4][0]=pieces[2][0];
					pieces[0][0]=pieces[3][0];
					pieces[2][0]=null;
					pieces[3][0]=null;

				}
				else if(lastMove.isKingSide && !lastMove.selectedIsBlack){
					pieces[4][7]=pieces[6][7];
					pieces[7][7]=pieces[5][7];
					pieces[5][7]=null;
					pieces[6][7]=null;

				}
				else{
					pieces[4][7]=pieces[2][7];
					pieces[0][7]=pieces[3][7];
					pieces[2][7]=null;
					pieces[3][7]=null;
				}
			}
			else if(lastMove.isMove && lastMove.captured == null){
				int fromX = Integer.parseInt(lastMove.from.substring(0, 1));
				int fromY = Integer.parseInt(lastMove.from.substring(1));
				int toX = Integer.parseInt(lastMove.to.substring(0, 1));
				int toY = Integer.parseInt(lastMove.to.substring(1));

				pieces[fromX][fromY] = pieces[toX][toY];
				pieces[toX][toY] = null;

				if(pieces[fromX][fromY] instanceof Pawn && (fromY==1 || fromY==6)){
					pieces[fromX][fromY].firstMove = true;
				}

			}
			else if(lastMove.isMove && lastMove.captured != null){
				int fromX = Integer.parseInt(lastMove.from.substring(0, 1));
				int fromY = Integer.parseInt(lastMove.from.substring(1));
				int toX = Integer.parseInt(lastMove.to.substring(0, 1));
				int toY = Integer.parseInt(lastMove.to.substring(1));

				pieces[fromX][fromY] = pieces[toX][toY];
				pieces[toX][toY] = lastMove.captured;

				if(pieces[fromX][fromY] instanceof Pawn && (fromY==1 || fromY==6)){
					pieces[fromX][fromY].firstMove = true;
				}

			}
			else if(lastMove.isPromotion && lastMove.capturedByPawn == null){
				int fromX = Integer.parseInt(lastMove.fromPawn.substring(0, 1));
				int fromY = Integer.parseInt(lastMove.fromPawn.substring(1));
				int toX = Integer.parseInt(lastMove.toPawn.substring(0, 1));
				int toY = Integer.parseInt(lastMove.toPawn.substring(1));
				boolean c = (pieces[toX][toY].getColor() == Color.white ? false : true);
				pieces[fromX][fromY] = new Pawn(c);
				pieces[toX][toY] = null;
			}
			else if(lastMove.isPromotion && lastMove.capturedByPawn != null){
				int fromX = Integer.parseInt(lastMove.fromPawn.substring(0, 1));
				int fromY = Integer.parseInt(lastMove.fromPawn.substring(1));
				int toX = Integer.parseInt(lastMove.toPawn.substring(0, 1));
				int toY = Integer.parseInt(lastMove.toPawn.substring(1));
				boolean c = (pieces[toX][toY].getColor() == Color.white ? false : true);
				pieces[fromX][fromY] = new Pawn(c);
				pieces[toX][toY] = lastMove.capturedByPawn;
			}
			if(turnCheck==Color.white) turnCheck = Color.black;
			else turnCheck = Color.white;
		}
		else{
			System.out.println("No moves for undo");
		}
		repaint();
	}

	public MyFrame load(String fileName){
		MyFrame newFrame = new MyFrame();
		Scanner c1 = null;
		try {
			c1 = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String turn = c1.next();
		System.out.println(turn);
		if(turn.equals("white"))
			newFrame.turnCheck = Color.white;
		else
			newFrame.turnCheck = Color.black;

		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				newFrame.pieces[i][j] = null;
			}
		}

		while(c1.hasNext()){
			String piece = c1.next();
			String color = "";
			String pieceType = "";
			String coord = "";
			int count = -1;

			for(int i=0; i<piece.length(); i++){
				if(piece.charAt(i)=='-'){
					count++;
					i++;
				}
				if(count==-1){
					color += (char)piece.charAt(i)+"";
				}
				else if(count==0){
					pieceType += (char)piece.charAt(i)+"";
				}
				else if(count==1){
					coord += (char)piece.charAt(i)+"";
				}
				
			}
			System.out.println(color+" "+pieceType+" "+coord);
			int coordX = coord.charAt(0)-'a';
			int coordY = 8-Integer.parseInt(coord.substring(1));

			boolean cur;
			if(color.equals("white"))
				cur = false;
			else
				cur = true;

			if(pieceType.equals("pawn"))
				newFrame.pieces[coordX][coordY] = new Pawn(cur);
			else if(pieceType.equals("queen"))
				newFrame.pieces[coordX][coordY] = new Queen(cur);
			else if(pieceType.equals("knight"))
				newFrame.pieces[coordX][coordY] = new Knight(cur);
			else if(pieceType.equals("bishop"))
				newFrame.pieces[coordX][coordY] = new Bishop(cur);
			else if(pieceType.equals("rook"))
				newFrame.pieces[coordX][coordY] = new Rook(cur);
			else if(pieceType.equals("king"))
				newFrame.pieces[coordX][coordY] = new King(cur);

		}
		
		c1.close();
		return newFrame;
	}

	public boolean isCheckmate(){
		int kingPosX = -1, kingPosY = -1, countThreats = 0;

		//King's position
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if(pieces[j][i] instanceof King && pieces[j][i].getColor()==turnCheck){
					kingPosX = j;
					kingPosY = i;
				}
			}
		}
		//How many threats
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if(pieces[j][i] != null && pieces[j][i].getColor() != turnCheck){
					if(pieces[j][i].canMove(kingPosX-j, kingPosY-i) && pathCheck(j,i,kingPosX,kingPosY)){
						threats[countThreats] = j+""+i;
						countThreats++;
					}
				}
			}
		}

		if (countThreats==0) {
			return false;
		}
		else if (countThreats==1) {
			int threatX = Integer.parseInt(threats[0].substring(0,1));
			int threatY = Integer.parseInt(threats[0].substring(1));

			System.out.println("can threat be crossed : "+canThreatBeCrossed(kingPosX, kingPosY, threatX, threatY)+"\ncan king run : "+
					canKingRun(kingPosX, kingPosY)+"\ntcanThreatBeEaten : "+threatCanBeEaten(threatX, threatY, kingPosX, kingPosY));


			if(canThreatBeCrossed(kingPosX, kingPosY, threatX, threatY) || canKingRun(kingPosX, kingPosY) ||
					threatCanBeEaten(threatX, threatY, kingPosX, kingPosY)){
				if(canKingRun(kingPosX, kingPosY) && pieces[threatX][threatY] instanceof Queen && (kingPosX-threatX==0 ||
						kingPosY-threatY==0 || Math.abs(kingPosX-threatX)==Math.abs(kingPosY-threatY)))
					return true;
				else if(canKingRun(kingPosX, kingPosY) && pieces[threatX][threatY] instanceof Rook && (kingPosX-threatX==0 ||
						kingPosY-threatY==0))
					return true;
				else if(canKingRun(kingPosX, kingPosY) && pieces[threatX][threatY] instanceof Bishop && 
						(Math.abs(kingPosX-threatX)==Math.abs(kingPosY-threatY)))
					return true;


				return false;
			}
		}
		else if (countThreats>1){
			if(canKingRun(kingPosX, kingPosY))
				return false;
		}

		return true;
	}

	public boolean canKingRun(int kingPosX, int kingPosY){
		if(kingPosX == 0 && kingPosY == 0){
			if (!isThreaten(kingPosX+1, kingPosY, turnCheck) && pieces[kingPosX+1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY+1, turnCheck) && pieces[kingPosX][kingPosY+1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY+1, turnCheck) && pieces[kingPosX+1][kingPosY+1] == null) {
				return true;
			}

		}
		else if(kingPosX == 7 && kingPosY == 0){
			if (!isThreaten(kingPosX-1, kingPosY, turnCheck) && pieces[kingPosX-1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX-1, kingPosY+1, turnCheck) && pieces[kingPosX-1][kingPosY+1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY+1, turnCheck) && pieces[kingPosX][kingPosY+1] == null) {
				return true;
			}

		}
		else if(kingPosX == 0 && kingPosY == 7){
			if (!isThreaten(kingPosX, kingPosY-1, turnCheck) && pieces[kingPosX][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY-1, turnCheck) && pieces[kingPosX+1][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY, turnCheck) && pieces[kingPosX+1][kingPosY] == null) {
				return true;
			}

		}
		else if(kingPosX == 7 && kingPosY == 7){
			if (!isThreaten(kingPosX-1, kingPosY-1, turnCheck) && pieces[kingPosX-1][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY-1, turnCheck) && pieces[kingPosX][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX-1, kingPosY, turnCheck) && pieces[kingPosX-1][kingPosY] == null) {
				return true;
			}

		}
		else if(kingPosX>0 && kingPosY==0 && kingPosX<7){
			if (!isThreaten(kingPosX-1, kingPosY, turnCheck) && pieces[kingPosX-1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY, turnCheck) && pieces[kingPosX+1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX-1, kingPosY+1, turnCheck) && pieces[kingPosX-1][kingPosY+1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY+1, turnCheck) && pieces[kingPosX][kingPosY+1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY+1, turnCheck) && pieces[kingPosX+1][kingPosY+1] == null) {
				return true;
			}

		}
		else if(kingPosY>0 && kingPosX==0 && kingPosY<7){
			if (!isThreaten(kingPosX, kingPosY-1, turnCheck) && pieces[kingPosX][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY-1, turnCheck) && pieces[kingPosX+1][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY, turnCheck) && pieces[kingPosX+1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY+1, turnCheck) && pieces[kingPosX][kingPosY+1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY+1, turnCheck) && pieces[kingPosX+1][kingPosY+1] == null) {
				return true;
			}

		}
		else if(kingPosX>0 && kingPosY==7 && kingPosX<7){
			if (!isThreaten(kingPosX-1, kingPosY-1, turnCheck) && pieces[kingPosX-1][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY-1, turnCheck) && pieces[kingPosX][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY-1, turnCheck) && pieces[kingPosX+1][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX-1, kingPosY, turnCheck) && pieces[kingPosX-1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY, turnCheck) && pieces[kingPosX+1][kingPosY] == null) {
				return true;
			}

		}
		else if(kingPosY>0 && kingPosX==7 && kingPosY<7){
			if (!isThreaten(kingPosX-1, kingPosY-1, turnCheck) && pieces[kingPosX-1][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY-1, turnCheck) && pieces[kingPosX][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX-1, kingPosY, turnCheck) && pieces[kingPosX-1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX-1, kingPosY+1, turnCheck) && pieces[kingPosX-1][kingPosY+1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY+1, turnCheck) && pieces[kingPosX][kingPosY+1] == null) {
				return true;
			}

		}
		else{
			if (!isThreaten(kingPosX-1, kingPosY-1, turnCheck) && pieces[kingPosX-1][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY-1, turnCheck) && pieces[kingPosX][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY-1, turnCheck) && pieces[kingPosX+1][kingPosY-1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX-1, kingPosY, turnCheck) && pieces[kingPosX-1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY, turnCheck) && pieces[kingPosX+1][kingPosY] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX-1, kingPosY+1, turnCheck) && pieces[kingPosX-1][kingPosY+1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX, kingPosY+1, turnCheck) && pieces[kingPosX][kingPosY+1] == null) {
				return true;
			}
			else if (!isThreaten(kingPosX+1, kingPosY+1, turnCheck) && pieces[kingPosX+1][kingPosY+1] == null) {
				return true;
			}
		}
		return false;
	}

	public boolean isThreaten(int toX, int toY, Color c){

		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				if(pieces[i][j] != null){
					if (pieces[i][j].canMove(toX-i, toY-j) && pathCheck(i,j,toX,toY) && pieces[i][j].getColor() != c) {
						return true;
					}
				}


			}
		}
		return false;
	}

	public boolean threatCanBeEaten(int threatX, int threatY, int kingX, int kingY){

		Color c = null;
		if (turnCheck == Color.white) {
			c = Color.black;
		}
		else
			c = Color.white;

		if(pieces[kingX][kingY].canCapture(threatX-kingX, threatY-kingY)){
			if(isThreaten(threatX, threatY, turnCheck))
				return false;
		}


		if (isThreaten(threatX, threatY, c)) {
			return true;
		}
		else
			return false;
	}

	public boolean canThreatBeCrossed(int kingX, int kingY, int threatX, int threatY){

		Color c = null;
		if (pieces[kingX][kingY].getColor()==Color.black) {
			c = Color.white;
		}
		else{
			c = Color.black;
		}


		if (kingX>threatX && kingY>threatY) {
			for (int i=1; i<kingX - threatX; i++) {
				if (isThreaten(threatX+i, threatY+i, c)) {
					return true;
				}
			}
		}
		else if (kingX==threatX && kingY>threatY) {
			for (int i=1; i<kingY - threatY; i++) {
				if(isThreaten(threatX, threatY+i, c)){
					return true;
				}
			}
		}
		else if (kingX<threatX && kingY>threatY) {
			for (int i=1; i<kingY - threatY; i++) {
				if (isThreaten(kingX+i, kingY-i, c)) {
					return true;
				}
			}
		}
		else if (kingX>threatX && kingY==threatY) {
			for (int i=1; i<kingX - threatX; i++) {
				if (isThreaten(threatX+i, threatY, c)) {
					return true;
				}
			}
		}
		else if (kingX<threatX && kingY==threatY) {
			for (int i=1; i<threatX - kingX; i++) {
				if (isThreaten(kingX+i, kingY, c)) {
					return true;
				}
			}
		}
		else if (kingX>threatX && kingY<threatY) {
			for (int i=1; i<kingX - threatX; i++) {
				if (isThreaten(kingX-i, kingY+i, c)) {
					return true;
				}
			}
		}
		else if (kingX==threatX && kingY<threatY) {
			for (int i=1; i<threatY - kingY; i++) {
				if (isThreaten(kingX, kingY+i, c)) {
					return true;
				}
			}
		}
		else{
			for (int i=1; i<threatX - kingX; i++) {
				if (isThreaten(kingX+i, kingY+i, c)) {
					return true;
				}
			}
		}
		return false;
	}

}