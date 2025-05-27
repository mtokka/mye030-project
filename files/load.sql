USE mye030_project;

LOAD DATA LOCAL INFILE '/home/melina/Documents/cs/mye030/project2025/files/final_countries.csv'
INTO TABLE countries
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/home/melina/Documents/cs/mye030/project2025/files/final_results.csv'
INTO TABLE results
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/home/melina/Documents/cs/mye030/project2025/files/final_shootouts.csv'
INTO TABLE shootouts
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/home/melina/Documents/cs/mye030/project2025/files/final_goalscorers.csv'
INTO TABLE scorers
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES;
