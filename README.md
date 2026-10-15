# Event Ticketing & Revenue Analytics

An immutable event-ticketing and analytics application written in Scala 3. It models events, participants, and tickets, with composable pricing rules and revenue reports.

## Features

- Validates event data and represents failed purchases with typed `Either` errors.
- Models ticket purchases as new state, leaving the existing event system unchanged.
- Composes student discounts and service fees using Scala trait linearization.
- Uses `BigDecimal` for ticket prices and revenue.
- Calculates ticket revenue, capacity-weighted occupancy, and a simple revenue forecast.
- Includes tests for purchases, validation, pricing, immutability, and analytics.

## Run

Requirements: Java 17 or newer and sbt 2.0.8 (pinned in `project/build.properties`).

Run these commands from this project directory:

```shell
sbt run
sbt 'testOnly *EventSystemSuite'
```

## Scope

This is a learning project and an in-memory command-line demo. It does not include persistent storage, a web interface, authentication, concurrency control, or payment processing. Its forecast is an average-revenue projection, not a predictive model.
