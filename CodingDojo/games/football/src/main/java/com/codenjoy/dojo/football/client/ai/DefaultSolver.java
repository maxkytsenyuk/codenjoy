package com.codenjoy.dojo.football.client.ai;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.football.client.Board;
import com.codenjoy.dojo.football.model.Actions;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.services.algs.DeikstraFindWay;

import java.util.ArrayList;
import java.util.List;

public class DefaultSolver implements Solver<Board> {

    private final Dice dice;
    private DeikstraFindWay way;

    public DefaultSolver(Dice dice) {
        this.way = new DeikstraFindWay();
        this.dice = dice;
    }

    public DeikstraFindWay.Possible possible(final Board board) {
        return new DeikstraFindWay.Possible() {
            @Override
            public boolean possible(Point from, Direction where) {
                int x = from.getX();
                int y = from.getY();
                if (board.isBarrierAt(x, y)) return false;

                Point newPt = where.change(from);
                int nx = newPt.getX();
                int ny = newPt.getY();

                if (board.isOutOfField(nx, ny)) return false;

                if (board.isBarrierAt(nx, ny)) return false;
                //if (board.isBombAt(nx, ny)) return false;

                return true;
            }

            @Override
            public boolean possible(Point atWay) {
                return true;
            }
        };
    }

    @Override
    public String get(final Board board) {
        if (board.isGameOver()) return "";

        String resultString = "";

        Point from = board.getMe();

        List<Direction> result = getDirections(board, from);
        if (result.isEmpty()) {
            resultString = resultString + ((resultString.length() == 0) ? "" : ", ") +
                    "act(" + dice.next(4) + ")";
        } else {
            Direction direction = result.get(0);
            resultString = resultString + ((resultString.length() == 0) ? "" : ", ") +
                    direction.toString() + "";

            if (direction == Direction.UP) {
                //resultString =  resultString + ((resultString.length() == 0) ? "" : ", ") +
                //            "act(" + Actions.UP.getValue() + ", " + from.getX() + ", " + from.getY();
                resultString = resultString + ", act(" + Actions.HIT_UP.getValue() + ", 3)";
            } else if (direction == Direction.RIGHT) {
                //resultString =  resultString + ((resultString.length() == 0) ? "" : ", ") +
                //        "act(" + Actions.RIGHT.getValue() + ", " + from.getX() + ", " + from.getY();
                resultString = resultString + ", act(" + Actions.HIT_RIGHT.getValue() + ", 3)";
            } else if (direction == Direction.DOWN) {
                //resultString =  resultString + ((resultString.length() == 0) ? "" : ", ") +
                //        "act(" + Actions.DOWN.getValue() + ", " + from.getX() + ", " + from.getY();
                resultString = resultString + ", act(" + Actions.HIT_DOWN.getValue() + ", 3)";
            } else if (direction == Direction.LEFT) {
                //resultString =  resultString + ((resultString.length() == 0) ? "" : ", ") +
                //        "act(" + Actions.LEFT.getValue() + ", " + from.getX() + ", " + from.getY();
                resultString = resultString + ", act(" + Actions.HIT_LEFT.getValue() + ", 3)";
            } else {
                //resultString =  resultString + ((resultString.length() == 0) ? "" : ", ") +
                //        direction.toString();
                resultString = resultString + ", act(" + Actions.STOP_BALL.getValue() + ")";
            }
        }
        return resultString;
    }

    public List<Direction> getDirections(Board board, Point from) {
        int size = board.size();
        List<Point> to = new ArrayList<>();

        Point ball = board.getBall();
        if ((ball.itsMe(from)) || (board.isBallOnMyTeam())) {
            to.add(board.getEnemyGoal());
        } else {
            to.add(ball);
        }

        DeikstraFindWay.Possible map = possible(board);
        return way.getShortestWay(size, from, to, map);
    }

    public static void main(String[] args) {
        start(WebSocketRunner.DEFAULT_USER, new RandomDice());
    }

    public static void start(String name, Dice dice) {
        WebSocketRunner.runAI(name,
                new DefaultSolver(dice),
                new Board());
    }
}
