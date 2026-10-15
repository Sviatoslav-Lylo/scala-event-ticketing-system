package events

trait Identifiable {
  val id: String
}

trait PriceCalculator {
  def calculatePrice(basePrice: BigDecimal): BigDecimal
}

trait StandardPrice extends PriceCalculator {
  def calculatePrice(basePrice: BigDecimal): BigDecimal = basePrice
}

trait StudentDiscount extends PriceCalculator {
  abstract override def calculatePrice(basePrice: BigDecimal): BigDecimal =
    super.calculatePrice(basePrice) * BigDecimal("0.8")
}

trait VipMarkup extends PriceCalculator {
  abstract override def calculatePrice(basePrice: BigDecimal): BigDecimal =
    super.calculatePrice(basePrice) * BigDecimal("1.5")
}

trait ServiceFee extends PriceCalculator {
  abstract override def calculatePrice(basePrice: BigDecimal): BigDecimal =
    super.calculatePrice(basePrice) + BigDecimal("50.00")
}