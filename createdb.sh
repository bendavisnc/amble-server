echo 'Initializing amble SQLite database'; 
mkdir -p amble-db; 
sqlite3 amble-db/amble.db < migrations/initfreshdb.sql;