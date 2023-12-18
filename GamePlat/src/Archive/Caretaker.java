package Archive;

import java.io.*;
import java.util.Objects;
import java.util.Vector;

//存档和恢复
//
public class Caretaker {
    private Vector<MementoIF> mementos = new Vector<>();//
    private MementoIF mementoBlack = null;
    private MementoIF mementoWhite = null;
    private MementoIF memento = null;//暂时没用到.
    private int current;

    public Caretaker(){
        this.current = 0;
    }

    //用于悔棋
    public void createSingleMemento(String player,MementoIF me){
        if(Objects.equals(player, "black"))
            mementoBlack = me;
        else
            mementoWhite = me;
    }
    public Memento restoreSingleMemento(String player) {
        Memento me;
        if(Objects.equals(player, "black"))
            me = (Memento) mementoBlack;
        else
            me = (Memento) mementoWhite;
        return me;
    }
    public boolean isSingleMemento(String player) {
        if(Objects.equals(player, "black")){
            return mementoBlack != null;
        }else if(Objects.equals(player, "white")){
            return mementoWhite != null;
        }
        return false;
    }
    public void restartSingleMemento(){
        mementoBlack = null;
        mementoWhite = null;
    }

    //用于存档，序列化和反序列化实现将存档写入文件中。
    public void saveMemento(MementoIF me){
        //序列化保存：
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("chessArchive/chess"+String.valueOf(current)+".ser"))) {
            out.writeObject(me);
        } catch (Exception e) {
            e.printStackTrace();
        }
        memento=me;
        current++;
    }
    public Memento restoreMemento(int indedx){
        //反序列化
        Memento me = null;
        try (FileInputStream fis = new FileInputStream("chessArchive/chess"+String.valueOf(indedx)+".ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            me= (Memento) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return me;
    }//恢复到某个存档点
    public void removeMemento(int index){
        mementos.removeElementAt(index);
        current--;
    }//删除某个点。

    public void setCurrent(int current) {
        this.current = current;
    }
}
