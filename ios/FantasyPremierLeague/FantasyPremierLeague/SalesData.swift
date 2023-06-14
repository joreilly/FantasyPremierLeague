import Foundation
import GameplayKit

private let gaussianRandoms = GKGaussianDistribution(lowestValue: 0, highestValue: 20)

private func date(year: Int, month: Int, day: Int = 1) -> Date {
    Calendar.current.date(from: DateComponents(year: year, month: month, day: day)) ?? Date()
}

struct SalesData {
    static let last365Days: [(day: Date, sales: Int)] = stride(from: 0, to: 200, by: 1).compactMap {
        let startDay: Date = date(year: 2022, month: 11, day: 17)  // 200 days before WWDC
        let day: Date = Calendar.current.date(byAdding: .day, value: $0, to: startDay)!
        let dayNumber = Double($0)

        var sales = randomSalesForDay(dayNumber)
        let dayOfWeek = Calendar.current.component(.weekday, from: day)
        if dayOfWeek == 6 {
            sales += gaussianRandoms.nextInt() * 3
        } else if dayOfWeek == 7 {
            sales += gaussianRandoms.nextInt()
        } else {
            sales = Int(Double(sales) * Double.random(in: 4...5) / Double.random(in: 5...6))
        }
        return (day: day, sales: sales)
    }
}

private extension SalesData {
    private static func randomSalesForDay(_ dayNumber: Double) -> Int {
        // Add noise to the generated data.
        let yearlySeasonality = 100.0 * (0.5 - 0.5 * cos(2.0 * .pi * (dayNumber / 364.0)))
        let monthlySeasonality = 10.0 * (0.5 - 0.5 * cos(2.0 * .pi * (dayNumber / 30.0)))
        let weeklySeasonality = 30.0 * (1 - cos(2.0 * .pi * ((dayNumber + 2.0) / 7.0)))
        return Int(yearlySeasonality + monthlySeasonality + weeklySeasonality + Double(gaussianRandoms.nextInt()))
    }
}
