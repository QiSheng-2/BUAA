# Chatroom

A simple WebSocket + Redis Pub/Sub chatroom built with Spring Boot.

## Run

Make sure Redis is running locally on port 6379, then from the project root run:

```powershell
mvnw.cmd spring-boot:run
```

Then open:

- http://localhost:8080/index.html

Open the page in multiple browser windows to see messages broadcast in real time.

