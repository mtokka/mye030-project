package mye030_project.controllers;

import mye030_project.repositories.ResultRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class YearController {

    @Autowired
    private ResultRepository resultRepository;

    @RequestMapping("/year-profile")
    private String getYearProfile(@RequestParam("year") Integer year, Model model){

        Integer totalMatches = resultRepository.getTotalMatches(year);
        Integer totalDraws = resultRepository.getTotalDraws(year);
        Integer penalties = resultRepository.getTotalMatchesWithPenalties(year);
        List<Map<String , Object>> totalMatchesByCountry =resultRepository.getTotalMatchesByCountry(year);
        model.addAttribute("totalMatchesByCountry", totalMatchesByCountry);
        JSONArray jsonArray = new JSONArray(totalMatchesByCountry);
        model.addAttribute("year", year);
        model.addAttribute("totalMatches", totalMatches);
        model.addAttribute("draws", totalDraws);
        model.addAttribute("penalties", penalties);
        model.addAttribute("data", jsonArray);
        return "year-profile";
    }

}
