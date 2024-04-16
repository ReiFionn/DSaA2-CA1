---------------------------------------------------------------------------	
DSaA2 - CA1 : PILL AND CAPSULE ANALYSER
---------------------------------------------------------------------------

> user supplies image
> 
> system displays image
> 
> user can select one or more pills by clicking them
> 
> user will be able to specify a name for the pill, with some search parameters such as min/max pill size, colour range settings/thresholds
> 
> system then locates the pills in the image and estimate how many there are in the image overall
> 
> system should mark location of pills in the image with coloured rectangles

---------------------------------------------------------------------------		
IMPLEMENTATION NOTES
---------------------------------------------------------------------------

- key aspect is to use a union-find algorithm to locate pills

- image should be converted to b&w in the first instance pixel by pixel using suitable luminance/hue/saturation/brightness/RGB calculations
> pixels belonging to a pill should be white, anything else black
> 
> users should be able to select the pill types in the image to location, done primarily by colour where the user clicks on a sample of the pill type to locate so a colour sample can be taken from the selected pixel then, when converting to b&w, any pixel in the image with a colour sufficiently like any selected pill sample colour will be white, all other will be black.
> 
> use hue, saturation, brightness, RGB channels, etc. to determine pixel colour similarity
> 
> allow the user to specify parameters for colour similarity settings/thresholds when pills are selected ***THIS IS CRUCIAL TO ACHIEVE A CLEAN BLACK AND WHITE CONVERSION FOR THE UNION-FIND TO WORK WELL FOR A GIVEN IMAGE***
> 
> users should be able to view the b&w version in a separate window/tab etc.

- each pixel in the b&w image can initially be considered a disjoint set, this info potentially represented in an array. Union-find can then be applied to union adjacent white pixels to identify pills of any type, black pixels can be disregarded
> you may resize the image from its original size for the recognition process

- once the union-find is complete, the individual disjoint sets can be processed to identify the set pixel boundaries. The size of the boundary can suggest whether a disjoint set is likely to be a valid pill or not. Coloured rectangles based on the boundaries can subsequently be superimposed on the original image to identify pills

- the user should be able to see the number of pixel units within a given pill (the exact size of the disjoint set representing the pill), along with the pill name, perhaps allowing users to interactively query it (touching the pill with the mouse shows a pop-up etc.)

- every pill should be sequentially numbered in sorted order whereby the pill that is nearest the top of the image is 1, next nearest is 2, etc. These numbers should be visible in some way alongside the pill size and name.

- a menu/user option should also be provided to allow for the top-down position-ordered sequential numbers to be shown onscreen in or alongside the rectangles.

- it should be possible to see the pixels of indiviual disjoints sets in the b&w image, possibly by:
> colouring all white pixels in the b&w image to their pill type sample colours
> 
> randomly colouring the various disjoint sets in the b&w image

- there should be support for the recognition of two-tone pills, whereby the colours of both ends of the capsule are recorded and used to recognise whole capsules

- some level of image noise reduction and outlier management should be incorporated into the analyser to allow users to strip out disjoin sets that are heuristically unlikely to be pills
> application should allow for min & max pill sizes to be user specified
> 
> the size of the sampled pill's disjoint set could be used to identify similar pills based on size similarity as well as colour similarity
> 
> consider calculations based on the interquartile range (for instance) to identify outliers
> 
> as noted before, some user-adjustable settings could be provided to achieve a good and clean b&w image coversion before the union-find operation

- an appropriate interactive JavaFX GUI should be provieded to allow the users to:
> select an image file to use
> 
> view the original image
> 
> choose one or more pills by clicking on them to sample their colour. The names of the pills should also be recorded, along with similarity settings for colour and size
> 
> perform and view the b&w image conversion, using the user specified pill colour/size similarity settings
> 
> perform the pill recognition and mark pills on the original image with coloured rectangles
> 
> see estimates of the number of pills in the image and the pixel unit/disjoint set sizes
> 
> ordered sequential numbering of pills, including on screen labelling
> 
> visualise disjoint set pixels in the b&w image
> 
> adjust settings to manage noise and outliers to aid the b&w conversion
> 
> clearly navigate and exit the application and have a good user experience overall

---------------------------------------------------------------------------				
MARKING SCHEME
---------------------------------------------------------------------------

- Image file selection and display = 5%
- Black-and-white image conversion and display = 8%
- Union-find implementation = 10%
- Onscreen identification of all selected pills/capsules (using rectangles) = 10%
- Ordered sequential pill/capsule numbering (onscreen labelling) = 8%
- Estimating/counting of pills/capsules in overall image (total and by type/name) = 8%
- Reporting the size of individual disjoint sets (in pixel units) = 7%
- Colouring disjoint sets in black-and-white image (both sampled and random colours) = 8%
- Recognition of two-tone capsules = 8%
- Image noise reduction and outlier management = 8%
- JavaFX GUI = 5%
- JUnit Testing = 5%
- JMH Benchmarking of key methods = 5%
- General (overall completeness, structure, commenting, logic, etc.) = 5% 

---------------------------------------------------------------------------				
RESULT
---------------------------------------------------------------------------

- Image file selection and display
- Black-and-white image conversion and display
- Union-find implementation
- Onscreen identification of all selected pills/capsules (using rectangles)
- Ordered sequential pill/capsule numbering (onscreen labelling with ToolTip)
- Estimating/counting of pills/capsules in overall image (total and by type/name)
- Reporting the size of individual disjoint sets (in pixel units)
- Colouring disjoint sets in black-and-white image (both sampled and random colours)
- Minimal (and not functional) implementation of two-tone recognition
- Image noise reduction and outlier management
- JavaFX GUI
- JUnit Testing
- JMH Benchmarking of key methods (in separate project, combined together for this GitHub)

90%, 31.5% of module grade.
