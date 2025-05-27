package mye030_project.repositories;

import com.fasterxml.jackson.core.ObjectCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class ScorersRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectCodec objectCodec;

    public Set<String> findAll() {
            String sql = "select scorer from scorers;";
            List<String> scorers = jdbcTemplate.queryForList(sql, String.class);
            Set<String> scorerSet = new HashSet<>(scorers);
            return scorerSet;
        }
        public Map<String,Object> getYears(String name){
            String sql = "select  YEAR(MIN(date)) AS firstGame, YEAR(MAX(date)) AS lastGame from scorers where scorer = ?" ;
            return jdbcTemplate.queryForMap(sql,name);
        }
        public Map<String,Object> getTotalGoals(String name){
            String sql = "SELECT COUNT(*) AS totalGoals FROM scorers where scorer = ?;" ;
            return jdbcTemplate.queryForMap(sql,name);
        }
        public Map<String,Object> getMaxNumberOfGoals(String name){
            String sql = "SELECT MAX(goals) as maxGoals " +
                    "FROM (" +
                    "   SELECT COUNT(*) AS goals " +
                    "   FROM scorers " +
                    "   WHERE scorer = ? " +
                    "   GROUP BY date,home_team,away_team)" +
                    "AS temp;" ;
            return jdbcTemplate.queryForMap(sql,name);
        }
    public Double getTeamsGoalsGame(String name, Integer fromYear, Integer toYear) {
        String sql = "  SELECT ROUND(1.0* "+
                "( SELECT COUNT(*)" +
                " FROM scorers" +
                " WHERE (team = (SELECT team FROM scorers WHERE scorer = ? LIMIT 1)) AND  (YEAR(date) >= ? AND YEAR(date) <= ?))  /"+
                "( SELECT COUNT(*)" +
                "FROM results WHERE" +
                "(home_team = (SELECT team FROM scorers WHERE scorer = ? LIMIT 1) OR " +
                "away_team = (SELECT team FROM scorers WHERE scorer = ? LIMIT 1)) AND (YEAR(date) >= ? AND YEAR(date) <= ?)" +
                "),5) AS totalGoalsGames;" ;

        return jdbcTemplate.queryForObject(sql,Double.class,name, fromYear, toYear,name, name, fromYear, toYear);
    }
    public List<Map<String, Object>> getTeamsGoalsGamePerYear(String name, Integer fromYear, Integer toYear) {

        String sql = "WITH scorer_team AS (\n" +
                "    SELECT team FROM scorers WHERE scorer = ? LIMIT 1\n" +
                "),\n" +
                "goals_per_year AS (\n" +
                "    SELECT YEAR(date) AS year, COUNT(*) AS goals\n" +
                "    FROM scorers\n" +
                "    WHERE team = (SELECT team FROM scorer_team)\n" +
                "    AND YEAR(date) >= ? AND YEAR(date) <= ?\n" +
                "    GROUP BY YEAR(date)\n" +
                "),\n" +
                "matches_per_year AS (\n" +
                "    SELECT YEAR(date) AS year, COUNT(*) AS matches\n" +
                "    FROM results\n" +
                "    WHERE (home_team = (SELECT team FROM scorer_team)\n" +
                "           OR away_team = (SELECT team FROM scorer_team))\n" +
                "    AND YEAR(date) >= ? AND YEAR(date) <= ?\n" +
                "    GROUP BY YEAR(date)\n" +
                ")\n" +
                "SELECT \n" +
                "    g.year,\n" +
                "    ROUND(1.0 * g.goals / m.matches, 5) AS totalGoalsGames\n" +
                "FROM goals_per_year g\n" +
                "JOIN matches_per_year m ON g.year = m.year\n" +
                "ORDER BY g.year;\n";
        return jdbcTemplate.queryForList(sql,name, fromYear, toYear, fromYear, toYear);
    }
}
