package mye030_project.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CountryRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> findAll() {
        String sql = "select * from countries";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("Display_Name"));
    }
    public Integer findId(String name) {
        String sql = "select id from countries where Display_Name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }

}
