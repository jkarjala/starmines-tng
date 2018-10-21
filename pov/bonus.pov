// POV Ray StarMines models - Copyright 1999-2018 Jari Karjala <jka@iki.fi>

#version 3.0
global_settings { assumed_gamma 2.2 }

#include "colors.inc"
#include "glass.inc"
#include "bonusobj.pov"

#declare sphere1 = 
// SPHERE _sphere3
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 scale  < 2, 2, 2 > 
 translate  < 0, 0, 0 > 
 }

object { sphere1 rotate <0, 360*clock, 0> }
