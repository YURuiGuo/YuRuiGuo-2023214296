import Archive.ArchiveSystem;
import PlayGameSystem.Chess;
import PlayGameSystem.PlayGameSystem;
import UISysetem.UISystem;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class GamePlat {
    private PlayGameSystem game = null; //棋类子系统
    private ArchiveSystem archive= null; //就是存档子系统
    private UISystem ui = null;//UI子系统
    //private AiPlayerSystem aiplayer= null; //AI对战子模块，采用策略模式，可以选择高中低三种AI ^_^ (不将AI集成在Chess类中)
    boolean flag = false;//判断是否请求结束
    private String winner;

    public GamePlat(){
        this.ui = new UISystem();//启动UI子系统
        this.game = new PlayGameSystem();//启动棋类子系统
        this.archive = new ArchiveSystem();//启动存档子系统
        archive.getArchiveindex();
        ui.WelcomePage();
    }
    public boolean startgame(){
        Scanner sc = new Scanner(System.in);
        ui.FirstLevelPage();
        String modeltype = sc.next();
        if(Objects.equals(modeltype, "新游戏")){
            ui.ChooseGamePage();
            String gametype = sc.next();//String gametype = "Weiqi";
            int size = sc.nextInt();//
            if(!Objects.equals(gametype, "围棋") && !Objects.equals(gametype, "五子棋"))
                    return false;
            if(size<8 ||size>16)
                return false;
           this.game.startNewGame(gametype,size);
        }else if(Objects.equals(modeltype, "继续游戏")){
            if(archive.getArchiveindex()==0){
                return false;
            }
            System.out.println("请选择存档点:");
            archive.printAllState();
            int index = sc.nextInt();
            if(index<=0||index>=archive.getArchiveindex())
                return false;
            Chess che = archive.restoreMemento(index);
            game.setChess(che);
        }else if(Objects.equals(modeltype, "退出")){
            System.exit(0);
        }else {
            System.out.println("输入错误!，请重新输入");
            return false;
        }
        return true;
    }


    public int playgame(int x,int y) {
        try {
            System.out.println(game.getPlayer());
            this.archive.createSingleMemento(game.getPlayer(),game.getChess());//存档一次。System.out.println("存档了");
            if (!this.game.move(x, y)) {
                return -1;//落子错误
            }
            //IsOver?//1正常 0结束对局
            if (this.game.isOver()) {
                winner = this.game.getWinner();
                return 0;
            }
            this.game.changePlayer();
            this.game.printGame();
            return 1;
        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean barControll(String command) throws IOException, ClassNotFoundException {
        boolean c = false;
        if (Objects.equals(game.getGametype(), "围棋")) {
            c = this.WeiqiControlBar(command);
        } else if (Objects.equals(game.getGametype(), "五子棋")) {
            c = this.GomokuControlBar(command);
        }
        return c;
    }
    public String getWinner(){
        return this.winner;
    }
    private boolean GomokuControlBar(String com) throws IOException, ClassNotFoundException {
        if(!flag){
            if(Objects.equals(com, "R")){//重新开始
                game.restart();//重置棋盘
                System.out.println("新棋盘：");
                game.printGame();
                flag = false;
                winner = null;
            }  else if (Objects.equals(com, "T")) {//悔棋
                Chess che = archive.restoreSingleMemento(game.getPlayer());
                if(che != null)
                    game.setChess(che);
                else
                    System.out.println("无法悔棋。");
            } else if (Objects.equals(com, "S")) {//认输
                winner = this.game.Surrend();
            } else if (Objects.equals(com, "A")) {//存档
                archive.saveMemento(game.getChess());
            } else if(Objects.equals(com, "N")){//隐藏Bar
                ui.setBarenable();
            }else if (Objects.equals(com, "E")) {//退出
                System.exit(0);
            }else if(Objects.equals(com, "D")){//请求结束。
                flag = true;
                game.changePlayer();
            }else {
                return false;
            }
        }else{
            if(Objects.equals(com, "yes")){
                winner = "draw";
                flag =false;
            }else if(Objects.equals(com, "no")){
                flag = false;
            }else {
                return false;
            }
        }
        return true;

    }

    public boolean WeiqiControlBar(String com) throws IOException, ClassNotFoundException {
        if(!flag){
            if(Objects.equals(com, "R")){//重新开始
                game.restart();
                System.out.println("新棋盘：");
                game.printGame();
                archive.restartSingleMemento();
                flag = false;
                winner = null;
            }  else if (Objects.equals(com, "T")) {//悔棋
                if(archive.isSingleMemento(game.getPlayer())){
                    Chess che = archive.restoreSingleMemento(game.getPlayer());
                    game.setChess(che);
                }else {
                    System.out.println("无法悔棋。");
                }
            } else if (Objects.equals(com, "S")) {//认输
                winner = this.game.Surrend();
            } else if (Objects.equals(com, "A")) {//存档
                archive.saveMemento(game.getChess());
            } else if(Objects.equals(com, "N")){//不显示Bar
                ui.setBarenable();
            }else if (Objects.equals(com, "E")) {//退出
                System.exit(0);
            } else if(Objects.equals(com, "P")){//跳过
                this.game.changePlayer();
            }else if(Objects.equals(com, "D")){//请求结束。
                game.changePlayer();
                flag = true;
            }else {
                return false;
            }
        }else{
            if(Objects.equals(com, "yes")){
                winner = this.game.getWinner();
                flag =false;
            }else if(Objects.equals(com, "no")){
                flag = false;
            }else {
                return false;
            }
        }
        return true;
    }

    public void displayBoard(){//打印Bar
        if(flag)
            System.out.println("是否同意结束：1.yes 2.no or others");
        else {
            if(Objects.equals(game.getGametype(), "五子棋"))
                ui.GomokuControllBar();
            else
                ui.WeiqiControllBar();
        }

    }

    public String getPlayer() {
        return game.getPlayer();
    }
    public int getStep(){
        return game.getPlayersteps()+1;

    }

    public String getGametype() {
        return game.getGametype();
    }
}
