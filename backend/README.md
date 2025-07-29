## How To Run Backend Stack via Docker Compose
### To run a new backend stack from scratch
1. Download docker desktop via : https://www.docker.com/products/docker-desktop/
2. After installing docker desktop (also install docker CLI as well), 
run compose by navigating to the root of the backend project (./backend) and running
compose up
   1. Navigate to ./backend
   2. Run CLI command ` docker compose up `

### To Delete Stack and Re-Run Backend Stack
1. Run `docker compose down` from root (./backend) to delete all resources associated to the backend
2. Re-run `docker compose up`


## Accessing API documentation
After running the containers to access the swagger documentation of the backend, simply navigate to:
http://localhost:8080/swagger-ui/index.html

## Accessing Database From Docker Container
To access the database created by the docker container here are the local url + credential information needed

<b>URL:</b> jdbc:mysql://localhost:3306/asclepius_db
<br> <b> username: </b> root 
<br> <b> password: </b> root

## Running Natively
To run the backend (mostly) natively, you will need to have the following installed:
1. OpenJDK 17
2. Maven 3.9.6
3. Docker (for the database)

After installing the above, you can run the backend by running the following commands:
1. In one terminal, start the database by running `docker compose up` from `/docker/sql/local-detached/`.
2. In another terminal, run `mvn spring-boot:run` from `/backend/`.


## Running Server
To run this on the server this is how we currently do it:
1. If not uploaded, upload the backend.service file to /etc/systemd/system
   a. this only needs to be done once
2. Package the project and upload the jar into the server
3. Move the jar file and overwrite what is in /opt/hayli/server
   a. Note if this location does not exist this must be created and ownership given `sudo chown -R ec2-user /opt/{name}`
4. `sudo systemctl stop backend.service`
5. `sudo systemctl start backend.service`
6. Check logs via /home/ubuntu/logs/backend
