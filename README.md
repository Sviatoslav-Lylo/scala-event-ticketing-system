# Event Ticketing & Revenue Analytics

An immutable event-ticketing and analytics application written in Scala 3. It models events, participants, and tickets, with composable pricing rules and revenue reports.

## Features

- Validates event data and represents failed purchases with typed `Either` errors.
- Models ticket purchases as new state, leaving the existing event system unchanged.
- Composes student discounts and service fees using Scala trait linearization.
- Uses `BigDecimal` for ticket prices and revenue.
- Calculates ticket revenue, capacity-weighted occupancy, and a simple revenue forecast.
- Includes tests for purchases, validation, pricing, immutability, and analytics.

## Functional programming concepts

The project keeps domain data immutable: operations such as buying a ticket return an updated `EventManager` instead of changing the existing one. Collection operations such as `map`, `filter`, `flatMap`, and `foldLeft` handle event updates and analytics. `Option` represents searches that may not find an event, while `Either` returns typed validation and purchase errors. Pricing rules are composed from traits, demonstrating Scala trait linearization; a monoid combines individual financial reports into a global report.

## Project structure

```text
.
├── build.sbt
├── project/
│   └── build.properties
└── src/
    ├── main/scala/events/
    │   ├── Analytics.scala
    │   ├── EventManager.scala
    │   ├── Main.scala
    │   ├── Models.scala
    │   └── Traits.scala
    └── test/scala/events/
        └── EventSystemSuite.scala
```

- `Models.scala` defines events, participants, tickets, and domain errors.
- `EventManager.scala` implements immutable event management and ticket purchases.
- `Traits.scala` contains composable pricing rules.
- `Analytics.scala` calculates revenue reports, occupancy, and forecasts.
- `Main.scala` is the command-line demo entry point.
- `EventSystemSuite.scala` tests the main domain and analytics behaviors.
- `build.sbt` and `project/build.properties` configure Scala and sbt.

## Run

Requirements: Java 17 or newer and sbt 2.0.8 (pinned in `project/build.properties`).

Run these commands from this project directory:

```shell
sbt run
sbt 'testOnly *EventSystemSuite'
```

## Scope

This is a learning project and an in-memory command-line demo. It does not include persistent storage, a web interface, authentication, concurrency control, or payment processing. Its forecast is an average-revenue projection, not a predictive model.
