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
 location   < 0, 0, 6 > 
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


















// CYLINDER _cylinder0
cylinder { < 0, -1, 0>, < 0, 1, 0>, 1
 texture { pigment { color NewTan } }
 translate  < -1.625, 0.78125, 0.604167 > 
 }

// BLOB _blob0
blob {  threshold 1
 component 1, 1, < 0.875, -1.5625, -0.0416665 >
 component 1, 1, < 0.583333, 1.125, -0.875 >
 component 1, 1, < -1.04167, -0.916667, -1.5 >
 pigment { White }
}
