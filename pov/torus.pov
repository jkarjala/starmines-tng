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
light_source { < 0, 0, 22 >
color White
}

// LIGHT _light4
light_source { < -10, 10, 10 >
color White
}

// TORUS _torus0
torus { 2, 0.5
 texture { pigment { color Silver } }
 rotate  < 90, clock*180, 0 >
 }


// SPHERE _sphere4
sphere {  < 0, 0, 0 > 1
 texture { pigment { color SummerSky } }
 scale  < 1.0, 1.0, 1.0 >
 }
