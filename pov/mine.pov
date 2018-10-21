// POV Ray StarMines models - Copyright 1999-2018 Jari Karjala <jka@iki.fi>

#include "colors.inc"  // The include files contain
#include "shapes.inc"  // pre-defined scene elements
#include "textures.inc"

camera {
 right      < -1, 0, 0 >
 up         < 0, 1, 0 >
 direction  < 0, 0, 1 >
 location   < 0, 0, 7.5 >
 look_at    < 0, 0, 0 >
}
// LIGHT _light3
light_source { < 0, 0, 50 >
color White
}

// LIGHT _light4
light_source { < -10, 10, 10 >
color White
}
light_source { < -10, 10, 10 >
color White
}
light_source { < -10, 10, 10 >
color White
}
light_source { < -10, 10, 10 >
color White
}


object {
	union {
		box { <-0.05, -0.4, -0.05>, <0.05, 0.4, 0.05> }
		box { <-0.4, -0.05, -0.05>, <0.4, 0.05, 0.05> }
		box { <-0.05, -0.05, -0.4>, <0.05, 0.05, 0.4> }
		sphere { <0, 0, 0>, 0.25 }
	}
	scale <8, 8, 8>
	rotate <0, 0, 90*clock>
    // texture { pigment { color Copper } }
    texture { Rust }
 }

