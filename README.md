# EarthboundBG

######An Android live wallpaper that displays the battle (and other) backgrounds from Earthbound / MOTHER 2

There is already [another live wallpaper][EBBG2] that does the same thing, but that one has quite a few drawbacks:

* Costs money (though you can compile it yourself for free)
* Stretches and letterboxes the backgrounds
* Rather slow, due to JNI and software rendering
* No animated palettes

EarthboundBG has none of those problems:

* Completely free
* No stretching, instead it repeats the background in all directions
* Very fast, due to using OpenGL (last time I checked, <1ms per frame)
* Supports animated palettes

EarthboundBG also has a less awkward settings menu:

* Use as many background layers as you want
* Preview the background without leaving the menu
* Thumbnails in all layer lists
* Includes a few non-battle backgrounds in the preset list

##Known bugs

* On some phones, the layer lists are white text on white background. (Workaround: hold down on an entry and it will become blue text on blue background)

[EBBG2]: https://github.com/gjtorikian/Earthbound-Battle-Backgrounds/
