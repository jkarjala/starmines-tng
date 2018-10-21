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
light_source { < 0, 0, 22 > 
color White
}

// LIGHT _light4
light_source { < -10, 10, 10 > 
color White
}






// SPHERE _sphere4
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 translate  < -1, -1, 0 > 
 }


// SPHERE _sphere4_0
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 translate  < 1, 1, 0 > 
 }


// SPHERE _sphere4_1
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 translate  < -1, 1, 0 > 
 }


// SPHERE _sphere4_1_2
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 translate  < 1, -1, 0 > 
 }


// SPHERE _sphere4_3
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 translate  < 0, 0, 1 > 
 }


// SPHERE _sphere4_3_4
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 translate  < 0, 0, -1 > 
 }

