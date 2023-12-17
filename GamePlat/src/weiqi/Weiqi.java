package weiqi;

import PlayGameSystem.Chess;
//规定大于0为黑色
//小于0为白色
//等于0 未定色。
import java.io.Serializable;
import java.util.Objects;

public class Weiqi implements Chess, Serializable {
    private String chessplayer = "black";
    private int playersteps;
    public int[][] chessboard= null;//19*19的棋盘变为20*20棋盘，以便更好的处理边界情况
    private int lastIndex = -1;
    private int boardsize;
    private ChessChain ceChain;


    public Weiqi(int size){
        this.boardsize = size+2;
        this.chessboard= new int[this.boardsize][this.boardsize];
        this.ceChain = new ChessChain(this.boardsize);
        this.playersteps = 0;
        this.initializeBoard();
    }
    @Override
    public void initializeBoard() {//初始化所有参数
        //初始化棋盘： 8*8 变成10*10,以便处理边界情况
        for (int i = 0; i < boardsize; i++) {
            for (int j = 0; j < boardsize; j++) {
                chessboard[i][j] = 0;
            }
        }
        ceChain.initBoard();
        this.playersteps = 0;
        lastIndex = -1;
        chessplayer = "black";
    }
    @Override
    public void printABoard(){//打印棋盘
        System.out.print("  ");
        for(int i=1; i<=boardsize-2; i++){
            System.out.printf(" %2d",(i));
        }
        System.out.println(" ");
        for (int i = 1; i <= boardsize-2; i++) {
            System.out.printf("%2d",i);
            for (int j = 1; j <= boardsize-2; j++) {
                if (this.chessboard[i][j] == 1) {
                    System.out.print("| ●");
                }else if (this.chessboard[i][j] == -1){
                    System.out.print("| ⊙");
                }else {
                    System.out.print("| +");
                }
            }
            System.out.println("|");
        }
    }

    //下一步棋子
    @Override
    public boolean move(int x,int y){
        int f = Objects.equals(this.chessplayer, "black") ? 1 : -1;//黑色或者白色
        //检查合法？
        if(!Regular(x,y,f))
            return false;
        //合法，则落子，并更新气数
        chessboard[x][y] = f;
        this.lastIndex = ceChain.updatechainboard(x,y,this.chessboard);

        //检查提子并更新。
        ceChain.captureUpdate(x,y,this.chessboard);
        return true;//成功落子
    }

    //更改player
    @Override
    public void changePlayer(){
        this.playersteps++;
        this.chessplayer = Objects.equals(this.chessplayer, "white") ? "black" : "white";
    }

    //判断棋局是否结束;即,没有棋子可下.
    @Override
    public boolean isOver(String player) {
        int f = Objects.equals(player, "black") ? 1 : -1;//黑色或者白色
        //模拟下棋，看看还有没有空位
        for (int i = 1; i <= boardsize-2; i++) {
            for (int j = 1; j <= boardsize - 2; j++) {
                if(chessboard[i][j] == 0){
                    if(!Regular(i,j,f))
                        continue;
                    return false;//没有结束。

                }
            }
        }
        return true;
    }

    @Override
    public String getWinner() {
        return this.finalJudge();
    }


    //落子规则:
    public boolean Regular(int x,int y,int f){//在Point(x,y)点上落子,f为棋子的颜色。
        //基本的不落子的规则有 自杀、劫、（禁手（五子禁手、三三禁手、四四禁手等））
        //最简单的检查：越界
        if(x<1||x>boardsize-2||y<1||y>boardsize-2)
            return false;
        if(chessboard[x][y]!=0)
            return false;

        //自杀和劫的检查
        int[] a =ceChain.assumeUpdateboard(x,y,f,this.chessboard);
        if( a == null)
            return false;
        else {
            for (int j : a)
                if (j == this.lastIndex) {
                    return false;
                }
        }
        return true;
    }

    //终局判断
    public String finalJudge(){
        //终局判断
        //两步：计算石，计算领地
        int blackStones = 0;
        int whiteStones = 0;

        for (int i = 1; i <= boardsize-2; i++) {
            for (int j = 1; j <= boardsize-2; j++) {
                int stone = chessboard[i][j];
                if (stone > 0) {
                    blackStones++;
                } else if (stone < 0) {
                    whiteStones++;
                }
            }
        }

        int blackTerritory = 0;
        int whiteTerritory = 0;

        for (int i = 1; i <= boardsize-2; i++) {
            for (int j = 1; j <= boardsize-2; j++) {
                int stone = chessboard[i][j];

                if (stone == 0) {
                    int territoryColor = calculateTerritory(i, j);
                    if (territoryColor > 0) {
                        blackTerritory++;
                    } else if (territoryColor < 0) {
                        whiteTerritory++;
                    }
                }
            }
        }
        if (blackStones + blackTerritory > whiteStones + whiteTerritory) {
            return "black";
        } else if (blackStones + blackTerritory < whiteStones + whiteTerritory) {
            return "white";
        } else {
            return "draw";
        }
    }
    //用于终局判断中,计算领地的函数
    private int calculateTerritory(int i, int j) {//判断该点属于谁？
        //采用最简单的领地判断方法，判断一个点的四周的气链的Index，如果都是同一个Index则为该气链对应的颜色的领地。
        //遇到四个墙，则谁的也不是。
        int index = -1;//
        boolean f = false;//判断是否出现第二个链；
        //上方的邻居
        for(int t=j; t<=boardsize-2; t++){
            if(chessboard[i][t]!=0 && index == -1){//遇到远方邻居
                index= ceChain.getIndex(i,t);//获取到其的Index
                break;
            }else if(chessboard[i][t]!=0 && index != ceChain.getIndex(i,t)){
                return 0;//谁都不属于的领地
            }
        }
        for(int t=j; t>=1; t--){//下方的邻居
            if(chessboard[i][t]!=0&&index == -1){//遇到远方邻居
                index= ceChain.getIndex(i,t);//获取到其的Index
                break;
            }else if(chessboard[i][t]!=0 && index != ceChain.getIndex(i,t)){
                return 0;//谁都不属于的领地
            }
        }
        for(int t=i; t>=1; t--){//左方的邻居
            if(chessboard[t][j]!=0&&index == -1){//遇到远方邻居
                index= ceChain.getIndex(t,j);//获取到其的Index
                break;
            }else if(chessboard[t][j]!=0 && index != ceChain.getIndex(t,j)){
                return 0;//谁都不属于的领地
            }
        }
        for(int t=i; t<=boardsize-2; t++){//右方的邻居
            if(chessboard[t][j]!=0&&index == -1){//遇到远方邻居
                index= ceChain.getIndex(t,j);//获取到其的Index
                break;
            }else if(chessboard[t][j]!=0 && index != ceChain.getIndex(t,j)){
                return 0;//谁都不属于的领地
            }
        }
        if(index == -1)
            return 0;
        //返回index对应的棋子颜色。即可。，虽然这个判断胜负的方法不对，但是能用。
        return index;
    }
    @Override
    public String getGametye(){
        return "Weiqi";
    }
    @Override
    public String getChessplayer(){
        return chessplayer;
    }
    @Override
    public int getPlayersteps(){
        return playersteps;
    }

}
