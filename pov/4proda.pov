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
color White
}

// LIGHT _light4
light_source { < -10, 10, 10 > 
color White
}



// SPHERE _sphere1
sphere {  < 0, 0, 0 > 1
 texture { pigment { color Quartz } }
 scale  < 0.75+0.5*sin(clock*pi), 0.75+0.5*sin(clock*pi), 2 > 
 }


// SPHERE _sphere0_3
sphere {  < 0, 0, 0 > 1
 texture { pigment { color Quartz } }
 scale  < 1, 1.5, 1 > 
 rotate  < 0, 0, 45 > 
 translate  < -1+0.5*sin(clock*pi), 1-0.5*sin(clock*pi), 0 > 
 }


// SPHERE _sphere0_0_3
sphere {  < 0, 0, 0 > 1
 texture { pigment { color Quartz } }
 scale  < 1, 1.5, 1 > 
 rotate  < 0, 0, -45 > 
 translate  < 1-0.5*sin(clock*pi), 1-0.5*sin(clock*pi), 0 > 
 }


// SPHERE _sphere0_1_3
sphere {  < 0, 0, 0 > 1
 texture { pigment { color Quartz } }
 scale  < 1, 1.5, 1 > 
 rotate  < 0, 0, 135 > 
 translate  < -1+0.5*sin(clock*pi), -1+0.5*sin(clock*pi), 0 > 
 }


// SPHERE _sphere0_2_3
sphere {  < 0, 0, 0 > 1
 texture { pigment { color Quartz } }
 scale  < 1, 1.5, 1 > 
 rotate  < 0, 0, -135 > 
 translate  < 1-0.5*sin(clock*pi), -1+0.5*sin(clock*pi), 0 > 
 }

