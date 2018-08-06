package com.codenjoy.dojo.lunolet.utility;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 - 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.lunolet.model.Level;
import com.codenjoy.dojo.lunolet.model.LevelManager;
import com.codenjoy.dojo.lunolet.model.LineIntersection;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class PrintLevels {

    public static void main(String[] args) {
        try {
            PrintWriter writer = new PrintWriter("src\\main\\webapp\\resources\\help\\lunolet-levels.html", "UTF-8");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
            writer.println("    <link href=\"files/style.css\" media=\"all\" type=\"text/css\" rel=\"stylesheet\">");
            writer.println("</head>");
            writer.println("<body style=\"background-color: white;\" class=\"single single-post postid-170 single-format-standard logged-in admin-bar singular one-column content customize-support\"");
            writer.println("<div id=\"page\" class=\"hfeed\">");
            writer.println("<div id=\"main\">");
            writer.println("<div id=\"primary\">");
            writer.println("<div id=\"content\" role=\"main\">");
            writer.println();
            writer.println("<h1 class=\"entry-title\">Lunolet &mdash; Levels</h1>");
            writer.println();
            writer.println("<div class=\"entry-content\">");
            writer.println("<div class=\"page-restrict-output\">");
            writer.println();

            LevelManager manager = new LevelManager();

            while (true) {
                int levelNum = manager.getLevelNumber();
                writer.println(String.format("<h2>Level %d</h2>", levelNum));
                Level level = manager.getLevel();

                List<Point2D.Double> relief = level.Relief;
                double targetX = level.TargetX;
                double targetY = 0.0;
                for (int i = 0; i < relief.size() - 1; i++) {
                    Point2D.Double pt1 = relief.get(i);
                    Point2D.Double pt2 = relief.get(i + 1);
                    if (pt1.x < targetX && pt2.x >= targetX) {

                        if (Math.abs(pt2.x - targetX) < 1e-5)
                            targetY = pt2.y;
                        else
                            targetY = ((targetX - pt1.x) / (pt2.x - pt1.x) * (pt2.y - pt1.y) + pt1.y);

                        break;
                    }
                }

                // find out the box margins
                double levelLeft = -30.0;
                double levelRight = 30.0;
                double levelTop = 20.0;
                double levelBottom = -20.0;
                if (targetX - 30.0 < levelLeft)
                    levelLeft = targetX - 30.0;
                if (targetX + 30.0 > levelRight)
                    levelRight = targetX + 30.0;
                if (targetY - 20.0 < levelBottom)
                    levelBottom = targetY - 20.0;
                if (targetY + 20.0 > levelTop)
                    levelTop = targetY + 20.0;

                for (int i = 0; i < relief.size(); i++) {
                    Point2D.Double pt1 = relief.get(i);
                    if (pt1.x < levelLeft || pt1.x > levelRight)
                        continue;
                    if (pt1.y < levelBottom)
                        levelBottom = pt1.y;
                    if (pt1.y > levelTop)
                        levelTop = pt1.y;
                    if (pt1.x < levelLeft)
                        levelLeft = pt1.x;
                    if (pt1.x > levelRight)
                        levelRight = pt1.x;
                }

                double levelWidth = levelRight - levelLeft;
                double levelHeight = levelTop - levelBottom;

                double scale = 1.0;
                for (int i = 1; i < 200; i++) {
                    scale = ((double) i) / 2.0;
                    if (levelWidth / scale < 800.0 && levelHeight / scale < 800.0)
                        break;
                }

                int boxWidth = (int) (levelWidth / scale);
                int boxHeight = (int) (levelHeight / scale);

                writer.print("<svg xmlns=\"http://www.w3.org/2000/svg\" ");
                writer.print(String.format("width=\"%d\" height=\"%d\" ", boxWidth, boxHeight));
                writer.print(String.format("viewBox=\"%f %f %f %f\" ", levelLeft, levelBottom, levelWidth, levelHeight));
                writer.println("transform=\"scale(1 -1)\">");

                writer.print(String.format(
                        "<polyline stroke=\"black\" stroke-width=\"%d\" fill=\"none\" points=\"", Math.round(scale)));
                for (int i = 0; i < relief.size(); i++) {
                    Point2D.Double pt = relief.get(i);
                    writer.print(String.format("%f,%f ", pt.x, pt.y));
                }
                writer.println("\" />");

                // starting point
                writer.println(String.format(
                        "<line stroke=\"blue\" stroke-width=\"%d\" x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" />",
                        Math.round(scale), 0.0, -scale * 5, 0.0, scale * 5));
                writer.println(String.format(
                        "<line stroke=\"blue\" stroke-width=\"%d\" x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" />",
                        Math.round(scale), -scale * 5, 0.0, scale * 5, 0.0));
                // target point
                writer.println(String.format(
                        "<line stroke=\"red\" stroke-width=\"%d\" x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" />",
                        Math.round(scale), targetX, targetY - scale * 5, targetX, targetY + scale * 5));
                writer.println(String.format(
                        "<line stroke=\"red\" stroke-width=\"%d\" x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" />",
                        Math.round(scale), targetX - scale * 5, targetY, targetX + scale * 5, targetY));

                writer.println("</svg>");

                writer.println();

                manager.levelUp();
                levelNum = manager.getLevelNumber();
                if (levelNum == 0 || levelNum >= 150)
                    break;
            }

            writer.println("</div>");
            writer.println("</div>");
            writer.println();
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</body>");
            writer.println("</html>");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
