package PlayGameSystem;

import java.util.Objects;

public class PlayGameSystem {
    private String gametype = null;
    private String player;
    private Chess chess;

    public PlayGameSystem(){
        this.player = "black";
    }
    public void startNewGame(String gametype,int boardsize){
        ChessFactory creatF = null;
        if(Objects.equals(gametype, "围棋")){
            creatF = new WeiqiFactory();
        }else if(Objects.equals(gametype, "五子棋")){
            creatF = new GomokuFactory();
        }
        if (creatF != null) {
            this.chess = creatF.factory(boardsize);
        }else {
            System.out.println("Error!");
        }
        this.gametype = gametype;
        this.printGame();
    }
    public void setChess(Chess chess){
        this.chess = chess;
        this.gametype =chess.getGametye();
        this.printGame();
    }

    public boolean move(int x,int y){//放置棋子。
        return chess.move(x,y);
    }
    public boolean isOver(){//是否结束。
        return chess.isOver(this.player);
    }
    public String getWinner(){//获得胜利者；
        return chess.getWinner();
    }
    public Chess getChess(){
        return this.chess;
    }
    public void printGame(){
        this.player = chess.getChessplayer();
//        System.out.println("正在进行"+gametype+" ，第"+(chess.getPlayersteps()+1)+"手"+",执棋手"+chess.getChessplayer());
        this.chess.printABoard();
    }
    public void restart(){
        chess.initializeBoard();
//        printGame();
    }
    public String getGametype(){
        return this.gametype;

    }

    public String Surrend() {
        String w;
        if (Objects.equals(this.player, "black")){
            w = "white";
        }else {
            w = "black";
        }
        return w;
    }

    public void changePlayer() {
        this.chess.changePlayer();

    }
    public String getPlayer() {
        return this.chess.getChessplayer();

    }

    public int getPlayersteps() {
        return chess.getPlayersteps();
    }
}
