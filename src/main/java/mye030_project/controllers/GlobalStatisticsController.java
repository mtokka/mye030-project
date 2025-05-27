package mye030_project.controllers;


import mye030_project.repositories.ResultRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class GlobalStatisticsController {

    @Autowired
    private ResultRepository resultRepository;

    @RequestMapping("/global-statistics")
    public String getGlobalStatistics(Model model) {

        List<Map<String, Object>> top10ByWins = resultRepository.getTop10ByWins();
        List<Map<String, Object>> top10ByScore = resultRepository.getTop10ByScore();
        List<Map<String, Object>> top10ByWinsYears = resultRepository.getTop10ByWinsYears();
        List<Map<String, Object>> top10ByScoreYears  = resultRepository.getTop10ByScoreYears();

        List<Map<String, Object>> winsArea = resultRepository.getWinsArea();
        List<Map<String, Object>> pointsPopulation = resultRepository.getPointsPopulation();

        JSONArray jsonArray1 = new JSONArray(top10ByWins);
        JSONArray jsonArray2 = new JSONArray(top10ByScore);
        JSONArray jsonArray3 = new JSONArray(top10ByWinsYears);
        JSONArray jsonArray4 = new JSONArray(top10ByScoreYears);
        JSONArray jsonArray5 = new JSONArray(winsArea);
        JSONArray jsonArray6 = new JSONArray(pointsPopulation);



        model.addAttribute("top10ByWins", top10ByWins);
        model.addAttribute("barchart_data1", jsonArray1);

        model.addAttribute("top10ByScore", top10ByScore);
        model.addAttribute("barchart_data2", jsonArray2);

        model.addAttribute("top10ByWinsYears", top10ByWinsYears);
        model.addAttribute("barchart_data3", jsonArray3);
        model.addAttribute("top10ByScoreYears", top10ByScoreYears);
        model.addAttribute("barchart_data4", jsonArray4);

        model.addAttribute("scatter_data1", jsonArray5);
        model.addAttribute("scatter_data2", jsonArray6);

        return "global-statistics";
    }
}
