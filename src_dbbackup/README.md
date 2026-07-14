# amble-dbbackup 

Provides two main functions, `amble-dbbackup.startupread.main/-main` and `amble-dbbackup.shutdownwrite.main/-main` that are meant to be called at the time the server starts and stops. 

At server startup time, a new repo is cloned from `amble-dbbackup.config/dbbackup-remote` which should provide the sqlite db file.

At server shutdown time, the repo's remote is pushed with whatever new db changes.
