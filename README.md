# SimpleExchange API

SimpleExchange is a REST API for managing a dual-currency (PLN/USD) account, built with Java and Spring Boot.

## Features
1) User registration with an initial balance in PLN.
2) Currency exchange PLN/USD based on real bid/ask rates from the NBP API.
3) Fetching account details and current balances.

## Running the Application

The application is fully containerized, making the setup process straightforward.

### Prerequisites
1) Docker
2) Docker Compose

### Building & Running

```bash
docker-compose up -d
```

## Testing the Application

1) Open the Swagger UI in your web browser: http://localhost:8080/swagger-ui.html
2) Create a user using the POST /api/v1/users endpoint. Use the "Try it out" feature to send a JSON request. Remember the username and password you set.
3) Test the secured endpoints, such as GET /api/v1/accounts/{accountId} and POST /api/v1/accounts/{accountId}/exchange, using the accountId you received after registration. Authenticate with username and password from the registration.


## API Documentation

**POST /api/v1/users** - Registers a new user and creates initial account.

**GET /api/v1/accounts/{accountId}** - Fetches the details of a currency account.

**POST /api/v1/accounts/{accountId}/exchange** - Executes a currency exchange transaction.