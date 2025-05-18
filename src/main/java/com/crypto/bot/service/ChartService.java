package com.crypto.bot.service;

import com.crypto.bot.entity.History;
import com.crypto.bot.entity.Symbol;
import com.crypto.bot.repository.BotHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final BotHistoryRepository historyRepository;

    public byte[] generateStyledChart(Symbol symbol, Timestamp from, Timestamp to) throws IOException {
        List<History> data = historyRepository.findBySymbolAndTimestampBetween(symbol, from, to);

        TimeSeries series = new TimeSeries("Price");
        for (History point : data) {
            series.addOrUpdate(new Minute(Date.from(point.getTimestamp().toInstant())), point.getValue() / 100.0);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                symbol.getSymbol() + " Price Chart",
                "Time",
                "Price (USD)",
                dataset
        );

        XYPlot plot = chart.getXYPlot();

        // Dark theme settings
        plot.setBackgroundPaint(new Color(30, 30, 30));
        plot.setDomainGridlinePaint(new Color(80, 80, 80));
        plot.setRangeGridlinePaint(new Color(80, 80, 80));
        chart.setBackgroundPaint(new Color(20, 20, 20));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(255, 100, 100, 180));
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        plot.setRenderer(renderer);

        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        domainAxis.setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);

        chart.getTitle().setPaint(Color.WHITE);

        var legend = chart.getLegend();
        legend.setItemPaint(Color.WHITE); // Белый текст
        legend.setBackgroundPaint(null);
        legend.setFrame(BlockBorder.NONE);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(out, chart, 800, 400);

        return out.toByteArray();
    }
}