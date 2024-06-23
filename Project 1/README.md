This project builds a webcam-based painting program in which some portion of the image acts as a "paintbrush." The detection of regions occurs within the class RegionFinder in the method find
regions(Color targetColor). It follows a basic flood fill algorithm, described in pseudocode below

    Loop over all the pixels
      If a pixel is unvisited and of the correct color
        Start a new region
        Keep track of which pixels need to be visited, initially just that one
        As long as there's some pixel that needs to be visited
          Get one to visit
          Add it to the region
          Mark it as visited
          Loop over all its neighbors
            If the neighbor is of the correct color
              Add it to the list of pixels to be visited
        If the region is big enough to be worth keeping, do so
