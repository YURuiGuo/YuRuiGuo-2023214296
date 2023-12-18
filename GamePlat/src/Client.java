
import java.io.IOException;
import java.util.Scanner;

//该函数为客户端
public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String gametype;
        int steps;
        String player;

        System.out.println("I am Client");
        GamePlat platform = new GamePlat();
        System.out.println("Loading Game ...");
        boolean flag = platform.startgame();
        while (!flag){//输入错误
            System.out.println("输入错误");
            flag = platform.startgame();
        }//选中了，开始下棋：

        boolean stopgame = false;//退出游戏标志
        boolean resflag = false;//请求结束标志

        //开始下棋：
        while (!stopgame){
            steps = platform.getStep();
            gametype= platform.getGametype();
            player = platform.getPlayer();
            System.out.println("游戏："+gametype+",执棋手："+player+",回合："+steps);

            platform.displayBoard();

            Scanner sc = new Scanner(System.in);
            String command = sc.nextLine();
            String[] com = command.split(" ");
            if(com.length == 2){
                int x = Integer.parseInt(com[0]);
                int y = Integer.parseInt(com[1]);
                int stateCode = platform.playgame(x,y);
                //移动棋子;IsOver;存档
               if(stateCode == -1){
                   System.out.println("输入错误！");
               }else if(stateCode == 0){
                   stopgame =true;
               }
            }else {//Bar Control:
                if(!platform.barControll(command)&&!resflag){
                    System.out.println("输入错误！");
                }else {
                    System.out.println("命令"+command+"执行成功");

                    if(command.equals("S")){//认输,就结束游戏.有胜利者的结束,而不是退出游戏，
                        stopgame = true;
                    }else if(command.equals("D")|| resflag){//或者请求结束
                        if(resflag){
                            stopgame = true;
                        }else {
                            resflag = true;
                        }
                    }else {
                     continue;
                    }
                }
            }
        }
        String winner = platform.getWinner();
        if(winner!=null)
            System.out.println("GameOver, Winner: " + winner+" !");
        }

}
