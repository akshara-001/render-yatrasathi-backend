# YatraSathi Backend — Spring Boot Migration

This repository contains the Spring Boot 3.x rewrite of the YatraSathi Express.js backend.

## Tech Stack
- Java 17+ / Spring Boot 3.2.3
- Spring Data MongoDB
- Spring Security (BCrypt password encoding)
- JJWT 0.12.5 (JSON Web Tokens)
- Cloudinary Java SDK (Direct memory upload)
- Spring WebFlux WebClient (Amadeus OAuth2 & Flight Search API integration)
- Lombok

## Environment Variables

The application can be configured via environment variables or `application.yml`:

| Environment Variable | Default Value | Description |
|---------------------|---------------|-------------|
| `PORT` | `5000` | Server listening port |
| `MONGO_URI` | `mongodb://localhost:27017/yatrasathi` | MongoDB connection string |
| `AMADEUS_CLIENT_ID` | `""` | Amadeus API key |
| `AMADEUS_CLIENT_SECRET` | `""` | Amadeus API secret |
| `CLOUDINARY_CLOUD_NAME` | `""` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | `""` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | `""` | Cloudinary API secret |
| `JWT_SECRET` | `mysupersecretkey123` | Secret key for signing JWT tokens |

## How to Build & Run

### Build package:
```bash
mvn clean package -DskipTests
```

### Run locally:
```bash
mvn spring-boot:run
```
Or run the packaged jar:
```bash
java -jar target/yatrasathi-backend-1.0.0.jar
```

## API Contract Summary

All endpoints maintain 100% compatibility with the original Node.js Express implementation:

- `POST /api/auth/register` — Register user
- `POST /api/auth/login` — Login user (returns JWT token and user info)
- `GET /api/travel/flights?from=&to=&date=` — Flight search (Amadeus + fallback)
- `GET /api/travel/trains?from=&to=&date=` — Train search (static dham data)
- `GET /api/travel/buses?from=&to=&date=` — Bus search (static dham data)
- `POST /api/gallery/upload` — Upload photo (multipart form)
- `GET /api/gallery/{dhamName}` — Fetch photos by dham
- `PUT /api/gallery/{id}/like` — Toggle like on photo
