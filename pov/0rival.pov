// Persistence Of Vision raytracer version 3.0 sample file.

#version 3.0
global_settings { assumed_gamma 2.2 }

#include "colors.inc"
#include "shipobj2.pov"




#declare _sphere1 = 
// SPHERE _sphere0
sphere {  < 0, 0, 0 > 1
 texture { Chrome_Texture }
 scale  < 1.75, 0.75, 0.65 > 
 }





#declare _cone1 = 
// CONE _cone0
cone { <0, -1, 0>, 1
  < 0, 1, 0>, 0
 texture { pigment { color SkyBlue } }
 scale  < 0.5, 2.5, 1.75 > 
 rotate  < 90, 90, 0 > 
 translate  < 0.75, 0, 0 > 
 }




#declare _cone1_0 = 
// CONE _cone0_0
cone { <0, -1, 0>, 1
  < 0, 1, 0>, 0
 texture { pigment { color Blue } }
 scale  < 1, 2.75, 1 > 
 rotate  < 90, 90, 0 > 
 translate  < 0.5, 0, 0 > 
 }



#declare ship =
// DEFAULT GROUP
union {
  object { _sphere1 }
  object { _cone1 }
  object { _cone1_0 }
}

object { ship rotate <0, 0, 360*clock> }
