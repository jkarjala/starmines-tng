// POV Ray StarMines models - Copyright 1999-2018 Jari Karjala <jka@iki.fi>

#version 3.0
global_settings { assumed_gamma 2.2 }

#include "colors.inc"
#include "shipobj.pov"

#declare ship = 
// DEFAULT GROUP
union {
  object { _sphere0 }
  object { _sphere1 }
  object { _cylinder0 }
  object { _cylinder0_0 }
  object { _sphere2 }
  object { _cylinder0_0_1 }
  object { _cylinder0_0_1_2 }
}

object { ship rotate <0, 0, 360*clock> }
