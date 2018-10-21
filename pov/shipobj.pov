// POV Ray StarMines models - Copyright 1999-2018 Jari Karjala <jka@iki.fi>

#include "colors.inc"
#include "textures.inc"
#include "shapes.inc"

// CAMERA POSITION
camera {
 right      < -1, 0, 0 > 
 up         < 0, 1, 0 > 
 direction  < 0, 0, 1 > 
 location   < 0, 0, 7.5 > 
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
 texture { pigment { color SlateBlue } }
 scale  < 2, 2, 1 > 
 }




#declare _sphere1 = 
// SPHERE _sphere1
sphere {  < 0, 0, 0 > 1
 texture { pigment { color Silver } }
 scale  < 2.75, 1.2, 1.2 > 
 translate  < 0.75, 0, 0 > 
 }


#declare _cylinder0 = 
// CYLINDER _cylinder0
cylinder { < 0, -1.375, 0>, < 0, 1.375, 0>, 0.5
 texture { pigment { color Silver } }
 rotate  < 0, 0, 90 > 
 translate  < -1, 0.45, 0.5 > 
 }


#declare _cylinder0_0 = 
// CYLINDER _cylinder0_0
cylinder { < 0, -1.375, 0>, < 0, 1.375, 0>, 0.5
 texture { pigment { color Silver } }
 rotate  < 0, 0, 90 > 
 translate  < -1, -0.45, 0.5 > 
 }


#declare _sphere2 = 
// SPHERE _sphere2
sphere {  < 0, 0, 0 > 1
 texture { Gold_Metal }
 scale  < 1, 0.5, 0.3 > 
 translate  < 1.5, 0, 1 > 
 }


#declare _cylinder0_0_1 = 
// CYLINDER _cylinder0_0_1
cylinder { < 0, -1, 0>, < 0, 1, 0>, 0.15
 texture { pigment { color Silver } }
 rotate  < 0, 0, 90 > 
 translate  < -1, -1.75, 0.25 > 
 }


#declare _cylinder0_0_1_2 = 
// CYLINDER _cylinder0_0_1_2
cylinder { < 0, -1, 0>, < 0, 1, 0>, 0.15
 texture { pigment { color Silver } }
 rotate  < 0, 0, 90 > 
 translate  < -1, 1.75, 0.25 > 
 }


