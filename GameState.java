import java.util.*;
import java.io.*;

public class GameState implements Serializable  
{
    private Player player;
    private ArrayList<Room> rooms;    
    
    public GameState(Player player, ArrayList<Room> rooms)
    {
        this.rooms = rooms;
        this.player = player;
    }
    
    public void setRooms(ArrayList<Room> rooms)
    {
        this.rooms = rooms;
    }
    
    public void setPlayer(Player player)
    {
        this.player = player;
    }
    
    public ArrayList<Room> getRooms()
    {
        return rooms;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    public GameState deepCopy(GameState gameStage) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(gameStage);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return (GameState) objInputStream.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}