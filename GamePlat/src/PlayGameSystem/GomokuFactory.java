package PlayGameSystem;

import gomoku.Gomoku;

public class GomokuFactory implements ChessFactory{
    @Override
    public Chess factory(int size){
        return new Gomoku(size);
    }
}
