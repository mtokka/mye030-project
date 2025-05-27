import pandas as pd

countries = pd.read_csv('countries.csv', encoding='utf-16')
results = pd.read_csv('results.csv', encoding='utf-16')
scorers = pd.read_csv('goalscorers.csv', encoding='utf-16')
penalties = pd.read_csv('shootouts.csv', encoding='utf-16')
former_names = pd.read_csv('former_names.csv', encoding='utf-16')

former_names.loc[former_names['current'] == 'DR Congo', 'current'] = 'Democratic Republic of the Congo'
former_countries = dict(zip(former_names['former'], former_names['current']))

results['home_team'] = results['home_team'].map(former_countries).combine_first(results['home_team'])
results['away_team'] = results['away_team'].map(former_countries).combine_first(results['away_team'])

penalties['home_team'] = penalties['home_team'].map(former_countries).combine_first(penalties['home_team'])
penalties['away_team'] = penalties['away_team'].map(former_countries).combine_first(penalties['away_team'])
penalties['winner'] = penalties['winner'].map(former_countries).combine_first(penalties['winner'])
penalties['first_shooter'] = penalties['first_shooter'].map(former_countries).combine_first(penalties['first_shooter'])

scorers['home_team'] = scorers['home_team'].map(former_countries).combine_first(scorers['home_team'])
scorers['away_team'] = scorers['away_team'].map(former_countries).combine_first(scorers['away_team'])

valid_countries = set(countries['Display_Name'])

correct_names = {
    "Czech Republic": "Czechia",
    "China PR": "China",
    "Vatican City": "Vatican",
    "Republic of Ireland": "Ireland",
    "DR Congo": "Democratic Republic of the Congo",
    "Åland Islands": "Aland Islands",
    "Åland": "Aland Islands"
}

results.replace({'home_team': correct_names, 'away_team': correct_names}, inplace=True)
scorers.replace({'home_team': correct_names, 'away_team': correct_names,'team':correct_names}, inplace=True)
penalties.replace({'home_team': correct_names, 'away_team': correct_names,'winner': correct_names, 'first_shooter': correct_names}, inplace=True)

results['home_fk_violations'] = ~results['home_team'].isin(valid_countries)
results['away_fk_violations'] = ~results['away_team'].isin(valid_countries)
penalties['home_fk_violations'] = ~penalties['home_team'].isin(valid_countries)
penalties['away_fk_violations'] = ~penalties['away_team'].isin(valid_countries)
scorers['home_fk_violations'] = ~scorers['home_team'].isin(valid_countries)
scorers['away_fk_violations'] = ~scorers['away_team'].isin(valid_countries)
violations = set(results.loc[results['home_fk_violations'], 'home_team']) | set(results.loc[results['away_fk_violations'], 'away_team'])
violations |= set(penalties.loc[penalties['home_fk_violations'], 'home_team']) | set(penalties.loc[penalties['away_fk_violations'], 'away_team'])
violations |= set(scorers.loc[scorers['home_fk_violations'], 'home_team']) | set(scorers.loc[scorers['away_fk_violations'], 'away_team'])
violations -= valid_countries

new_countries = [{'Display_Name': name, 'Official_Name': name} for name in violations]

if new_countries:
    countries = pd.concat([countries, pd.DataFrame(new_countries)], ignore_index=True)

countries['id'] = range(1, len(countries) + 1)

countries_dict = dict(zip(countries['Display_Name'], countries['id']))
results.replace({'home_team': countries_dict, 'away_team': countries_dict}, inplace=True)
penalties.replace({'home_team': countries_dict, 'away_team': countries_dict, 'winner': countries_dict, 'first_shooter': countries_dict}, inplace=True)
scorers.replace({'home_team': countries_dict, 'away_team': countries_dict,'team':countries_dict}, inplace=True)

penalties.fillna(-1, inplace=True)

results.drop(columns=['home_fk_violations', 'away_fk_violations'], inplace=True)
penalties.drop(columns=['home_fk_violations', 'away_fk_violations'], inplace=True)
scorers.drop(columns=['home_fk_violations', 'away_fk_violations'], inplace=True)

scorers['own_goal'].replace({True: 1, False: 0}, inplace=True)
scorers['penalty'].replace({True: 1, False: 0}, inplace=True)
scorers.fillna(-1,inplace=True)
results = results.astype({'home_team': int, 'away_team': int})
penalties = penalties.astype({'home_team': int, 'away_team': int, 'winner': int, 'first_shooter': int})
scorers = scorers.astype({'home_team': int, 'away_team': int,'team':int,'own_goal':int,'penalty':int})

countries = countries.fillna(-1)


countries.drop(columns = ['Small Island Developing States (SIDS)', 'Land Locked Developing Countries (LLDC)', 'Least Developed Countries (LDC)'],inplace=True)

results['neutral'] = results['neutral'].astype(int)


valid_matches = set(zip(results['date'], results['home_team'], results['away_team']))
mismatched_penalties = penalties[~penalties[['date', 'home_team', 'away_team']].apply(tuple, axis=1).isin(valid_matches)]
penalties.drop(index=mismatched_penalties.index, inplace=True)

mismatched_penalties.to_csv('quarantine.csv',encoding='utf-8', index=False)
countries.to_csv('final_countries.csv', encoding='utf-8',index=False)
results.to_csv('final_results.csv', encoding='utf-8',index=False)
penalties.to_csv('final_shootouts.csv', encoding='utf-8',index=False)
scorers.to_csv('final_goalscorers.csv', encoding='utf-8',index=False)