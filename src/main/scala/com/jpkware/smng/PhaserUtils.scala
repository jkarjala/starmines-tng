package com.jpkware.smng

import scala.scalajs.js

// This state is the way scala.js models a Javascript literal object -> https://www.w3schools.com/js/js_objects.asp
object State {
  def apply(preload: () => Unit,  create: () => Unit, update: () => Unit, render: () => Unit): State = {
    js.Dynamic.literal(preload = preload, create = create, update = update, render = render).asInstanceOf[State]
  }
}

@js.native
trait State extends js.Object {
  def preload: Unit = js.native
  def create: Unit = js.native
  def update: Unit = js.native
  def render: Unit = js.native
}