package Game;

public class UndoMove {
	boolean isMove;
	String from;
	String to;
	Piece captured;
	
	boolean isCastle;
	boolean isKingSide;
	boolean selectedIsBlack;
	
	boolean isPromotion;
	String fromPawn;
	String toPawn;
	Piece capturedByPawn;

	public UndoMove(boolean isMove, String from, String to){
		this.isMove = isMove;
		this.from = from;
		this.to = to;
		
		captured = null;
		isCastle = false;
		isPromotion = false;
	}
	public UndoMove(boolean isMove, String from, String to, Piece captured){
		this.isMove = isMove;
		this.from = from;
		this.to = to;
		this.captured = captured;
		
		isCastle = false;
		isPromotion = false;
	}
	public UndoMove(boolean isCastle, boolean isKingSide, boolean selectedIsBlack){
		this.isCastle = isCastle;
		this.isKingSide = isKingSide;
		this.selectedIsBlack = selectedIsBlack;
		
		isMove = false;
		isPromotion = false;
		
	}
	public UndoMove( boolean isPromotion, Piece capturedByPawn, String fromPawn, String toPawn){
		this.capturedByPawn = capturedByPawn;
		this.isPromotion = isPromotion;
		this.fromPawn = fromPawn;
		this.toPawn = toPawn;
		
		isCastle = false;
		isMove = false;
	}
	
	
	
	
}
