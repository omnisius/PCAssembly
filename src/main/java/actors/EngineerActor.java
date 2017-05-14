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
                .match(PC.PCBuilder.class, pc ->{
                    currentPC = pc;
                    this.getSender().tell(pc, this.self());
                    log.info("Received PC template: {}", pc.build());
                })
                .match(String.class, s -> {
                    if(s.startsWith("HDD")) {
                        currentPC.addHDD(s.replace("HDD:", ""));
                    } else if (s.startsWith("CPU")) {
                        currentPC.addCPU(s.replace("CPU:", ""));
                    } else if (s.startsWith("RAM")) {
                        currentPC.addRAM(s.replace("RAM:", ""));
                    }
                    this.getSender().tell(currentPC, this.self());
                    log.info("Received PC part: {}", s);
                    log.info("PC in stage: {}", currentPC.build());
                })
                .matchAny(o -> log.info(this.self().toString() + " received unknown part"))
                .build();
    }
}
