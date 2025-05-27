package mye030_project.controllers;

import mye030_project.repositories.CountryRepository;
import mye030_project.repositories.ResultRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class CountryController {


    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ResultRepository resultRepository;

    @RequestMapping("/country-profile")
    public String getCountryProfile(@RequestParam("country") String name, Model model) {
        Integer countryId = countryRepository.findId(name);
        List<Map<String, Object>> dates = resultRepository.findFirstAndLastGame(countryId);
        if (dates.get(0).get("firstGame") == null || dates.get(0).get("lastGame") == null) {
            return null;
        }
        LocalDate firstGame = ((java.sql.Date) dates.get(0).get("firstGame")).toLocalDate();
        LocalDate lastGame =  ((java.sql.Date) dates.get(0).get("lastGame")).toLocalDate();
        int fromYear = firstGame.getYear();
        int toYear = lastGame.getYear();

        Map<String, Object> allMatches = resultRepository.getResultsFromYearToYear(countryId, fromYear,toYear);
        Map<String, Object> homeMatches = resultRepository.getResultsFromYearToYearAsHomeTeam(countryId, fromYear,toYear);
        Map<String, Object> awayMatches = resultRepository.getResultsFromYearToYearAsAwayTeam(countryId, fromYear,toYear);



        List<Map<String , Object>> matches =  List.of(allMatches ,homeMatches, awayMatches);
        List<Map<String,Object>> resultsPerYear= resultRepository.getResultsPerYear(countryId, fromYear,toYear);
        addAttributes(model, name, fromYear,toYear,matches,resultsPerYear);

        return "country-profile";
    }

    @RequestMapping("/country-filter")
    public String getCountry(@RequestParam("country") String name,@RequestParam("startYear") Integer fromYear,@RequestParam("endYear") Integer toYear, Model model) {
        Integer countryId = countryRepository.findId(name);

        Map<String, Object> allMatches = resultRepository.getResultsFromYearToYear(countryId,fromYear,toYear);
        Map<String, Object> homeMatches = resultRepository.getResultsFromYearToYearAsHomeTeam(countryId,fromYear,toYear);
        Map<String, Object> awayMatches = resultRepository.getResultsFromYearToYearAsAwayTeam(countryId,fromYear,toYear);



        List<Map<String , Object>> matches =  List.of(allMatches ,homeMatches, awayMatches);
        List<Map<String,Object>> resultsPerYear= resultRepository.getResultsPerYear(countryId,fromYear,toYear);
        addAttributes(model, name,fromYear,toYear,matches,resultsPerYear);
        return "country-filter";
    }

    @RequestMapping("/country-matches")
    public String getMatches(@RequestParam("country") String name,@RequestParam("fromYear") Integer fromYear,@RequestParam("toYear") Integer toYear, Model model) {
        Integer countryId = countryRepository.findId(name);


        model.addAttribute("countryName", name);
        model.addAttribute("fromYear", fromYear);
        model.addAttribute("toYear", toYear);

        List<Map<String, Object>> matches =  resultRepository.getAllMatchesFromYear(countryId,fromYear,toYear);
        model.addAttribute("totalMatches", matches.size());
        model.addAttribute("matches", matches);



        return "country-matches";
    }



    private void addAttributes(Model model, String name,Integer fromYear,Integer toYear,List<Map<String , Object>> matches,List<Map<String,Object>> resultsPerYear) {


        model.addAttribute("countryName", name);
        model.addAttribute("fromYear", fromYear);
        model.addAttribute("toYear", toYear);

        model.addAttribute("totalMatches", matches.get(0).get("totalMatches"));
        model.addAttribute("totalWins", matches.get(0).get("wins"));
        model.addAttribute("totalLosses", matches.get(0).get("losses"));
        model.addAttribute("totalDraws", matches.get(0).get("draws"));
        model.addAttribute("totalShootoutsWins", matches.get(0).get("shootouts_wins"));
        model.addAttribute("totalShootoutsLosses", matches.get(0).get("shootouts_losses"));


        model.addAttribute("homeMatches", matches.get(1).get("totalHomeMatches"));
        model.addAttribute("homeWins", matches.get(1).get("wins"));
        model.addAttribute("homeLosses", matches.get(1).get("losses"));
        model.addAttribute("homeDraws", matches.get(1).get("draws"));
        model.addAttribute("homeShootoutsWins", matches.get(1).get("shootouts_wins"));
        model.addAttribute("homeShootoutsLosses", matches.get(1).get("shootouts_losses"));


        model.addAttribute("awayMatches", matches.get(2).get("totalAwayMatches"));
        model.addAttribute("awayWins", matches.get(2).get("wins"));
        model.addAttribute("awayLosses", matches.get(2).get("losses"));
        model.addAttribute("awayDraws", matches.get(2).get("draws"));
        model.addAttribute("awayShootoutsWins", matches.get(2).get("shootouts_wins"));
        model.addAttribute("awayShootoutsLosses", matches.get(2).get("shootouts_losses"));


        JSONArray array = new JSONArray(resultsPerYear);
        model.addAttribute("data", array);

    }
}
