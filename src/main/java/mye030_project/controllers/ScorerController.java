package mye030_project.controllers;

import mye030_project.repositories.ScorersRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ScorerController {
    @Autowired
    private ScorersRepository scorersRepository;

    @RequestMapping("/scorer")
    public String getScorerProfile(@RequestParam("scorer")String scorer, Model model)
    {
        Map<String,Object> years = scorersRepository.getYears(scorer);
        Integer fromYear = ((Number) years.get("firstGame")).intValue();
        Integer toYear = ((Number) years.get("lastGame")).intValue();
        model.addAttribute("fromYear",fromYear);
        model.addAttribute("toYear",toYear);
        Map<String,Object> totalGoals = scorersRepository.getTotalGoals(scorer);
        model.addAttribute("totalGoals",totalGoals.get("totalGoals"));
        model.addAttribute("scorer",scorer);
        Map<String,Object> maxGoalsPerGame = scorersRepository.getMaxNumberOfGoals(scorer);
        model.addAttribute("maxGoalsPerGame",maxGoalsPerGame.get("maxGoals"));
        Double teamGoalsGame = scorersRepository.getTeamsGoalsGame(scorer,fromYear,toYear);
        model.addAttribute("teamsGoalsGame",teamGoalsGame);
        List<Map<String, Object>> teamGoalsPerGame= scorersRepository.getTeamsGoalsGamePerYear(scorer,fromYear,toYear);
        model.addAttribute("totalGoalsGamesPerYear",teamGoalsPerGame);
        JSONArray jsonArray = new JSONArray(teamGoalsPerGame);
        model.addAttribute("data",jsonArray);
        return "scorer-profile";
    }

}
