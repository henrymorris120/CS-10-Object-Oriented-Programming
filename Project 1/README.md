This project builds a webcam-based painting program in which some portion of the image acts as a "paintbrush." The detection of regions occurs within the class RegionFinder in the method find
regions(Color targetColor). The parameter is obviously color, which is used as a parameter
whenever the method colorMatch is called to see if a pixel in image has a color relatively close to
color. Overally, the method regionFinder works by first creating a buffered image called visited
that will be used to determine if a pixel has been checked yet as well as an array list called
regions that holds array lists which will represent each region. Then a nested loop is used to check
every pixel in the buffered image instance variable image. If that point has not been visited and
has a color close to targetColor, an array list is created called region and another called neighbors,
in which that point is added to both array lists and marked as visited. Then, a while loop is called
until the length of the neighbors list is zero, within which the last point in neighbors is popped and
all its neighbors are checked to see if that neighbor point has not been visited and has the right
color, and if these are both true, the neighbor point is added to neighbors and region. When the
loop ends, the length of the array list region is checked and if it is at least a certain size, then it is
added to regions. The nested loop of course moves on to the next point and continues this process
until each point has been checked, though of course for efficiency reasons most points don’t go
through all this because they have already been visited. At the end, regions holds each region.
The method is called for Baker and the webcam but Baker uses all the regions in region while the
webcam uses just the largest region in terms of size in regions - there are methods in the class
that do just this for recoloredImage.
