package actors;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class HucksterActor extends EngineerActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
}
