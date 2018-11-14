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
package main

import (
	"./network"
	"flag"
	"log"
	"os"
	"os/signal"
	"strings"
)

var urlStr = flag.String("url", "ws://127.0.0.1:8080/codenjoy-contest/ws?user=apofig@gmail.com&code=20010765231070354251", "server url")

func main() {
	flag.Parse()
	log.SetFlags(0)

	urls := strings.Split(*urlStr, " ")

	for id, url := range urls {
		network.NewClient(id, url)
	}

	interrupt := make(chan os.Signal, 1)
	signal.Notify(interrupt, os.Interrupt)
	<-interrupt
	log.Println("exiting")
	// To cleanly close a connection, a client should send a close
	// frame and wait for the server to close the connection.
}