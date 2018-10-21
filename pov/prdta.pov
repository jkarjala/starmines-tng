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
 location   < 0, 0, 4 > 
 look_at    < 0, 0, 0 > 
}
// LIGHT _light3
light_source { < 0, 0.5, 21.625 > 
color White
}

// LIGHT _light4
light_source { < -10, 10, 10 > 
color White
}

// SPHERE _sphere0_3
sphere {  < 0, 0, 0 > 1
 texture { pigment { color Quartz } }
 scale  < 1.5, 1, 1 > 
 rotate  < 0, 0, 360*clock > 
 }

