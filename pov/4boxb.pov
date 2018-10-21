// Persistence Of Vision raytracer version 3.0 sample file.

#version 3.0
global_settings { assumed_gamma 2.2 }


#include "colors.inc"
#include "textures.inc"
#include "shapes.inc"

// CAMERA POSITION
camera {
 right      < -1, 0, 0 > 
 up         < 0, 1, 0 > 
 direction  < 0, 0, 1 > 
 location   < 0, 0, 5.5 > 
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

// BOX _box0
box { < -0.5, -0.5, -0.5>,
 < 0.5, 0.5, 0.5>
 texture { pigment { color NeonPink } }
 scale  < 1.5, 1.5, 1.5 > 
 rotate  < 0, -clock*90, -clock*90 > 
 translate  < 1, 1, 0 > 
 }


// BOX _box0_0
box { < -0.5, -0.5, -0.5>,
 < 0.5, 0.5, 0.5>
 texture { pigment { color NeonPink } }
 scale  < 1.5, 1.5, 1.5 > 
 rotate  < 0, clock*90, clock*90 > 
 translate  < 1, -1, 0 > 
 }


// BOX _box0_1
box { < -0.5, -0.5, -0.5>,
 < 0.5, 0.5, 0.5>
 texture { pigment { color NeonPink } }
 scale  < 1.5, 1.5, 1.5 > 
 rotate  < 0, -clock*90, -clock*90 > 
 translate  < -1, -1, 0 > 
 }


// BOX _box0_1_2
box { < -0.5, -0.5, -0.5>,
 < 0.5, 0.5, 0.5>
 texture { pigment { color NeonPink } }
 scale  < 1.5, 1.5, 1.5 > 
 rotate  < 0, clock*90, clock*90 > 
 translate  < -1, 1, 0 > 
 }
