package events

trait Identifiable {
  val id: String
}

trait PriceCalculator {
  def calculatePrice(basePrice: Double): Double
}

trait StandardPrice extends PriceCalculator {
  def calculatePrice(basePrice: Double): Double = basePrice
}

trait StudentDiscount extends PriceCalculator {
  abstract override def calculatePrice(basePrice: Double): Double = 
    super.calculatePrice(basePrice) * 0.8 // 20% знижка
}

trait VipMarkup extends PriceCalculator {
  abstract override def calculatePrice(basePrice: Double): Double = 
    super.calculatePrice(basePrice) * 1.5 // 50% націнка
}

trait ServiceFee extends PriceCalculator {
  abstract override def calculatePrice(basePrice: Double): Double = 
    super.calculatePrice(basePrice) + 50.0 // Фіксований сервісний збір платформи
}