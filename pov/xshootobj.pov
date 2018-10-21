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
















// CYLINDER _cylinder0
cylinder { < 0, -1, 0>, < 0, 1, 0>, 1
 texture { Blue_Sky }
 scale  < 0.5, 1, 0.5 > 
 }


// CONE _cone0
cone { <0, -1, 0>, 1
  < 0, 1, 0>, 0
 texture { Blue_Sky2 }
 scale  < 2, 0.75, 2 > 
 translate  < 0, 1.25, 0 > 
 }


// CONE _cone0_0
cone { <0, -1, 0>, 1
  < 0, 1, 0>, 0
 texture { Blue_Sky2 }
 scale  < 2, 0.75, 2 > 
 rotate  < 0, 0, 180 > 
 translate  < 0, -1.25, 0 > 
 }


// CYLINDER _cylinder1
cylinder { < 0, -1, 0>, < 0, 1, 0>, 1
 texture { pigment { color Quartz } }
 scale  < 0.2, 1.25, 0.2 > 
 rotate  < 0, 0, 90 > 
 }

