package Archive;


import PlayGameSystem.Chess;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

//发起人角色
//存档子系统：也是发起人。
//用于游戏的恢复及保存。
public class ArchiveSystem {
    private int index;//存了几个档案了
    private int archiveindex;
    private Caretaker caretaker;
    public ArchiveSystem(){
        this.caretaker = new Caretaker();
        this.archiveindex =0;
    }

    //用于悔棋的备忘录
    public void createSingleMemento(String player,Chess chess) throws IOException, ClassNotFoundException {

        caretaker.createSingleMemento(player,new Memento(chess));//创建一个存档
    }
    public Chess restoreSingleMemento(String player){
        MementoIF me = caretaker.restoreSingleMemento(player);
        return ((Memento) me).getChessState();
    }


    //用于存档的备忘录
    public void saveMemento(Chess chess) throws IOException, ClassNotFoundException {
        caretaker.saveMemento(new Memento(chess));//创建一个存档
        archiveindex++;
    }
    public Chess restoreMemento(int index){
        MementoIF me = caretaker.restoreMemento(index);
        return ((Memento) me).getChessState();
    }


    //用于获取存档数目
    public int getArchiveindex(){
        File folder = new File("chessArchive");
        Vector<String> serFiles = new Vector<>();
        if (folder.exists() && folder.isDirectory()) {
            // 遍历文件夹中的所有文件
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 判断文件名是否以".txt"结尾
                    System.out.println(file.getName());
                    if (file.getName().endsWith(".ser")) {
                        serFiles.add(file.getName());
                    }
                }
            }
        }
        int len = serFiles.size();
        printAllState(serFiles);
        archiveindex = len;
        return len;

    }

    public void printAllState(Vector<String> serFiles) {
        int len = serFiles.size();
        if (len == 0) {
            System.out.println("没有存档");
        }
        System.out.println("恢复点:");
        for (int i = 0; i < serFiles.size(); i++) {
            System.out.println((i) + "." + serFiles.get(i));
        }
    }

    public boolean isSingleMemento(String player) {
        return caretaker.isSingleMemento(player);
    }
    public void restartSingleMemento() {
        caretaker.restartSingleMemento();
    }
}
