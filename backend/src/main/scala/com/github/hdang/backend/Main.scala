package com.github.hdang.backend

import cats.effect.IOApp


object Main extends IOApp.Simple {

  val run = BackendServer.run
}
