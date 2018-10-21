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
















// TORUS _torus0
torus { 1.8, 0.2 
 texture { Chrome_Texture }
 rotate  < 90, 0, 90 > 
 translate  < 0, 0, 1 > 
 }


// TORUS _torus0_0_1
torus { 1.8, 0.2 
 texture { Chrome_Texture }
 rotate  < 90, 0, 90 > 
 translate  < 0, 0, -5 > 
 }


// SPHERE _sphere0
sphere {  < 0, 0, 0 > 1
 texture { Chrome_Texture }
 scale  < 0.2, 0.2, 0.2 > 
 translate  < 0, 1.75, 0 > 
 }


// SPHERE _sphere0_2
sphere {  < 0, 0, 0 > 1
 texture { Chrome_Texture }
 scale  < 0.2, 0.2, 0.2 > 
 translate  < 0, -1.75, 0 > 
 }


// SPHERE _sphere0_2_3
sphere {  < 0, 0, 0 > 1
 texture { Chrome_Texture }
 scale  < 0.2, 0.2, 0.2 > 
 translate  < 1.75, 0, 0 > 
 }


// SPHERE _sphere0_2_3_4
sphere {  < 0, 0, 0 > 1
 texture { Chrome_Texture }
 scale  < 0.2, 0.2, 0.2 > 
 translate  < -1.75, 0, 0 > 
 }

