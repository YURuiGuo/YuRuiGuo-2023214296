package PlayGameSystem;

//抽象产品
public interface Chess {
    //构造过程中要求用户传入棋盘大小，构造时直接根据该大小初始化棋盘
    //三个基本方法：开始游戏，功能：控制游戏流程。
    //Regular：判断落子是否合法
    //JudgeWinner：终局判断。
    public abstract void initializeBoard();
    public abstract void printABoard();
    public abstract boolean move(int x,int y);
    public abstract boolean isOver(String player);

    void changePlayer();

    public abstract String getWinner();

    String getGametye();

    String getChessplayer();

    int getPlayersteps();

}
