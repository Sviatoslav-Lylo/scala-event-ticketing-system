package events

sealed trait TicketTier
object TicketTier {
    case object Standard extends TicketTier
    case object VIP extends TicketTier
    case object Student extends TicketTier
}

sealed trait EventCategory
object EventCategory {
    case object Conference extends EventCategory
    case object Concert extends EventCategory
    case object Workshop extends EventCategory
}

sealed trait DomainError
case class EventNotFound(id: String) extends DomainError
case class SoldOut(eventId: String) extends DomainError
case class InvalidData(msg: String) extends DomainError

// Сутності

case class Ticket(
    id: String,
    eventId: String,
    participantId: String,
    tier: TicketTier,
    finalPrice: Double
) extends Identifiable

case class Participant(
    id: String,
    name: String,
    email: String,
    tickets: List[Ticket] = Nil
) extends Identifiable

case class Event(
    id: String,
    title: String,
    category: EventCategory,
    maxCapacity: Int,
    basePrice: Double,
    soldTickets: List[Ticket] = Nil
) extends Identifiable {
  def isAvailable: Boolean = soldTickets.length < maxCapacity
}

object Event {
  def create(
      id: String,
      title: String,
      category: EventCategory,
      maxCapacity: Int,
      basePrice: Double
  ): Either[DomainError, Event] = {
    if (maxCapacity <= 0) Left(InvalidData("Місткість має бути більшою за 0"))
    else if (basePrice < 0) Left(InvalidData("Ціна не може бути від'ємною"))
    else Right(Event(id, title, category, maxCapacity, basePrice))
  }
}
