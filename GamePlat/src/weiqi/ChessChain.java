package weiqi;

import java.io.Serializable;
import java.util.Arrays;

public class ChessChain implements Serializable {

    private int size;//棋盘大小，与chessboard大小同步
    private int index=0;
    private int[][] chainboard = null;
    private int[] chainfate = new  int[181*3];// 维护一个就行，至于黑棋还是白棋的，到时候看占到这个位置的chessbord是谁即可

    public ChessChain(int size){
        this.chainboard = new int[size][size];
        this.size = size;
        Arrays.fill(chainfate,-1);//初始化chainfate为 -1
        for (int i = 0; i < size; i++) {//初始化chainboard -1
            for (int j = 0; j < size; j++) {
                chainboard[i][j] = -1;
            }
        }
    }
    public void initBoard(){
        Arrays.fill(chainfate,-1);//初始化chainfate为 -1
        for (int i = 0; i < size; i++) {//初始化chainboard -1
            for (int j = 0; j < size; j++) {
                chainboard[i][j] = -1;
            }
        }
        index=0;
    }

    public int updatechainboard(int x, int y,int[][] chessboard){
        int fate = 4;
        if(x*y == size-2||x*y==1||x*y == (size-2)*(size-2))
            fate = 2;
        else if(x==1||x==size-2||y==1||y==size-2)
            fate = 3;
        fate -= neighbor(x,y,chessboard);
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        boolean flag = false;//判断棋子是否加入了其他链中，如果没有，则自成链子
        for (int i = 0; i < 4; i++) {
            int newRow = x + dr[i];
            int newCol = y + dc[i];

            if (isValidPosition(newRow, newCol,chessboard)) {
                //判断是否不同色：
                if (chessboard[newRow][newCol] != chessboard[x][y] ) {
                    chainfate[chainboard[newRow][newCol]]--;
                } else {//同色的处理比较复杂：
                    if(!flag){//还没有被并到新链中
                        chainboard[x][y]=chainboard[newRow][newCol];//并到链中
                        chainfate[chainboard[newRow][newCol]]=chainfate[chainboard[newRow][newCol]]+fate-1;
                        flag = true;
                    }else {//已经被并到其他链中，需要两个链子合并。
                        //遍历整个chainboard，只要是目标链则合并。选用谁的index都行
                        int index1 = chainboard[x][y];
                        int index2 = chainboard[newRow][newCol];
                        merge_chain(x,y,index1,index2);//合并&更新气数
                    }
                }
            }
        }
        if (!flag) {//自成一链子
            chainboard[x][y]=index;
            chainfate[index]=fate;
            index++;
        }
        return chainboard[x][y];//返回对应链子的编号
    }

    private void merge_chain(int x, int y, int index1, int index2) {
        //选用较小的index，作为合并后链的编号。
        if(index1>index2){
            int l = index1;
            index1 = index2;
            index2 = l;
        }
        for(int i=1;i<=size-2;i++){
            for(int j=1;j<=size-2;j++){
                if(chainboard[i][j]==index2){
                    chainboard[i][j] = index1;
                }
            }
        }
        chainfate[index1]=chainfate[index1]+ chainfate[index2] -1;
        chainfate[index2] = -1;//使chainfate[index2]失效。
    }

    private int neighbor(int x,int y,int[][] chessboard){
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int coun=0;
        for (int i = 0; i < 4; i++) {
            int newRow = x + dr[i];
            int newCol = y + dc[i];

            if (isValidPosition(newRow, newCol,chessboard)) {
                coun++;
            }
        }
        return coun;  // 没有邻居
    }


    private boolean isValidPosition(int row, int col,int[][] chessboard) {
        if(row >= 1 && row <= size-2 && col >= 1 && col <= size-2){
            if(chessboard[row][col] != 0){
                return true;//有邻居
            }
        }
        return false;
    }


    //落完子后，就检查提子
    //由于将所有的棋子处理成chain，检查提子的逻辑就很简单：看哪个chain为0，为0就提子
    //还有一个是提子后更新气数。
    //参数：被提的index数组，长度，发生提子的index
    public void captureUpdate(int x,int y, int[][] chessboard){//发生提子后更新气数。由于上述得到逻辑是将所有的棋子归为一个Chain，所以只需要提对应的chain即可。
        //获取Fate为0的邻居
        int indexx = chainboard[x][y];
        int[] a = new int[4];
        int l =0;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        //检查四个邻居的气数是否存在0的情况，：
        for (int i = 0; i < 4; i++) {
            int newRow = x + dr[i];
            int newCol = y + dc[i];
            if (isValidPosition(newRow, newCol,chessboard)&&(chainboard[newRow][newCol]!=indexx)) {
                if(chainfate[chainboard[newRow][newCol]] == 0){//说明该邻居气数为0
                    //记录下Index
                    a[l]=chainboard[newRow][newCol];
                    l++;
                }
            }
        }
        if(l==0){
            System.out.println("未发生提子");
            return;//没有发生提子
        }


        for (int in =0; in<l; in++){
            for(int i=1;i<=size-2;i++){
                for(int j=1;j<=size-2;j++){
                    if(chainboard[i][j]==a[in]){
                        chessboard[i][j] = 0;//该处置为初始态
                        chainboard[i][j] = -1;//该处置为初始态
                        captureAddFate(i,j,indexx,chessboard);//给被提的链子加气。
                    }
                }
            }
            //并使其气数失效：
            chainfate[a[in]] = -1;
        }
    }
    public void captureAddFate(int x,int y,int indexx,int[][] chainboard){
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int newRow = x + dr[i];
            int newCol = y + dc[i];
            if (isValidPosition(newRow, newCol,chainboard)&&chainboard[newRow][newCol]==indexx) {
                chainfate[indexx]++;
            }
        }
    }



    public int[] assumeUpdateboard(int x, int y, int f,int[][] chessboard){
        //复制一份：
        int[][] asChainB = new int[size][size];
        int asindex=index;
        int[] asChainfate = new  int[181*3];
        for (int i = 0; i < chainboard.length; i++) {
            System.arraycopy(chainboard[i], 0, asChainB[i], 0, chainboard[i].length);
        }
        System.arraycopy(chainfate, 0, asChainfate, 0, chainfate.length);




        int[] a = new int[4];
        Arrays.fill(a,-2);//初始化chainfate为 -1
        int l =0;

        int fate = 4;
        if(x*y == size-2)
            fate = 2;
        else if(x==1||x==size-2||y==1||y==size-2)
            fate = 3;
        fate -= neighbor(x,y,chessboard);
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        boolean flag = false;//判断棋子是否加入了其他链中，如果没有，则自成链子
        for (int i = 0; i < 4; i++) {
            int newRow = x + dr[i];
            int newCol = y + dc[i];
            if (isValidPosition(newRow, newCol,chessboard)) {
                //判断是否不同色：
                if (f != chessboard[newRow][newCol]) {
                    asChainfate[asChainB[newRow][newCol]]--;
                    if(asChainfate[asChainB[newRow][newCol]] == 0){
                        //提子！
                        a[l]=asChainB[newRow][newCol];
                        l++;
                    }
                } else {//同色的处理比较复杂：
                    if(!flag){//还没有被并到新链中
                        asChainB[x][y]=asChainB[newRow][newCol];//并到链中
                        asChainfate[asChainB[newRow][newCol]]=asChainfate[asChainB[newRow][newCol]]+fate-1;
                        flag = true;
                    }else {//已经被并到其他链中，需要两个链子合并。
                        //遍历整个chainboard，只要是目标链则合并。选用谁的index都行
                        int index1 = asChainB[x][y];
                        int index2 = asChainB[newRow][newCol];
                        merge_chain(x,y,index1,index2);//合并&更新气数
                    }
                }
            }
        }
        if (!flag) {//自成一链子
            asChainB[x][y]=asindex;
            asChainfate[asindex]=fate;
        }

        //要获取的内容，该落子点四周的应该提子的编号，如果没有
        int asfateindex =  asChainB[x][y];//该落子点对应链子的编号
        int asfate = asChainfate[asfateindex];//气数

        if(asfate == 0 && l == 0)
            return null;//说明自杀；
        return a;
    }


    public int getIndex(int i, int t) {
        return chainboard[i][t];
    }
}
