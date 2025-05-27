package mye030_project.repositories;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class ResultRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Integer> findAllYears() {
        String sql = "SELECT DISTINCT(YEAR(date))FROM results;";
        return jdbcTemplate.queryForList(sql, Integer.class);
    }

    public List<Map<String, Object>> findFirstAndLastGame(Integer countryID) {
        String sql = "select  MIN(date) AS firstGame, MAX(date) AS lastGame from results where home_team = ? or away_team = ?" ;
        return jdbcTemplate.queryForList(sql,countryID, countryID);
    }


    public Map<String, Object> getResultsFromYearToYear(Integer countryId,int fromYear,int toYear) {
        String sql = "SELECT " +
                "SUM(CASE WHEN (home_team = ? AND home_score - away_score>0) OR (away_team = ? AND away_score - home_score>0) THEN 1 ELSE 0 END) AS wins, " +
                "SUM(CASE WHEN (home_team = ? AND home_score - away_score <0) OR (away_team = ? AND away_score - home_score<0) THEN 1 ELSE 0 END) AS losses, " +
                "SUM(CASE WHEN (home_team = ? OR away_team = ?) AND (home_score - away_score = 0) THEN 1 ELSE 0 END) AS draws, " +
                "SUM( CASE WHEN (home_team = ? OR away_team = ?) THEN 1 ELSE 0 END) AS totalMatches " +
                "FROM results " +
                "WHERE year(date) >= ? AND year(date) <= ?;";

        Map<String,Object> map = jdbcTemplate.queryForMap(sql, countryId, countryId,countryId, countryId,countryId, countryId, countryId, countryId, fromYear, toYear);

        sql = "SELECT " +
                "SUM(CASE WHEN (shootouts.winner = results.away_team AND results.home_team = ?) " +
                "OR (shootouts.winner = results.home_team AND results.away_team = ?) " +
                "THEN 1 ELSE 0 END) AS shootouts_losses, " +

                "SUM(CASE WHEN (shootouts.winner = results.home_team AND results.home_team = ?) " +
                "OR (shootouts.winner = results.away_team AND results.away_team = ?) " +
                "THEN 1 ELSE 0 END) AS shootouts_wins " +

                "FROM results " +
                "INNER JOIN shootouts ON results.home_team = shootouts.home_team " +
                "AND results.away_team = shootouts.away_team " +
                "AND results.date = shootouts.date " +
                "WHERE (YEAR(results.date) >= ? AND YEAR(results.date) <= ?) " +
                "AND (results.home_team = ? OR results.away_team = ?);";

        Map<String,Object> map1 =jdbcTemplate.queryForMap(sql, countryId,countryId,countryId,countryId,fromYear,toYear,countryId,countryId);
        map.putAll(map1);
        return map;

    }
    public Map<String, Object> getResultsFromYearToYearAsHomeTeam(Integer countryId, int fromYear, int toYear) {
        String sql = "SELECT SUM(CASE WHEN r.home_team = ? AND r.home_score - r.away_score > 0 THEN 1 ELSE 0 END) AS wins, " +
                "  SUM(CASE WHEN r.home_team = ? AND r.home_score - r.away_score < 0 THEN 1 ELSE 0 END) AS losses, " +
                "  SUM(CASE WHEN r.home_team = ? AND r.home_score - r.away_score = 0 THEN 1 ELSE 0 END) AS draws, " +
                "  SUM(CASE WHEN r.home_team = ? THEN 1 ELSE 0 END) AS totalHomeMatches " +
                "FROM results r " +
                "WHERE YEAR(r.date) >= ? AND YEAR(r.date) <= ?;";

        Map<String,Object> map = jdbcTemplate.queryForMap(sql, countryId, countryId, countryId, countryId, fromYear, toYear);

        sql = "SELECT SUM(CASE WHEN shootouts.winner = results.away_team THEN 1 ELSE 0 END) AS shootouts_losses," +
            "  SUM(CASE WHEN shootouts.winner = results.home_team THEN 1 ELSE 0 END) AS shootouts_wins" +
                " FROM results INNER JOIN shootouts     " +
                "ON results.home_team = shootouts.home_team     " +
                "AND results.away_team = shootouts.away_team     " +
                "AND results.date = shootouts.date WHERE YEAR(results.date) >= ?    " +
                "AND YEAR(results.date) <= ?     AND results.home_team = ?;";
        Map<String,Object> map1 =jdbcTemplate.queryForMap(sql, fromYear,toYear,countryId);
        map.putAll(map1);
        return map;
    }

    public Map<String, Object> getResultsFromYearToYearAsAwayTeam(Integer countryId,int fromYear,int toYear) {
        String sql = " SELECT SUM(CASE WHEN (away_team = ? AND home_score - away_score>0)THEN 1 ELSE 0 END) AS losses, SUM(CASE WHEN (away_team = ? AND home_score - away_score < 0) THEN 1 ELSE 0 END) AS wins, SUM(CASE WHEN away_team = ? AND (home_score - away_score = 0) THEN 1 ELSE 0 END) AS draws, SUM( CASE WHEN away_team = ? THEN 1 ELSE 0 END) AS totalAwayMatches FROM results WHERE year(date) >= ? AND year(date) <= ?; ";
        Map<String,Object> map = jdbcTemplate.queryForMap(sql, countryId, countryId, countryId, countryId, fromYear, toYear);

        sql = "SELECT SUM(CASE WHEN shootouts.winner = results.away_team THEN 1 ELSE 0 END) AS shootouts_wins," +
                "  SUM(CASE WHEN shootouts.winner = results.home_team THEN 1 ELSE 0 END) AS shootouts_losses" +
                " FROM results INNER JOIN shootouts     ON results.home_team = shootouts.home_team     AND results.away_team = shootouts.away_team     AND results.date = shootouts.date WHERE YEAR(results.date) >= ?    AND YEAR(results.date) <= ?     AND results.away_team = ?;";
        Map<String,Object> map1 =jdbcTemplate.queryForMap(sql, fromYear,toYear,countryId);
        map.putAll(map1);

        return map;
    }
    public List<Map<String, Object>> getAllMatchesFromYear(Integer countryId, Integer fromYear, Integer toYear) {
        String sql = "SELECT r.date,ch.Display_Name AS home_team_name, ca.Display_Name AS away_team_name, r.Home_score,r.Away_score,r.Tournament,r.City, r.Neutral, " +
                "CASE " +
                "WHEN (r.home_score-r.away_score = 0) THEN 'Draw'\n" +
                "WHEN (r.home_score-r.away_score > 0) THEN ch.Display_Name\n" +
                "WHEN (r.home_score-r.away_score < 0) THEN ca.Display_Name\n" +
                "END AS winner " +
                "FROM results r " +
                "LEFT JOIN countries ch ON r.home_team = ch.id  " +
                "LEFT JOIN countries ca ON r.away_team = ca.id  " +
                "WHERE year(r.date) >= ? AND year(r.date) <= ? AND (r.away_team = ? OR r.home_team = ?)";
        return jdbcTemplate.queryForList(sql, fromYear, toYear, countryId, countryId);
    }
    public List<Map<String, Object>> getResultsPerYear(Integer countryId, Integer fromYear, Integer toYear) {
        String sql ="SELECT \n" +
                "    YEAR(r.date) AS year, \n" +
                "    SUM(CASE WHEN r.home_score = r.away_score THEN 1 ELSE 0 END) AS draw,\n" +
                "    SUM(CASE WHEN (r.home_score > r.away_score AND r.home_team = ?) \n" +
                "             OR (r.away_score > r.home_score AND r.away_team = ?) THEN 1 ELSE 0 END) AS wins,\n" +
                "    SUM(CASE WHEN (r.home_score > r.away_score AND r.away_team = ?) \n" +
                "             OR (r.away_score > r.home_score AND r.home_team = ?) THEN 1 ELSE 0 END) AS losses\n" +
                "FROM results r\n" +
                "WHERE (r.home_team = ? OR r.away_team = ?)\n" +
                "  AND YEAR(r.date) BETWEEN ? AND ? \n" +
                "GROUP BY YEAR(r.date);\n";
        return jdbcTemplate.queryForList(sql, countryId,countryId,countryId,countryId ,countryId,countryId,fromYear, toYear);
    }

    public List<Map<String, Object>> getTop10ByWins() {
        String sql =
                "WITH match_results AS (" +
                        "    SELECT " +
                        "        home_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN home_score > away_score THEN 'win' " +
                        "            WHEN home_score = away_score THEN 'draw' " +
                        "            WHEN home_score < away_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "    UNION ALL " +
                        "    SELECT " +
                        "        away_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN away_score > home_score THEN 'win' " +
                        "            WHEN away_score = home_score THEN 'draw' " +
                        "            WHEN away_score < home_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "), " +
                        "team_stats AS (" +
                        "    SELECT " +
                        "        team, " +
                        "        COUNT(*) AS total_matches, " +
                        "        SUM(CASE WHEN result = 'win' THEN 1 ELSE 0 END) AS wins, " +
                        "        SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) AS draws, " +
                        "        SUM(CASE WHEN result = 'loss' THEN 1 ELSE 0 END) AS losses " +
                        "    FROM match_results " +
                        "    GROUP BY team " +
                        ") " +
                        "SELECT " +
                        "    c.Display_Name AS country, " +
                        "    ts.total_matches AS total_matches, " +
                        "    ts.wins AS wins, " +
                        "    ts.draws AS draws, " +
                        "    ts.losses AS losses " +
                        "FROM countries c " +
                        "LEFT JOIN team_stats ts ON c.id = ts.team " +
                        "ORDER BY ts.wins DESC " +
                        "LIMIT 10";
        return jdbcTemplate.queryForList(sql);
    }
    public List<Map<String, Object>> getTop10ByScore() {
        String sql =
                "WITH match_results AS (" +
                        "    SELECT " +
                        "        home_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN home_score > away_score THEN 'win' " +
                        "            WHEN home_score = away_score THEN 'draw' " +
                        "            WHEN home_score < away_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "    UNION ALL " +
                        "    SELECT " +
                        "        away_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN away_score > home_score THEN 'win' " +
                        "            WHEN away_score = home_score THEN 'draw' " +
                        "            WHEN away_score < home_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "), " +
                        "team_stats AS (" +
                        "    SELECT " +
                        "        team, " +
                        "        COUNT(*) AS total_matches, " +
                        "        SUM(CASE WHEN result = 'win' THEN 1 ELSE 0 END) AS wins, " +
                        "        SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) AS draws, " +
                        "        SUM(CASE WHEN result = 'loss' THEN 1 ELSE 0 END) AS losses, " +
                        "        SUM(CASE WHEN  result = 'win' THEN 3  " +
                        "                 WHEN result = 'draw' THEN 1 ELSE 0 END) AS points " +
                        "    FROM match_results " +
                        "    GROUP BY team " +
                        ") " +
                        "SELECT " +
                        "    c.Display_Name AS country, " +
                        "    ts.total_matches AS total_matches, " +
                        "    ts.wins AS wins, " +
                        "    ts.draws AS draws, " +
                        "    ts.losses AS losses, " +
                        "    ts.points AS points " +
                        "FROM countries c " +
                        "LEFT JOIN team_stats ts ON c.id = ts.team " +
                        "ORDER BY ts.points DESC " +
                        "LIMIT 10";
        return jdbcTemplate.queryForList(sql);
    }
    public List<Map<String, Object>> getTop10ByWinsYears() {
        String sql =
                "WITH match_results AS (" +
                        "    SELECT " +
                        "date," +
                        "        home_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN home_score > away_score THEN 'win' " +
                        "            WHEN home_score = away_score THEN 'draw' " +
                        "            WHEN home_score < away_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "    UNION ALL " +
                        "    SELECT " +
                        "date," +
                        "        away_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN away_score > home_score THEN 'win' " +
                        "            WHEN away_score = home_score THEN 'draw' " +
                        "            WHEN away_score < home_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "), " +
                        "team_stats AS (" +
                        "    SELECT " +
                        "        YEAR(MAX(date)) AS toYear, " +
                        "        YEAR(MIN(date)) AS fromYear, " +
                        "        team, " +
                        "        COUNT(*) AS total_matches, " +
                        "        SUM(CASE WHEN result = 'win' THEN 1 ELSE 0 END) AS wins, " +
                        "        SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) AS draws, " +
                        "        SUM(CASE WHEN result = 'loss' THEN 1 ELSE 0 END) AS losses " +
                        "    FROM match_results " +
                        "    GROUP BY team " +
                        ") " +
                        "SELECT " +
                        "    c.Display_Name AS country, " +
                        "ts.fromYear," +
                        "ts.toYear," +
                        "    ts.total_matches AS total_matches, " +
                        "    ts.wins AS wins, " +
                        "    ts.draws AS draws, " +
                        "    ts.losses AS losses," +
                        "    ts.wins/(ts.toYear-ts.fromYear) as winsYears " +
                        "FROM countries c " +
                        "LEFT JOIN team_stats ts ON c.id = ts.team " +
                        "ORDER BY ts.wins/(ts.toYear-ts.fromYear) DESC " +
                        "LIMIT 10";
        return jdbcTemplate.queryForList(sql);

    }
    public List<Map<String, Object>> getTop10ByScoreYears() {
        String sql =
                "WITH match_results AS (" +
                        "    SELECT " +
                        "        date, " +
                        "        home_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN home_score > away_score THEN 'win' " +
                        "            WHEN home_score = away_score THEN 'draw' " +
                        "            WHEN home_score < away_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "    UNION ALL " +
                        "    SELECT " +
                        "        date, " +
                        "        away_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN away_score > home_score THEN 'win' " +
                        "            WHEN away_score = home_score THEN 'draw' " +
                        "            WHEN away_score < home_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "), " +
                        "team_stats AS (" +
                        "    SELECT " +
                        "        YEAR(MAX(date)) AS toYear, " +
                        "        YEAR(MIN(date)) AS fromYear, " +
                        "        team, " +
                        "        COUNT(*) AS total_matches, " +
                        "        SUM(CASE WHEN result = 'win' THEN 1 ELSE 0 END) AS wins, " +
                        "        SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) AS draws, " +
                        "        SUM(CASE WHEN result = 'loss' THEN 1 ELSE 0 END) AS losses, " +
                        "        SUM(CASE WHEN  result = 'win' THEN 3  " +
                        "                 WHEN result = 'draw' THEN 1 ELSE 0 END) AS points " +
                        "    FROM match_results " +
                        "    GROUP BY team " +
                        ") " +
                        "SELECT " +
                        "    c.Display_Name AS country," +
                        "   ts.fromYear," +
                        "   ts.toYear, " +
                        "    ts.total_matches AS total_matches, " +
                        "    ts.wins AS wins, " +
                        "    ts.draws AS draws, " +
                        "    ts.losses AS losses, " +
                        "    ts.points AS points," +
                        "    ts.points/(ts.toYear-ts.fromYear) AS pointsYears " +
                        "FROM countries c " +
                        "LEFT JOIN team_stats ts ON c.id = ts.team " +
                        "ORDER BY  ts.points/(ts.toYear-ts.fromYear) DESC " +
                        "LIMIT 10";
        return jdbcTemplate.queryForList(sql);
    }
    public List<Map<String, Object>> getWinsArea() {
        String sql =
                "WITH match_results AS (" +
                        "    SELECT " +
                        "date," +
                        "        home_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN home_score > away_score THEN 'win' " +
                        "            WHEN home_score = away_score THEN 'draw' " +
                        "            WHEN home_score < away_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "    UNION ALL " +
                        "    SELECT " +
                        "date," +
                        "        away_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN away_score > home_score THEN 'win' " +
                        "            WHEN away_score = home_score THEN 'draw' " +
                        "            WHEN away_score < home_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "), " +
                        "team_stats AS (" +
                        "    SELECT " +
                        "        YEAR(MAX(date)) AS toYear, " +
                        "        YEAR(MIN(date)) AS fromYear, " +
                        "        team, " +
                        "        COUNT(*) AS total_matches, " +
                        "        SUM(CASE WHEN result = 'win' THEN 1 ELSE 0 END) AS wins, " +
                        "        SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) AS draws, " +
                        "        SUM(CASE WHEN result = 'loss' THEN 1 ELSE 0 END) AS losses " +
                        "    FROM match_results " +
                        "    GROUP BY team " +
                        ") " +
                        "SELECT " +
                        "    c.Display_Name AS country, " +
                        "   c.Area_SqKm AS area," +
                        "    ts.wins AS wins " +
                        "FROM countries c " +
                        "LEFT JOIN team_stats ts ON c.id = ts.team " ;
        return jdbcTemplate.queryForList(sql);

    }    public List<Map<String, Object>> getPointsPopulation() {
        String sql =
                "WITH match_results AS (" +
                        "    SELECT " +
                        "        home_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN home_score > away_score THEN 'win' " +
                        "            WHEN home_score = away_score THEN 'draw' " +
                        "            WHEN home_score < away_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "    UNION ALL " +
                        "    SELECT " +
                        "        away_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN away_score > home_score THEN 'win' " +
                        "            WHEN away_score = home_score THEN 'draw' " +
                        "            WHEN away_score < home_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                        "), " +
                        "team_stats AS (" +
                        "    SELECT " +
                        "        team, " +
                        "        COUNT(*) AS total_matches, " +
                        "        SUM(CASE WHEN result = 'win' THEN 1 ELSE 0 END) AS wins, " +
                        "        SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) AS draws, " +
                        "        SUM(CASE WHEN result = 'loss' THEN 1 ELSE 0 END) AS losses, " +
                        "        SUM(CASE WHEN  result = 'win' THEN 3  " +
                        "                 WHEN result = 'draw' THEN 1 ELSE 0 END) AS points " +
                        "    FROM match_results " +
                        "    GROUP BY team " +
                        ") " +
                        "SELECT " +
                        "    c.Display_Name AS country," +
                        "   c.Population AS population," +
                        "    ts.points AS points" +
                        "   FROM countries c " +
                        "   LEFT JOIN team_stats ts ON c.id = ts.team ";
        return jdbcTemplate.queryForList(sql);
    }
    public Integer getTotalMatches(Integer year){
        String sql = "SELECT COUNT(*) AS totalMatches FROM results WHERE YEAR(date) = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, year);
    }
    public Integer getTotalDraws(Integer year){
        String sql = "SELECT COUNT(*) AS totalMatches FROM results WHERE YEAR(date) = ? AND home_score = away_score";
        return jdbcTemplate.queryForObject(sql, Integer.class, year);
    }
    public Integer getTotalMatchesWithPenalties(Integer year){
        String sql = "SELECT COUNT(*) AS totalMatches FROM shootouts WHERE YEAR(date) = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, year);
    }
    public List<Map<String,Object>> getTotalMatchesByCountry(Integer year){
        String sql =
                        "WITH match_results AS (" +
                        "    SELECT " +
                        "        home_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN home_score > away_score THEN 'win' " +
                        "            WHEN home_score = away_score THEN 'draw' " +
                        "            WHEN home_score < away_score THEN 'loss' " +
                        "        END AS result " +
                        "    FROM results " +
                                "WHERE YEAR(date) = ? " +
                        "    UNION ALL " +
                        "    SELECT " +
                        "        away_team AS team, " +
                        "        home_score, " +
                        "        away_score, " +
                        "        CASE " +
                        "            WHEN away_score > home_score THEN 'win' " +
                        "            WHEN away_score = home_score THEN 'draw' " +
                        "            WHEN away_score < home_score THEN 'loss' " +
                        "        END AS result " +
                        "       FROM results " +
                                " WHERE YEAR(date) = ? " +
                        "), " +
                                "penalty_stats AS (" +
                                "    SELECT " +
                                "        home_team AS team " +
                                "    FROM shootouts " +
                                " WHERE YEAR(date) = ? " +
                                "    UNION ALL " +
                                "    SELECT " +
                                "        away_team AS team " +
                                "       FROM shootouts " +
                                " WHERE YEAR(date) = ? " +

                                "), penalties  AS (" +
                                "    SELECT " +
                                "        team, " +
                                " COUNT(*) AS penalties" +
                                " FROM penalty_stats " +
                                " GROUP BY team ), " +
                        " team_stats AS (" +
                        "    SELECT " +
                        "        team, " +
                        "        COUNT(*) AS total_matches, " +
                        "        SUM(CASE WHEN result = 'win' THEN 1 ELSE 0 END) AS wins, " +
                        "        SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) AS draws, " +
                        "        SUM(CASE WHEN result = 'loss' THEN 1 ELSE 0 END) AS losses " +
                        "    FROM match_results " +
                        "    GROUP BY team " +
                        ") "+
                        " SELECT " +
                        " c.Display_Name AS country, c.Region_Name AS region, c.Developed_or_Developing AS developedOrDeveloping,c.Status AS status,c.Sub_region_Name AS subRegion, " +
                        "    ts.total_matches AS total_matches, " +
                        "    ts.wins AS wins, " +
                        "    ts.draws AS draws, " +
                        "    ts.losses AS losses, " +
                                " p.penalties AS penalties " +
                        "FROM countries c " +
                        "INNER JOIN team_stats ts ON c.id = ts.team " +
                                " LEFT JOIN penalties p ON p.team = c.id "
                                + " ORDER BY ts.total_matches DESC ;";

        return jdbcTemplate.queryForList(sql, year,year,year,year);
    }
}
