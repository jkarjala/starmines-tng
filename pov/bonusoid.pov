// POV Ray StarMines models - Copyright 1999-2019 Jari Karjala - https://www.jpkware.com/

#version 3.0
global_settings { assumed_gamma 2.2 }

#include "colors.inc"
#include "glass.inc"
#include "bonusobj.pov"

#declare obj = 
union {
  object { _cone0 }
  object { _cone1 }
  object { _sphere3       translate  < 0, -0.5*sin(3*pi*clock), 0 > }
  object { _sphere3_7      translate  < 0, -0.5*cos(3*pi*clock), 0 > }
  object { _sphere3_7_8  translate  < 0, 0.5*cos(3*pi*clock), 0 > }
  object { _sphere3_7_8_9  translate  < 0, 0.5*sin(3*pi*clock), 0 >  }
}

object { obj rotate <0, 180*clock, 0> }
//object { obj  rotate <0, 45, 0> }
