/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sportstats.sparkjava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static java.lang.Integer.valueOf;
import java.util.List;
import spark.Request;
import spark.Session;
import static spark.Spark.*;
import sportstats.domain.League;
import sportstats.domain.Season;
import sportstats.domain.Sport;
import sportstats.service.AddSportService;
import sportstats.service.GetAllLeaguesBySportIdService;
import sportstats.service.GetAllSeasonsByLeagueIdService;
import sportstats.service.GetAllSportsService;
import sportstats.service.ServiceRunner;

public class Main {
    
    private static ObjectMapper objectMapper;
    
    public static void main(String[] args) {
        
        objectMapper = new ObjectMapper();
        
        stop();
        port(4580);

        get("/getAllSports", (req, res) -> getAllSports());
        
        get("/getAllLeaguesBySportId/:sportId", (req, res) -> getAllLeaguesBySportId(req.params(":sportId")));
        
        get("/getAllSeasonsByLeagueId/:leagueId", (req, res) -> getAllSeasonsByLeagueId(req.params(":leagueId")));
        
        get("/addSport/:sportName", (req, res) -> addSport(req.params(":sportName")));
          
    }
    
    private static String getAllSports() throws JsonProcessingException{
        
        GetAllSportsService getAllSportsService = new GetAllSportsService();
        ServiceRunner<List> serviceRunner = new ServiceRunner(getAllSportsService);
        
        List<Sport> sports = serviceRunner.execute();
 
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sports);
  
    }
    
    private static String getAllLeaguesBySportId(String sportId) throws JsonProcessingException{
        
        int id = Integer.parseInt(sportId);

        ServiceRunner<List> serviceRunner = new ServiceRunner(new GetAllLeaguesBySportIdService(id));
        List<League> leagues = serviceRunner.execute();

        String printString = "";
        
        printString = leagues.stream().map((l) -> "</br>" + "id: " + l.getId() + " leagueName: " + l.getLeagueName() + " sportId: " + l.getSportId()).reduce(printString, String::concat);
 
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(printString);
  
    }
    
    private static String getAllSeasonsByLeagueId(String leagueId) throws JsonProcessingException{
        
        int id = Integer.parseInt(leagueId);

        ServiceRunner<List> serviceRunner = new ServiceRunner(new GetAllSeasonsByLeagueIdService(id));
        List<Season> seasons = serviceRunner.execute();

        String printString = "";
        
        printString = seasons.stream().map((s) -> "</br>" + "id: " + s.getId() + " startYear: " + s.getStartYear() + " endYear: " + s.getEndYear() + " leagueId: " + s.getLeagueId()).reduce(printString, String::concat);
 
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(printString);
  
    }
    
    private static String addSport(String sportName){
        
        ServiceRunner<Sport> serviceRunner = new ServiceRunner(new AddSportService(sportName));
        Sport sport = serviceRunner.execute();
        
        return sport.getSportName() + " with and id of " + sport.getId() + " was added to sports";
    
    }
    
}