package sim;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class SimpleBarChart extends JPanel {
	private Integer[] value;
	private Float[] languages;
	private String title;

	public SimpleBarChart(Integer[] value2, Float[] languages2, String t) {
		languages = languages2;
		value = value2;
		title = t;
	}
	public void paintComponent(Graphics graphics) {

		super.paintComponent(graphics);
		if (value == null || value.length == 0)
			return;
		double minValue = 0;
		double maxValue = 0;
		for (int i = 0; i < value.length; i++) {
			if (minValue > value[i])
				minValue = value[i];
			if (maxValue < value[i])
				maxValue = value[i];
		}
		Dimension dim = getSize();
		int clientWidth = dim.width;
		int clientHeight = dim.height;
		int barWidth = clientWidth / value.length;
		Font titleFont = new Font("Book Antiqua", Font.BOLD, 15);
		FontMetrics titleFontMetrics = graphics.getFontMetrics(titleFont);
		Font labelFont = new Font("Book Antiqua", Font.PLAIN, 10);
		FontMetrics labelFontMetrics = graphics.getFontMetrics(labelFont);
		int titleWidth = titleFontMetrics.stringWidth(title);
		int q = titleFontMetrics.getAscent();
		int p = (clientWidth - titleWidth) / 2;
		graphics.setFont(titleFont);
		graphics.drawString(title, p, q);
		int top = titleFontMetrics.getHeight();
		int bottom = labelFontMetrics.getHeight();
		if (maxValue == minValue)
			return;
		double scale = (clientHeight - top - bottom) / (maxValue - minValue);
		q = clientHeight - labelFontMetrics.getDescent();
		graphics.setFont(labelFont);
		for (int j = 0; j < value.length; j++) {
			int valueP = j * barWidth + 1;
			int valueQ = top;
			int height = (int) (value[j] * scale);
			if (value[j] >= 0)
				valueQ += (int) ((maxValue - value[j]) * scale);
			else {
				valueQ += (int) (maxValue * scale);
				height = -height;
			}
			graphics.setColor(Color.blue);
			graphics.fillRect(valueP, valueQ, barWidth - 2, height);
			graphics.setColor(Color.black);
			graphics.drawRect(valueP, valueQ, barWidth - 2, height);

			Float labelWidth = languages[j];
			p = (int) (j * barWidth + (barWidth - labelWidth) / 2);
			/*
  Rectangle2D.Double rect = new Rectangle2D.Double((double)languages[j],(double) p,(double) q, (double)languages[j]);
  Graphics2D g2d = (Graphics2D)graphics.create();
  g2d.draw(rect);*/

		}
	}
}
