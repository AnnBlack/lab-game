import java.util.*;

public class GameStateTracker
{
    private Stack<GameState> stateTrackList;
    private GameState lastGameState;

    public GameStateTracker()
    {
        // initialise instance variables
        this.stateTrackList = new Stack<>(); 
        
    }
    
    public GameState getLastGameStage()
    {   
        lastGameState = null;
        
        for(int i = 0; i < 2; i++)
        {   
            if (!stateTrackList.empty()) {
                lastGameState = stateTrackList.pop(); //if you are at the start of the game you stay where you are - nowhere to go back to
            }                                                    
        }
        
        return lastGameState;
    }
    
    public void recordGameState(GameState currentGameState)  
    {
       lastGameState = currentGameState.deepCopy(currentGameState);
       stateTrackList.push(lastGameState);
    }
}