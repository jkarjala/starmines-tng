// Persistence Of Vision raytracer version 3.0 sample file.

#version 3.0
global_settings { assumed_gamma 2.2 }

#include "colors.inc"
#include "shipobj2.pov"

#declare ship =
// DEFAULT GROUP
union {
  object { _sphere0 }
  object { _cone0 }
  object { _cone0_0 }
}

object { ship rotate <0, 0, 360*clock> }
