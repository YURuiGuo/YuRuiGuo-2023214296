package UISysetem;

public class UISystem {
    private boolean barenable = true;
    public void WelcomePage(){//欢迎界面
        System.out.println("Welcome to Chess Game Platform !   Copyright © 2024 YuRuiGuo");
        System.out.println("欢迎来到棋类对战平台");
        System.out.println("该平台当前支持两种棋类: 围棋、五子棋");
        System.out.println("玩的开心^_^ !");

    }
    public void FirstLevelPage(){//一级页面
        System.out.println("请选择游戏模型：1.新游戏 2.继续游戏 3.退出 ");
        System.out.println("请输入选项：");
    }
    public void ChooseGamePage(){//二级页面
        System.out.println("1.围棋  2.五子棋  3.退出");
        System.out.println("请输入游戏类型及棋盘大小(8-19)：");
    }
    public void WeiqiControllBar(){//围棋控制Bar
        if(!barenable)
            return;
        System.out.println("Bar: R-重新开始、T-悔棋、S-认输、A-存档、N-隐藏Bar、P-跳过、D-请求结束、E-退出");
    }
    public void GomokuControllBar(){//五子棋控制Bar
        if(!barenable)
            return;
        System.out.println("Bar: R-重新开始、T-悔棋、S-认输、A-存档、N-隐藏Bar、D-请求结束、E-退出");
    }
    public void setBarenable() {
        this.barenable = !this.barenable;
    }
}
