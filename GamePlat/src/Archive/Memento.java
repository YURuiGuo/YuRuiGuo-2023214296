package Archive;
//备忘录模式
//三个角色：
//发起人
//备忘录角色
//备忘录管理员

import PlayGameSystem.Chess;

import java.io.*;

interface MementoIF{

}
public class Memento implements MementoIF,Serializable {
    private Chess chessState;//棋盘的状态
    //private Vector<String> command;//执行的命令可能用不着
//    private String command;//检查点
    private int cur =0 ;

    //用于悔棋
    public Memento(Chess state) throws IOException, ClassNotFoundException {// public Memento(Chess state, String command)
        this.cur = 0;
        // Serialize the object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(state);
        oos.flush();
        oos.close();
        // Deserialize the object
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        this.chessState = (Chess) ois.readObject();
        ois.close();
    }



    //状态取值方法
    public Chess getChessState(){
        return this.chessState;
    }

    public Chess getChessState(int index){
        try (FileInputStream fis = new FileInputStream("chess"+String.valueOf(index)+".ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Chess) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }


//    public int getCommand(){
//        return index;
//    }
}
