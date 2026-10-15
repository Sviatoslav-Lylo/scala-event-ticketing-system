package events

@main def runApp(): Unit = {
  println("=== Event ticketing and analytics demo ===")

  val initialSystem = new EventManager()

  val eventResult = Event.create(
    id = "E-001",
    title = "Scala & AI Engineering Conference",
    category = EventCategory.Conference,
    maxCapacity = 100,
    basePrice = BigDecimal("1000.00")
  )

  val systemWithEvent = eventResult match {
    case Right(event) => initialSystem.addEvent(event)
    case Left(error) =>
      println(s"Could not create event: $error")
      initialSystem
  }

  val participant =
    Participant("U-001", "Demo Participant", "participant@example.com")
  val systemWithParticipant = systemWithEvent.addParticipant(participant)
  val studentPricing = new StandardPrice with StudentDiscount with ServiceFee

  println("\n--- Ticket purchase ---")
  val purchaseResult = systemWithParticipant.buyTicket(
    eventId = "E-001",
    participantId = "U-001",
    tier = TicketTier.Student,
    priceCalculator = studentPricing
  )

  val finalSystem = purchaseResult match {
    case Right((updatedSystem, ticket)) =>
      println(s"Ticket ${ticket.id} purchased for ${ticket.finalPrice} UAH.")
      updatedSystem
    case Left(error) =>
      println(s"Purchase rejected: $error")
      systemWithParticipant
  }

  println("\n--- Financial report ---")
  val report = EventAnalytics.generateGlobalReport(finalSystem.events)
  println(s"Total revenue: ${report.totalRevenue} UAH")
  println(s"Tickets sold: ${report.ticketsSold}")

  val occupancy = EventAnalytics.averageOccupancyRate(finalSystem.events)
  println(f"Capacity-weighted occupancy: $occupancy%.2f%%")

  EventAnalytics.forecastRevenue(finalSystem.events, futureEventsCount = 5) match {
    case Right(forecast) =>
      println(s"Projected revenue for the next five events: $forecast UAH")
    case Left(error) =>
      println(s"Could not calculate forecast: $error")
  }
}
