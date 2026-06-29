import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Tournament{
    private static final String url = "jdbc:mysql://localhost:3306/chesstournamentdb";
    private static final String username = "root";
    private static final String password = "YOUR_PASSWORD";
    
    void addPlayer(int playerId, String name, int rating){
        try{
            Connection con = DriverManager.getConnection(url,username,password);

            String query1 = "insert into players(playerId, name, rating, points) values(?,?,?,0)";
            String query2 = "select playerid from players";
            PreparedStatement statement1 = con.prepareStatement(query2);
            ResultSet playerexistence = statement1.executeQuery();
            while(playerexistence.next()){
                if(playerexistence.getInt("playerid")==playerId){
                    System.out.println("Player Already Exist.");
                    return;
                }
            }
            PreparedStatement statement = con.prepareStatement(query1);
            statement.setInt(1, playerId);
            statement.setString(2, name);
            statement.setInt(3, rating);

            int result = statement.executeUpdate();
            if(result>0){
                System.out.println("Player Added");
            }
            
        }catch(SQLException a){
            System.out.println(a.getMessage());
        }
        
    }

    void removePlayer(int playerId){
        try{
            Connection con = DriverManager.getConnection(url,username,password);

            String query = "delete from players where playerId = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, playerId);
            
            int result = statement.executeUpdate();
            if(result>0){
                System.out.println("Player Removed");
            }
            else{
                System.out.println("Player Does not Exist");
            }
        }catch(SQLException a){
            System.out.println(a.getMessage());
        }
    }

    void searchPlayer(String partOfName){
        System.out.println("Searching...");
        boolean found = false;
        try{
            Connection con = DriverManager.getConnection(url,username,password);

            String query = "select name from players";
            PreparedStatement statement = con.prepareStatement(query);

            ResultSet result = statement.executeQuery();
            while(result.next()){
                String name = result.getString("name");
                if(name.toLowerCase().contains(partOfName.toLowerCase())){
                    found = true;
                    String printPlayer = "select * from players where name = ?";
                    PreparedStatement statement2 = con.prepareStatement(printPlayer);
                    statement2.setString(1, name);
                    ResultSet playerdata = statement2.executeQuery();
                    while (playerdata.next()) {
                        System.out.println("********************************************");
                        System.out.println("PlayerId: "+playerdata.getInt("playerId")+"\nName: "+playerdata.getString("name")+"\nRating: "+playerdata
                    .getInt("rating")+"\nPoints: "+playerdata.getDouble("points"));
                    }
                }
            }
        }catch(SQLException a){
            System.out.println(a.getMessage());
        }
        if(found==false){
            System.out.println("Player not Found.");
        }
    }

    void displayPlayerProfile(int playerId){
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            String query1 = "select * from players where playerid = ?";
            PreparedStatement statement1 = con.prepareStatement(query1);
            String query2 = "select player02id, winnerid from matches where player01id = ?";
            String query3 = "select player01id, winnerid from matches where player02id = ?";
            String query4 = "select name from players where playerid = ?";
            PreparedStatement statement2 = con.prepareStatement(query2);
            PreparedStatement statement3 = con.prepareStatement(query3);
            PreparedStatement statement4 = con.prepareStatement(query4);
            statement1.setInt(1, playerId);
            ResultSet playerDetails = statement1.executeQuery();
            if(playerDetails.next()){
                int i = 1;
                System.out.println("PlayerId: "+playerDetails.getInt("playerid")+"\nName: "+playerDetails.getString("name")+"\nRating: "+playerDetails.getInt("rating")+"\nPoints: "+playerDetails.getDouble("points"));
                statement2.setInt(1,playerId);
                ResultSet MatchesDetails = statement2.executeQuery();
                while(MatchesDetails.next()){
                    statement4.setInt(1, MatchesDetails.getInt("player02id"));
                    ResultSet playername = statement4.executeQuery();
                    if(playername.next()){
                        System.out.println(i+" vs "+playername.getString("name"));
                        if(MatchesDetails.getInt("winnerid")==playerId) System.out.println("Result: Won");
                        else System.out.println("Result: Loss");
                    } 
                }
                statement3.setInt(1,playerId);
                MatchesDetails = statement3.executeQuery();
                while(MatchesDetails.next()){
                    statement4.setInt(1, MatchesDetails.getInt("player01id"));
                    ResultSet playername = statement4.executeQuery();
                    if(playername.next()){
                        System.out.println(i+" vs "+playername.getString("name"));
                        if(MatchesDetails.getInt("winnerid")==playerId) System.out.println("Result: Won");
                        else System.out.println("Result: Loss");
                    } 
                }

            }
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
    }

    void displayPlayers(){
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            String query = "select * from players";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet players = statement.executeQuery();
            while(players.next()){
                System.out.println("*************************************");
                System.out.println("Player Id: "+players.getInt("playerId")+"\nName: "+players.getString("name")+"\nRating: "+players.getInt("rating")+"\nPoints: "+players.getDouble("points"));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void recordMatch(int player01Id, int player02Id){
        Scanner sc = new Scanner(System.in);
        int check =0 ;
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            String query1 = "select playerId, name from players";
            PreparedStatement statement1 = con.prepareStatement(query1);
            ResultSet players = statement1.executeQuery();
            String Player01Name = "";
            String Player02Name = "";
            while(players.next()){
                
                if(players.getInt("playerId")==player01Id){
                    check++;
                    Player01Name = players.getString("name");
                }
                if(players.getInt("playerId")==player02Id){
                    check++;
                    Player02Name = players.getString("name");
                }
            }
            if(check<2){
                System.out.println("Player Does Not Exist.");
                return;
            } 
            if(player01Id==player02Id) System.out.println("Player Can't play themselves");
            else{
                String query2 = "update players set points = points + ? where playerId = ?";
                PreparedStatement statement2 = con.prepareStatement(query2);
                String query3 = "insert into matches(player01id,player02id,winnerid) values(?,?,?)";
                PreparedStatement statement3 = con.prepareStatement(query3);
                System.out.print("Enter the result: \n 1.Player 1 "+Player01Name+" won \n 2.Player 2 "+Player02Name+" won \n 3.Draw\n Result: ");
                int result = sc.nextInt();
                switch (result) {
                    case 1:
                        statement2.setDouble(1, 2.5);
                        statement2.setInt(2, player01Id);
                        statement3.setInt(1, player01Id);
                        statement3.setInt(2, player02Id);
                        statement3.setInt(3, player01Id);
                        statement2.executeUpdate();
                        statement3.executeUpdate();
                        break;
                    case 2:
                        statement2.setDouble(1, 2.5);
                        statement2.setInt(2, player02Id);
                        statement3.setInt(1, player01Id);
                        statement3.setInt(2, player02Id);
                        statement3.setInt(3, player02Id);
                        statement2.executeUpdate();
                        statement3.executeUpdate();
                        break;
                    case 3:
                        statement2.setInt(1, 1);
                        statement2.setInt(2, player01Id);
                        statement2.executeUpdate();
                        statement2.setInt(1, 1);
                        statement2.setInt(2, player02Id);
                        statement2.executeUpdate();
                        statement3.setInt(1, player01Id);
                        statement3.setInt(2, player02Id);
                        statement3.setInt(3, 0);
                        statement3.executeUpdate();
                    default:
                        break;
                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
    }

    void displayMatches(){
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            String query1 = "select * from matches";
            String query2 = "select name from players where playerId = ?";
            PreparedStatement statement1 = con.prepareStatement(query1);
            PreparedStatement statement2 = con.prepareStatement(query2);
            ResultSet matches = statement1.executeQuery();
            if(!matches.next()) System.out.println("No Matches Played");
            while(matches.next()){
                System.out.println("**********************************");
                System.out.println("matchId: "+matches.getInt("matchid"));
                statement2.setInt(1,matches.getInt("player01id"));
                ResultSet playerName = statement2.executeQuery();
                if(playerName.next()) System.out.print(playerName.getString("name"));
                System.out.print(" V/S ");
                statement2.setInt(1,matches.getInt("player02id"));
                playerName = statement2.executeQuery();
                if(playerName.next()) System.out.println(playerName.getString("name"));
                if(matches.getInt("winnerId")==0) System.out.println("Result: Draw");
                else{ 
                    statement2.setInt(1,matches.getInt("winnerid"));
                    playerName = statement2.executeQuery();
                    if(playerName.next()) System.out.println("Result: "+playerName.getString("name")+" Wins");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    void leaderboard(){
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            String query = "select playerid, name, points from players order by points desc";
            int i = 1;
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet leaderboard = statement.executeQuery();
            while (leaderboard.next()) {
                System.out.println("*******************************");
                System.out.println("Rank: "+i+ "\nPlayerId: "+leaderboard.getInt("playerid")+"\nName: "+leaderboard.getString("name")+"\nPoints: "+leaderboard.getDouble("points"));
                i++;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void tournamentStats(){
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            String query1 = "select count(playerid), max(points), min(points) from players";
            String query2 = "select count(matchid) from matches";
            
            PreparedStatement statement1 = con.prepareStatement(query1); 
            PreparedStatement statement2 = con.prepareStatement(query2);
            
            ResultSet playerPoints = statement1.executeQuery();
            ResultSet totalMatches = statement2.executeQuery();
            if(playerPoints.next() && totalMatches.next()){
                System.out.println("Total Players: "+playerPoints.getInt("count(playerid)")+"\nTotal Matches: "+totalMatches.getInt("count(matchId)")+"\nMaximum Points: "+playerPoints.getDouble("max(points)")+"\nMinimum Points: "+playerPoints.getDouble("min(points)"));
            }
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
}

public class main{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        Tournament t = new Tournament();
        System.out.println("===========WELCOME TO CHESS TOURNAMENT MANAGEMENT==========");
        while(true){
            
            System.out.print("Select Your Choice\n 1.Add Player\n 2.Remove Player\n 3.Search Player\n 4.Display Players\n 5.Display Player Profile\n 6.Record Match\n 7.Display Matches\n 8.LeaderBoard\n 9.Tournament Stats\n 10.Exit\n choice: ");    
            int choice = sc.nextInt();
            
            switch(choice){
                case 1:
                    System.out.print("Enter Player Id: ");
                    int playerId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Player Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Player Rating: ");
                    int rating = sc.nextInt();
                    t.addPlayer(playerId,name,rating);
                    break;
                case 2:
                    System.out.print("Enter Player Id: ");
                    int playerIdToRemove = sc.nextInt();
                    t.removePlayer(playerIdToRemove);
                    break;
                case 3:
                    sc.nextLine();
                    System.out.print("Enter part of the name to search: ");
                    String partOfName = sc.nextLine();
                    t.searchPlayer(partOfName.toLowerCase());
                    break;
                case 4:
                    t.displayPlayers();
                    break;
                case 5:
                    System.out.print("Enter Player Id: ");
                    int playerIdToDisplay = sc.nextInt();
                    t.displayPlayerProfile(playerIdToDisplay);
                    break;
                case 6:
                    System.out.print("Enter Player 01 Id: ");
                    int playerId01 = sc.nextInt();
                    System.out.print("Enter Player 02 Id: ");
                    int playerId02 = sc.nextInt();
                    t.recordMatch(playerId01,playerId02);
                    break;
                case 7:
                    t.displayMatches();
                    break;
                case 8:
                    t.leaderboard();
                    break;
                case 9:
                    t.tournamentStats();
                    break;
                case 10:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid Choice.");
                    break;
            }
        }
        
    
    }
}