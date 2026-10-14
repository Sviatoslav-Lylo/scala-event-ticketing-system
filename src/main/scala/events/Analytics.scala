package events

trait Monoid[A] {
  def empty: A
  def combine(x: A, y: A): A
}

case class FinancialReport(totalRevenue: Double, ticketsSold: Int)

object FinancialReport {
  implicit val reportMonoid: Monoid[FinancialReport] = new Monoid[FinancialReport] {

    def empty: FinancialReport = FinancialReport(0.0, 0)

    def combine(x: FinancialReport, y: FinancialReport): FinancialReport =
      FinancialReport(
        x.totalRevenue + y.totalRevenue,
        x.ticketsSold + y.ticketsSold
      )
  }
}


object EventAnalytics {

  def combineAll[A](xs: List[A])(implicit m: Monoid[A]): A =
    xs.foldLeft(m.empty)(m.combine)

  def generateGlobalReport(events: List[Event]): FinancialReport = {
    val singleReports = events.map { event =>
      val revenue = event.soldTickets.map(_.finalPrice).sum
      FinancialReport(revenue, event.soldTickets.size)
    }
    combineAll(singleReports)
  }

  def averageOccupancyRate(events: List[Event]): Double = {
    if (events.isEmpty) 0.0
    else {
      val (totalSold, totalCapacity) = events.foldLeft((0, 0)) { case ((soldAcc, capAcc), event) =>
        (soldAcc + event.soldTickets.length, capAcc + event.maxCapacity)
      }
      if (totalCapacity == 0) 0.0 else (totalSold.toDouble / totalCapacity) * 100.0
    }
  }

  def forecastRevenue(events: List[Event], futureEventsCount: Int): Double = {
    if (events.isEmpty) 0.0
    else {
      val globalReport = generateGlobalReport(events)
      val averageRevenuePerEvent = globalReport.totalRevenue / events.length

      averageRevenuePerEvent * futureEventsCount
    }
  }
}