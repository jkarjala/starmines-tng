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
 location   < 0, 0, 4.5 >
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






#declare _sphere4 =
// SPHERE _sphere4
sphere {  < 0, 0, 0 > 1
 texture { pigment { color MediumSpringGreen  } }
 translate  < -0.75, -0.75, 0 >
 }


#declare _sphere4_0 =
// SPHERE _sphere4_0
sphere {  < 0, 0, 0 > 1
 texture { pigment { color MediumSpringGreen  } }
 translate  < 0.75, 0.75, 0 >
 }


#declare _sphere4_1 =
// SPHERE _sphere4_1
sphere {  < 0, 0, 0 > 1
 texture { pigment { color MediumSpringGreen  } }
 translate  < -0.75, 0.75, 0 >
 }


#declare _sphere4_1_2 =
// SPHERE _sphere4_1_2
sphere {  < 0, 0, 0 > 1
 texture { pigment { color MediumSpringGreen  } }
 translate  < 0.75, -0.75, 0 >
 }


union {
  object { _sphere4 scale (0.75 + 0.3*sin(clock*pi)) }
  object { _sphere4_0 scale (0.75 + 0.3*sin(clock*pi))}
  object { _sphere4_1 scale (1.0 - 0.3*sin(clock*pi))}
  object { _sphere4_1_2 scale (1.0 - 0.3*sin(clock*pi))}
}

