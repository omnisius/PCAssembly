package actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class HucksterActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    this.getSender().tell("Can sell you PC part: '" + s + "'", this.self());
                    log.info("Received PC part: {}", s);
                })
                .matchAny(o -> log.info(this.self().toString() + " received unknown part" + o))
                .build();
    }
}
