package com.jpkware.smng

import scala.scalajs.js

// This state is the way scala.js models a Javascript literal object -> https://www.w3schools.com/js/js_objects.asp
object State {
  def apply(init: () => Unit, preload: () => Unit,  create: () => Unit, update: () => Unit, render: () => Unit): State = {
    js.Dynamic.literal(init = init, preload = preload, create = create, update = update, render = render).asInstanceOf[State]
  }
  def apply(preload: () => Unit,  create: () => Unit, update: () => Unit, render: () => Unit): State = {
    js.Dynamic.literal(init = () => {}, preload = preload, create = create, update = update, render = render).asInstanceOf[State]
  }

  def apply(preload: () => Unit,  create: () => Unit, update: () => Unit): State = {
    js.Dynamic.literal(init = () => {}, preload = preload, create = create, update = update, render = () => {}).asInstanceOf[State]
  }

}

@js.native
trait State extends js.Object {
  def init: Unit = js.native
  def preload: Unit = js.native
  def create: Unit = js.native
  def update: Unit = js.native
  def render: Unit = js.native
}