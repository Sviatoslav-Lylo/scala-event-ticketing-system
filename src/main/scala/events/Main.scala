package events

@main def runApp(): Unit = {
  println("=== Ініціалізація cистеми управління подіями ===")

  val initialSystem = new EventManager()

  val eventResult = Event.create(
    id = "E-001",
    title = "Scala & AI Engineering Conference 2026",
    category = EventCategory.Conference,
    maxCapacity = 100,
    basePrice = 1000.0
  )

  val systemWithEvent = eventResult match {
    case Right(event) => initialSystem.addEvent(event)
    case Left(error)  => 
      println(s"Помилка створення події: $error")
      initialSystem
  }

  val user = Participant("U-001", "Святослав", "sviat@gmail.com")
  val systemWithUser = systemWithEvent.addParticipant(user)

  val studentPricing = new StandardPrice with StudentDiscount with ServiceFee

  println("\n--- Спроба купівлі квитка ---")
  val purchaseResult = systemWithUser.buyTicket(
    eventId = "E-001",
    participantId = "U-001",
    tier = TicketTier.Student,
    priceCalculator = studentPricing
  )

  val finalSystem = purchaseResult match {
    case Right((newManager, ticket)) =>
      println(s"Квиток ${ticket.id} успішно придбано.")
      println(s"Розрахована ціна (1000 - знижка 20% + збір 50): ${ticket.finalPrice} грн.")
      newManager
    case Left(error) =>
      println(s"Відмова у купівлі: $error")
      systemWithUser
  }

  println("\n--- Фінансовий звіт ---")
  val report = EventAnalytics.generateGlobalReport(finalSystem.events)
  
  println(s"Загальний дохід системи: ${report.totalRevenue} грн")
  println(s"Загальна кількість проданих квитків: ${report.ticketsSold}")
  
  val occupancy = EventAnalytics.averageOccupancyRate(finalSystem.events)
  println(f"Середня заповнюваність залів: $occupancy%.2f%%")
  
  val forecast = EventAnalytics.forecastRevenue(finalSystem.events, futureEventsCount = 5)
  println(s"Прогноз доходу з наступних 5 подій: $forecast грн")
}