// POV Ray StarMines models - Copyright 1999-2018 Jari Karjala <jka@iki.fi>

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
// LIGHT _light4
light_source { < -10, 10, 10 > 
color White
}

// LIGHT _light4_10
light_source { < -10, -10, 10 > 
color White
}

// LIGHT _light4_10_11
light_source { < 10, 10, 10 > 
color White
}

// LIGHT _light4_10_11_12
light_source { < 10, -10, 10 > 
color White
}





#declare _cone0 = 
// CONE _cone0
cone { <0, -1, 0>, 1
  < 0, 1, 0>, 0
 texture { Silver1 }
 scale  < 2, 2, 3 > 
 rotate  < 180, 0, 0 > 
 translate  < 0, -1, 0 > 
 }


#declare _cone1 = 
// CONE _cone1
cone { <0, -1, 0>, 1
  < 0, 1, 0>, 0
 texture { Silver1 }
 scale  < 2, 2, 3 > 
 rotate  < 0, 90, 0 > 
 translate  < 0, 1, 0 > 
 }





#declare _sphere3 = 
// SPHERE _sphere3
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 scale  < 0.5, 0.5, 0.5 > 
 translate  < 0, 1.5, 2 > 
 }


#declare _sphere3_7 = 
// SPHERE _sphere3_7
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 scale  < 0.5, 0.5, 0.5 > 
 translate  < 2, -1.5, 0 > 
 }


#declare _sphere3_7_8 = 
// SPHERE _sphere3_7_8
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 scale  < 0.5, 0.5, 0.5 > 
 translate  < -2, -1.5, 0 > 
 }


#declare _sphere3_7_8_9 = 
// SPHERE _sphere3_7_8_9
sphere {  < 0, 0, 0 > 1
 texture { Bright_Blue_Sky }
 scale  < 0.5, 0.5, 0.5 > 
 translate  < 0, 1.5, -2 > 
 }


