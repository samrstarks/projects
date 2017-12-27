-- Question 5 Setup
DROP TABLE IF EXISTS q5_edges;
CREATE TABLE q5_edges(src varchar(2), dest varchar(2), length integer);
INSERT INTO q5_edges VALUES
('AL','FL',1),
('AL','GA',1),
('AL','MS',1),
('AL','TN',1),
('AR','LA',1),
('AR','MO',1),
('AR','MS',1),
('AR','OK',1),
('AR','TN',1),
('AR','TX',1),
('AZ','CA',1),
('AZ','NM',1),
('AZ','NV',1),
('AZ','UT',1),
('CA','AZ',1),
('CA','NV',1),
('CA','OR',1),
('CO','KS',1),
('CO','NE',1),
('CO','NM',1),
('CO','OK',1),
('CO','UT',1),
('CO','WY',1),
('CT','MA',1),
('CT','NY',1),
('CT','RI',1),
('DC','MD',1),
('DC','VA',1),
('DE','MD',1),
('DE','NJ',1),
('DE','PA',1),
('FL','AL',1),
('FL','GA',1),
('GA','AL',1),
('GA','FL',1),
('GA','NC',1),
('GA','SC',1),
('GA','TN',1),
('IA','IL',1),
('IA','MN',1),
('IA','MO',1),
('IA','NE',1),
('IA','SD',1),
('IA','WI',1),
('ID','MT',1),
('ID','NV',1),
('ID','OR',1),
('ID','UT',1),
('ID','WA',1),
('ID','WY',1),
('IL','IA',1),
('IL','IN',1),
('IL','KY',1),
('IL','MO',1),
('IL','WI',1),
('IN','IL',1),
('IN','KY',1),
('IN','MI',1),
('IN','OH',1),
('KS','CO',1),
('KS','MO',1),
('KS','NE',1),
('KS','OK',1),
('KY','IL',1),
('KY','IN',1),
('KY','MO',1),
('KY','OH',1),
('KY','TN',1),
('KY','VA',1),
('KY','WV',1),
('LA','AR',1),
('LA','MS',1),
('LA','TX',1),
('MA','CT',1),
('MA','NH',1),
('MA','NY',1),
('MA','RI',1),
('MA','VT',1),
('MD','DC',1),
('MD','DE',1),
('MD','PA',1),
('MD','VA',1),
('MD','WV',1),
('ME','NH',1),
('MI','IN',1),
('MI','OH',1),
('MI','WI',1),
('MN','IA',1),
('MN','ND',1),
('MN','SD',1),
('MN','WI',1),
('MO','AR',1),
('MO','IA',1),
('MO','IL',1),
('MO','KS',1),
('MO','KY',1),
('MO','NE',1),
('MO','OK',1),
('MO','TN',1),
('MS','AL',1),
('MS','AR',1),
('MS','LA',1),
('MS','TN',1),
('MT','ID',1),
('MT','ND',1),
('MT','SD',1),
('MT','WY',1),
('NC','GA',1),
('NC','SC',1),
('NC','TN',1),
('NC','VA',1),
('ND','MN',1),
('ND','MT',1),
('ND','SD',1),
('NE','CO',1),
('NE','IA',1),
('NE','KS',1),
('NE','MO',1),
('NE','SD',1),
('NE','WY',1),
('NH','MA',1),
('NH','ME',1),
('NH','VT',1),
('NJ','DE',1),
('NJ','NY',1),
('NJ','PA',1),
('NM','AZ',1),
('NM','CO',1),
('NM','OK',1),
('NM','TX',1),
('NV','AZ',1),
('NV','CA',1),
('NV','ID',1),
('NV','OR',1),
('NV','UT',1),
('NY','CT',1),
('NY','MA',1),
('NY','NJ',1),
('NY','PA',1),
('NY','VT',1),
('OH','IN',1),
('OH','KY',1),
('OH','MI',1),
('OH','PA',1),
('OH','WV',1),
('OK','AR',1),
('OK','CO',1),
('OK','KS',1),
('OK','MO',1),
('OK','NM',1),
('OK','TX',1),
('OR','CA',1),
('OR','ID',1),
('OR','NV',1),
('OR','WA',1),
('PA','DE',1),
('PA','MD',1),
('PA','NJ',1),
('PA','NY',1),
('PA','OH',1),
('PA','WV',1),
('RI','CT',1),
('RI','MA',1),
('SC','GA',1),
('SC','NC',1),
('SD','IA',1),
('SD','MN',1),
('SD','MT',1),
('SD','ND',1),
('SD','NE',1),
('SD','WY',1),
('TN','AL',1),
('TN','AR',1),
('TN','GA',1),
('TN','KY',1),
('TN','MO',1),
('TN','MS',1),
('TN','NC',1),
('TN','VA',1),
('TX','AR',1),
('TX','LA',1),
('TX','NM',1),
('TX','OK',1),
('UT','AZ',1),
('UT','CO',1),
('UT','ID',1),
('UT','NV',1),
('UT','WY',1),
('VA','DC',1),
('VA','KY',1),
('VA','MD',1),
('VA','NC',1),
('VA','TN',1),
('VA','WV',1),
('VT','MA',1),
('VT','NH',1),
('VT','NY',1),
('WA','ID',1),
('WA','OR',1),
('WI','IA',1),
('WI','IL',1),
('WI','MI',1),
('WI','MN',1),
('WV','KY',1),
('WV','MD',1),
('WV','OH',1),
('WV','PA',1),
('WV','VA',1),
('WY','CO',1),
('WY','ID',1),
('WY','MT',1),
('WY','NE',1),
('WY','SD',1),
('WY','UT',1)           
;

DROP TABLE IF EXISTS q5_paths;
CREATE TABLE q5_paths
AS
    SELECT *, ARRAY[src, dest] AS path 
      FROM q5_edges
;

DROP TABLE IF EXISTS q5_paths_to_update;
CREATE TABLE q5_paths_to_update
AS
    SELECT * FROM q5_paths
;
