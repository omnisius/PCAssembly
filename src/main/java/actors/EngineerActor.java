package actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.PC;

public class EngineerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private PC.PCBuilder currentPC;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    this.getSender().tell("Received PC part: '" + s + "'", this.self());
                    log.info("Received PC part: {}", s);
                    currentPC.addCPU(s);
                })
                .matchAny(o -> log.info(this.self().toString() + " received unknown part"))
                .build();
    }

    public PC.PCBuilder getCurrentPC() {
        return currentPC;
    }
}
