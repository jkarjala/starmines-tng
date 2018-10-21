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
 location   < 0, 0, 7 > 
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



#declare _sphere0 = 
// SPHERE _sphere0
sphere {  < 0, 0, 0 > 1
 texture { Chrome_Texture }
 scale  < 1.75, 0.75, 0.75 > 
 }





#declare _cone0 = 
// CONE _cone0
cone { <0, -1, 0>, 1
  < 0, 1, 0>, 0
 texture { pigment { color Orange } }
 scale  < 0.5, 2.5, 2 > 
 rotate  < 90, 90, 0 > 
 translate  < 0.75, 0, 0 > 
 }




#declare _cone0_0 = 
// CONE _cone0_0
cone { <0, -1, 0>, 1
  < 0, 1, 0>, 0
 texture { pigment { color Red } }
 scale  < 1, 2.75, 1 > 
 rotate  < 90, 90, 0 > 
 translate  < 0.5, 0, 0 > 
 }


