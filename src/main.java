import java.util.Scanner;
import java.util.TreeMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


class Player{
    private int playerId;
    private String name;
    private int rating;
    private double points;

    Player(int playerId, String name, int rating, double points){
        this.playerId = playerId;
        this.name = name;
        this.rating = rating;
        this.points  = points;
    }

    int getPlayerId(){
        return playerId;
    }
    String getName(){
        return name;
    }
    int getRating(){
        return rating;
    }
    double getPoints(){
        return points;
    }
    void addRating(int rating){
        this.rating = this.rating+rating;
    }
    void addPoints(double points){
        this.points = this.points+points;
    }

}



class Match{
    private int matchId;
    private int player01Id;
    private int player02Id;
    private int winId;

    Match(int matchId, int player01Id, int player02Id, int winId){
        this.matchId = matchId;
        this.player01Id = player01Id;
        this.player02Id = player02Id;
        this.winId = winId;
        
    }

    int getMatchId(){
        return matchId;
    }
    int getPlayer01Id(){
        return player01Id;
    }
    int getPlayer02Id(){
        return player02Id;
    }
    int getWinId(){
        return winId;
    }
    String getResult(){
        if(winId!=0) return winId+" Wins";
        else return "Draw";
    }
}



class Tournament{
    private static final String url = "jdbc:mysql://localhost:3306/chesstournamentdb";
    private static final String username = "root";
    private static final String password = "Password";
    

    TreeMap<Integer, Player> players = new TreeMap<>();
    TreeMap<Integer, Match> matches = new TreeMap<>();


    void addPlayer(int playerId, String name, int rating){
        
        try{
            Connection con = DriverManager.getConnection(url,username,password);

            String query = "insert into players(playerId, name, rating, points) values(?,?,?,0)";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, playerId);
            statement.setString(2, name);
            statement.setInt(3, rating);

            int result = statement.executeUpdate();
            if(result>0){
                System.out.println("Player Added");
            }
            else{
                System.out.println("Player Already Exist");
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
        if(players.containsKey(playerId)){
            Player p = players.get(playerId);
            int i=1;
            System.out.println("Player Id: "+p.getPlayerId()+"\nPlayer Name: "+p.getName()+"\nRating: "+p.getRating()+"\nPoints: "+p.getPoints());
            for(Match match : matches.values()){
                if(match.getPlayer01Id()==playerId){
                    if(i==1) System.out.println("Matches Played: ");
                    String result;
                    if(match.getWinId()==playerId) result = "wins";
                    if(match.getWinId()==0) result = "draw";
                    else result = "loss";
                    System.out.println(i+". vs "+players.get(match.getPlayer02Id()).getName()+" - "+result );
                    i++;
                }
                else if(match.getPlayer02Id()==playerId){
                    if(i==1) System.out.println("Matches Played: ");
                    String result;
                    if(match.getWinId()==playerId) result = "wins";
                    if(match.getWinId()==0) result = "draw";
                    else result = "loss";
                    System.out.println(i+". vs "+players.get(match.getPlayer01Id()).getName()+" - "+result);
                    i++;
                }
            }
            if(i==1) System.out.println("No Matches Played yet.");
        }
        else{
            System.out.println("Player Not found");
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
                if(check==2) break;
            }
            if(check<2) System.out.println("Player Does Not Exist.");
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
        if(!matches.isEmpty()){
            System.out.println("Matches Played: ");
            for(Integer matchId : matches.keySet()){
                Match m = matches.get(matchId);
                System.out.println("Match Id: "+m.getMatchId()+"\n"+players.get(m.getPlayer01Id()).getName()+" vs "+players.get(m.getPlayer02Id()).getName()+"\nResult: "+m.getResult());
            }
        }
        else{
            System.out.println("No Matches Played.");
        }
    }

    void leaderboard(){
        if(!players.isEmpty()){
            System.out.println("LeaderBoard: ");
            ArrayList<Player> leaderboard = new ArrayList<>(players.values());
            Comparator<Player> byPoints = new Comparator<Player>() {
                public int compare(Player p1, Player p2){
                    return Double.compare(p2.getPoints(), p1.getPoints());
                }
    
            };
            Collections.sort(leaderboard, byPoints);
            for(int i=0;i<leaderboard.size();i++){
                Player p = leaderboard.get(i);
                System.out.println("Rank: "+(i+1)+"\nPlayerId: "+p.getPlayerId()+"\nName: "+p.getName()+"\nPoints: "+p.getPoints());
            }
        }
    }

    void tournamentStats(){
        if(!players.isEmpty()){
            ArrayList<Player> leaderboard = new ArrayList<>(players.values());
            Comparator<Player> byPoints = new Comparator<Player>() {
                public int compare(Player p1, Player p2){
                    return Double.compare(p2.getPoints(), p1.getPoints());
                }
    
            };
        
            Collections.sort(leaderboard, byPoints);
            double sum=0;
            for(Player player : leaderboard){
                sum=sum+player.getPoints();
            }
        
            System.out.println("Total Players: "+players.size()+"\nTotal Matches: "+matches.size()+"\nHighest Points: "+leaderboard.get(0).getPoints()+"\nLowest Points: "+leaderboard.get(leaderboard.size()-1).getPoints()+"\nAverage Score: "+(sum/leaderboard.size()));
        }
        else{
            System.out.println("NO players Present");
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