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
 location   < 0, 0, 5 > 
 look_at    < 0, 0, 0 > 
}
// LIGHT _light3
light_source { < 0, 0.5, 21.625 > 
color NeonPink
}

// LIGHT _light4
light_source { < -10, 10, 10 > 
color White
}















// BOX _box0
box { < -0.5, -0.5, -0.5>,
 < 0.5, 0.5, 0.5>
 texture { pigment { color NeonPink } }
 scale  < 1.5, 1.5, 1.5 > 
 rotate  < 45, 0, 0 > 
 translate  < 1, 1, 0 > 
 }


// BOX _box0_0
box { < -0.5, -0.5, -0.5>,
 < 0.5, 0.5, 0.5>
 texture { pigment { color NeonPink } }
 scale  < 1.5, 1.5, 1.5 > 
 rotate  < 0, 45, 0 > 
 translate  < 1, -1, 0 > 
 }


// BOX _box0_1
box { < -0.5, -0.5, -0.5>,
 < 0.5, 0.5, 0.5>
 texture { pigment { color NeonPink } }
 scale  < 1.5, 1.5, 1.5 > 
 rotate  < 45, 0, 0 > 
 translate  < -1, -1, 0 > 
 }


// BOX _box0_1_2
box { < -0.5, -0.5, -0.5>,
 < 0.5, 0.5, 0.5>
 texture { pigment { color NeonPink } }
 scale  < 1.5, 1.5, 1.5 > 
 rotate  < 0, 45, 0 > 
 translate  < -1, 1, 0 > 
 }


