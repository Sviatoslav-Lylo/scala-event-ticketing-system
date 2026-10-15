package events

import org.scalatest.funsuite.AnyFunSuite

class EventSystemSuite extends AnyFunSuite {
  private val baseEvent =
    Event.create(
      "E-001",
      "Test Conference",
      EventCategory.Conference,
      maxCapacity = 2,
      basePrice = BigDecimal("1000.00")
    ).toOption.get
  private val participant =
    Participant("U-001", "Demo Participant", "participant@example.com")
  private val initialManager =
    new EventManager(List(baseEvent), List(participant), Nil)

  test("A successful purchase returns a new state and keeps the price exact") {
    val result = initialManager.buyTicket(
      "E-001",
      "U-001",
      TicketTier.Standard,
      new StandardPrice {}
    )

    assert(result.isRight)
    val (updatedManager, ticket) = result.toOption.get
    assert(ticket.finalPrice == BigDecimal("1000.00"))
    assert(updatedManager.tickets == List(ticket))
    assert(updatedManager.events.head.soldTickets == List(ticket))
    assert(initialManager.tickets.isEmpty)
    assert(initialManager.events.head.soldTickets.isEmpty)
  }

  test("A full event rejects a purchase") {
    val fullEvent = baseEvent.copy(
      soldTickets = List(
        Ticket("T-1", "E-001", "U-002", TicketTier.Standard, BigDecimal("1000.00")),
        Ticket("T-2", "E-001", "U-003", TicketTier.Standard, BigDecimal("1000.00"))
      )
    )
    val manager = new EventManager(List(fullEvent), List(participant), Nil)

    val result = manager.buyTicket(
      "E-001",
      "U-001",
      TicketTier.Standard,
      new StandardPrice {}
    )

    assert(result.swap.toOption.get.isInstanceOf[SoldOut])
    assert(manager.tickets.isEmpty)
  }

  test("Trait linearization applies pricing rules in mixin order") {
    val discountThenFee = new StandardPrice with StudentDiscount with ServiceFee
    val feeThenDiscount = new StandardPrice with ServiceFee with StudentDiscount
    val basePrice = BigDecimal("1000.00")

    assert(discountThenFee.calculatePrice(basePrice) == BigDecimal("850.00"))
    assert(feeThenDiscount.calculatePrice(basePrice) == BigDecimal("840.00"))
  }

  test("Global financial reports combine revenue and ticket counts") {
    val event1 = baseEvent.copy(
      soldTickets = List(
        Ticket("T-1", "E-001", "U-1", TicketTier.Standard, BigDecimal("1000.00"))
      )
    )
    val event2 = baseEvent.copy(
      soldTickets = List(
        Ticket("T-2", "E-002", "U-2", TicketTier.VIP, BigDecimal("2000.00"))
      )
    )

    val report = EventAnalytics.generateGlobalReport(List(event1, event2))

    assert(report.totalRevenue == BigDecimal("3000.00"))
    assert(report.ticketsSold == 2)
  }

  test("Event creation validates required fields and rounds to cents") {
    assert(
      Event.create("", "Conference", EventCategory.Conference, 10, BigDecimal("10.00")).isLeft
    )
    assert(
      Event.create("E-002", " ", EventCategory.Conference, 10, BigDecimal("10.00")).isLeft
    )
    assert(
      Event.create("E-003", "Conference", EventCategory.Conference, 0, BigDecimal("10.00")).isLeft
    )
    assert(
      Event.create("E-004", "Conference", EventCategory.Conference, 10, BigDecimal("-1.00")).isLeft
    )

    val rounded = Event.create(
      "E-005",
      "Conference",
      EventCategory.Conference,
      10,
      BigDecimal("10.005")
    )

    assert(rounded.toOption.get.basePrice == BigDecimal("10.01"))
  }

  test("A negative calculated ticket price is rejected without changing state") {
    val invalidPrice = new StandardPrice {
      override def calculatePrice(basePrice: BigDecimal): BigDecimal =
        BigDecimal("-1.00")
    }

    val result = initialManager.buyTicket(
      "E-001",
      "U-001",
      TicketTier.Standard,
      invalidPrice
    )

    assert(result.left.toOption.get.isInstanceOf[InvalidData])
    assert(initialManager.tickets.isEmpty)
  }

  test("Revenue forecasts validate the horizon and round to cents") {
    val event = baseEvent.copy(
      soldTickets = List(
        Ticket("T-1", "E-001", "U-1", TicketTier.Standard, BigDecimal("10.00"))
      )
    )

    assert(EventAnalytics.forecastRevenue(List(event), 3) == Right(BigDecimal("30.00")))
    assert(EventAnalytics.forecastRevenue(Nil, 3) == Right(BigDecimal("0.00")))
    assert(EventAnalytics.forecastRevenue(List(event), -1).isLeft)
  }
}
