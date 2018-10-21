// Scene Created by the Breeze Designer 2.0
// Written by Neville Richards

#include "colors.inc"
#include "textures.inc"
#include "shapes.inc"

// CAMERA POSITION
camera {
 right      < -1, 0, 0 > 
 up         < 0, 1, 0 > 
 direction  < 0, 0, 1 > 
 location   < 0, 0, 2.75 > 
 look_at    < 0, 0, 0 > 
}
// LIGHT _light3
light_source { < 0, 0, 22 > 
color White
}

// LIGHT _light4
light_source { < -10, 10, 10 > 
color White
}






#declare _torus1 = 
// TORUS _torus1
torus { 1, 0.25 
 texture { pigment { color LightSteelBlue } }
 }


#declare _torus1_0 = 
// TORUS _torus1_0
torus { 1, 0.25 
 texture { pigment { color LightSteelBlue } }
 rotate  < 0, 0, 90 > 
 }


#declare _torus1_0_1 = 
// TORUS _torus1_0_1
torus { 1, 0.25 
 texture { pigment { color LightSteelBlue } }
 rotate  < 0, 0, 45 > 
 }


#declare _torus1_0_1_2 = 
// TORUS _torus1_0_1_2
torus { 1, 0.25 
 texture { pigment { color LightSteelBlue } }
 rotate  < 0, 0, -45 > 
 }


// DEFAULT GROUP
union {
  object { _torus1 }
  object { _torus1_0 }
  object { _torus1_0_1 }
  object { _torus1_0_1_2 }
}
