package com.codenjoy.dojo.bomberman.client.simple;

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

import com.codenjoy.dojo.bomberman.client.Board;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.RandomDice;

import java.util.HashMap;

public class YourSolverLite implements Solver<Board> {

    private Processor processor;
    private Dice dice;
    private Board board;

    public YourSolverLite(Dice dice) {
        this.dice = dice;
        this.processor = new Processor();
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.isMyBombermanDead()) return "";

        setup();

        return processor.process(board).toString();
    }

    private void setup() {
        processor.addIf(Direction.RIGHT,
                "???" +
                "♥☺?" +
                "???");

        processor.addIf(Direction.LEFT,
                "???" +
                "♥☺?" +
                "???");

        processor.addIf(Direction.DOWN,
                "?♥?" +
                "?☺?" +
                "???");

        processor.addIf(Direction.UP,
                "???" +
                "?☺?" +
                "?♥?");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Bad format, please run program with 1 argument " +
                    "like 'http://codenjoy.com:80/codenjoy-contest/board/player/playerId?code=1234567890123456789'");
        }
        WebSocketRunner.runClient(
                args[0],
                new YourSolverLite(new RandomDice()),
                new Board());
    }

}