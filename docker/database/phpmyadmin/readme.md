# Instructions
1. `docker compose up` in this folder.
2. New terminal, `cd ../local-detached` and run `docker compose up` to spin up the asclepius database.
3. New terminal, `docker network inspect phpmyadmin_default` and copy the IP address of the container with the name `asclepiusdb-local-detached`.
4. Navigate to [http://localhost:8081](http://localhost:8081) and login with the following credentials:
	- Username: root
	- Password: root