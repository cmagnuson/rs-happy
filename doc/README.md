README
-------
Converts T+S TCP feed to RunScore Open format - RunScore can handle this faster and without crashing

This splits the location name sent and only uses everything to the left of the first `-`  
If there is no `-` then the entire location name is used.  
One T+S location can send to multiple RunScore Events, but using the `+` sign to separate them - for example `BikeOut+BikeIn` would send to both `BikeOut` and `BikeIn` RunScore events.