import Charts
import SwiftUI

struct DailySalesChart: View {
    let isScrollable: Bool
    @Binding var scrollPosition: TimeInterval

    private let data = SalesData.last365Days
    @State private var rawSelectedDate: Date?
    private var selectedDataPoint: (day: Date, sales: Int)? {
        guard let rawSelectedDate else {
            return nil
        }
        var closestDataPoint: (day: Date, sales: Int)?
        var closestDistance: TimeInterval = .infinity
        for candidate in data {
            let candidateDistance = abs(candidate.day.timeIntervalSince(rawSelectedDate))
            if candidateDistance < closestDistance {
                closestDataPoint = candidate
                closestDistance = candidateDistance
            }
        }
        return closestDataPoint
    }

    var body: some View {
        Chart {
            ForEach(data, id: \.day) {
                BarMark(
                    x: .value("Day", $0.day, unit: .day),
                    y: .value("Sales", $0.sales)
                )
            }
            .foregroundStyle(.blue)
            if let selectedDataPoint {
                RuleMark(x: .value("Selected", selectedDataPoint.day, unit: .day))
                    .foregroundStyle(Color(.systemFill))
                    .offset(yStart: -10)
                    .zIndex(-1)
                    .annotation(
                        position: .top,
                        spacing: 0,
                        overflowResolution: .init(
                            x: .fit(to: .chart),
                            y: .disabled
                        )
                    ) { context in
                        let markWidth = context.targetSize.width
                        VStackLayout(alignment: .leading) {
                            Text(selectedDataPoint.day.formatted(.dateTime.month().day()))
                                .font(.callout.monospacedDigit())
                            Text("\(selectedDataPoint.sales) sales")
                                .font(.body.bold().monospacedDigit())
                        }
                        .frame(minWidth: markWidth > 0 ? markWidth : 0, alignment: .leading)
                        .fixedSize()
                        .padding(6)
                        .background {
                            RoundedRectangle(cornerRadius: 4)
                                .foregroundStyle(Color.gray.opacity(0.12))
                        }
                    }
            }
        }
        .chartScrollableAxes(isScrollable ? .horizontal : [])
        .chartXVisibleDomain(length: 3600 * 24 * 30)
        .chartScrollTargetBehavior(
            .valueAligned(
                matching: .init(hour: 0),
                majorAlignment: .matching(.init(day: 1))))
        .chartScrollPosition(x: $scrollPosition)
        .chartXAxis {
            AxisMarks(values: .stride(by: .day, count: 7)) {
                AxisTick()
                AxisGridLine()
                AxisValueLabel(format: .dateTime.month().day())
            }
        }
        .chartXSelection(value: $rawSelectedDate)
    }
}
