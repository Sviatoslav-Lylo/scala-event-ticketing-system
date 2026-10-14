package events

class EventManager(
    val events: List[Event],
    val participants: List[Participant],
    val tickets: List[Ticket]
) {

  def this() = this(Nil, Nil, Nil)
  def this(initialEvents: List[Event]) = this(initialEvents, Nil, Nil)

  def addEvent(event: Event): EventManager =
    new EventManager(events :+ event, participants, tickets)

  def addParticipant(participant: Participant): EventManager =
    new EventManager(events, participants :+ participant, tickets)

  def findEvent(id: String): Option[Event] =
    events.find(_.id == id)

  def getAvailableEvents: List[Event] =
    events.filter(_.isAvailable)

  // Головна бізнес-логіка

  def buyTicket(
      eventId: String,
      participantId: String,
      tier: TicketTier,
      priceCalculator: PriceCalculator
  ): Either[DomainError, (EventManager, Ticket)] = {

    val eventOpt = findEvent(eventId)
    val participantOpt = participants.find(_.id == participantId)

    (eventOpt, participantOpt) match {
      case (None, None) => Left(InvalidData("Помилка: не знайдено ані події, ані учасника."))
      case (None, _) => Left(EventNotFound(eventId))
      case (_, None) => Left(InvalidData(s"Учасника $participantId не знайдено"))
      case (Some(event), Some(participant)) =>
        if (!event.isAvailable) {
          Left(SoldOut(eventId))
        } else {
            val finalPrice = priceCalculator.calculatePrice(event.basePrice)

            val ticketId = s"T-${tickets.length + 1}"

            val ticket = Ticket(ticketId, eventId, participantId, tier, finalPrice)
    
            val updatedEvents = events.map {
              case e if e.id == eventId => e.copy(soldTickets = e.soldTickets :+ ticket)
              case e => e
            }
    
            val updatedParticipants = participants.map {
              case p if p.id == participantId => p.copy(tickets = p.tickets :+ ticket)
              case p => p
            }
    
            val newManager = new EventManager(
              updatedEvents,
              updatedParticipants,
              tickets :+ ticket
            )
    
            Right((newManager, ticket))
        }
    }
  }

  def getTicketsByCategory(category: EventCategory): List[Ticket] =
    events
      .filter(_.category == category)
      .flatMap(_.soldTickets)
}
