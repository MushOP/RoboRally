package dk.dtu.compute.se.pisd.roborally.model;
import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Jens Olesen
 *
 */

public class Wall {
    public Heading heading;

    public Heading getHeading() {
        return this.heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }
}
