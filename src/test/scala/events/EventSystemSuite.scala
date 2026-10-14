package events

import org.scalatest.funsuite.AnyFunSuite

class EventSystemSuite extends AnyFunSuite {

  // Дані для тестування
  val baseEvent = Event.create("E-001", "Test Conference", EventCategory.Conference, maxCapacity = 2, basePrice = 1000.0).toOption.get
  val user = Participant("U-001", "Святослав", "svyat@example.com")
  val initialManager = new EventManager(List(baseEvent), List(user), Nil)

  test("Успішна купівля квитка та перевірка іммутабельності") {
    val standardPrice = new StandardPrice {}
    val result = initialManager.buyTicket("E-001", "U-001", TicketTier.Standard, standardPrice)

    assert(result.isRight)
    val (newManager, ticket) = result.toOption.get
    
    // Перевірка, що новий стан змінився
    assert(newManager.tickets.length == 1)
    assert(newManager.events.head.soldTickets.length == 1)
    
    // Перевірка іммутабельності
    assert(initialManager.tickets.isEmpty)
    assert(initialManager.events.head.soldTickets.isEmpty)
  }

  test("Відмова у купівлі через брак місць (перевірка Either.Left)") {
    val fullEvent = baseEvent.copy(soldTickets = List(
      Ticket("T-1", "E-001", "U-002", TicketTier.Standard, 1000.0),
      Ticket("T-2", "E-001", "U-003", TicketTier.Standard, 1000.0)
    ))
    val managerWithFullEvent = new EventManager(List(fullEvent), List(user), Nil)

    val result = managerWithFullEvent.buyTicket("E-001", "U-001", TicketTier.Standard, new StandardPrice {})

    assert(result.isLeft)
    assert(result.swap.toOption.get.isInstanceOf[SoldOut]) // Помилка має бути саме типу SoldOut
  }

  test("Правильність розрахунку ціни через Trait Linearization") {
    val pricing = new StandardPrice with StudentDiscount with ServiceFee
    assert(pricing.calculatePrice(1000.0) == 850.0)
    
    val reversedPricing = new StandardPrice with ServiceFee with StudentDiscount
    assert(reversedPricing.calculatePrice(1000.0) == 840.0)
  }
  
  test("Перевірка Моноїда та глобального звіту") {
    val event1 = baseEvent.copy(soldTickets = List(Ticket("T-1", "E-001", "U-1", TicketTier.Standard, 1000.0)))
    val event2 = baseEvent.copy(soldTickets = List(Ticket("T-2", "E-002", "U-2", TicketTier.VIP, 2000.0)))
    
    val report = EventAnalytics.generateGlobalReport(List(event1, event2))
    
    assert(report.totalRevenue == 3000.0)
    assert(report.ticketsSold == 2)
  }
}
