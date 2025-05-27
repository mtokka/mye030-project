package mye030_project.controllers;


import mye030_project.repositories.CountryRepository;
import mye030_project.repositories.ResultRepository;
import mye030_project.repositories.ScorersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
public class MainController {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ScorersRepository scorersRepository;

    @Autowired
    private ResultRepository resultRepository;

    @RequestMapping("/home")
    public String getHome(Model model) {
        List<String> countries = countryRepository.findAll();
        model.addAttribute("countries", countries.stream().sorted());
        Set<String> scorers = scorersRepository.findAll();
        scorers.remove("-1");
        model.addAttribute("scorers", scorers.stream().sorted());
        List<Integer> years = resultRepository.findAllYears();
        model.addAttribute("years", years.stream().sorted());
        return "home";
    }
    @RequestMapping("/")
    public String index(Model model) {
        return "redirect:/home";
    }
}
