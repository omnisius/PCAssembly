package manufactory;


import actors.ChiefActor;
import actors.EngineerActor;
import actors.HucksterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import model.PC;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

class PCManufactory {
    private static final String PC_MANUFACTORY = "PC_Manufactory";
    private static final String HUCKSTER = "Huckster";
    private static final String ENGINEER = "Engineer";
    private static final String CHIEF_ENGINEER = "Chief_Engineer";
    private static final String DETAIL_CONFIRM_MESSAGE = "Detail are compatible.";
    private static final String COMPLETE_MESSAGE = "PC is done ";
    private static final String PARTS_HAS_ARRIVED = "Parts have arrived";
    private static final String HDD_MODEl = "HDD:500 Gb HDD";
    private static final String CPU_MODEl = "CPU:Intel Core i5";
    private static final String MOTHERBOARD_MODEl = "MB:SMTH13";
    private static final String RAM_MODEl = "RAM:8 Gb DDR3 L";
    private static final int TIMEOUT_MILLIS = 1000;

    static void createPC()  throws ExecutionException, InterruptedException {
        ActorSystem system = ActorSystem.create(PC_MANUFACTORY);
        ActorRef chiefEngineer = system.actorOf(Props.create(ChiefActor.class), CHIEF_ENGINEER);
        PC.PCBuilder currentPC = new PC.PCBuilder();

        ActorRef huckster = system.actorOf(Props.create(HucksterActor.class), HUCKSTER);

        CompletableFuture<Object> hucksterFuture = ask(huckster, PARTS_HAS_ARRIVED, TIMEOUT_MILLIS).toCompletableFuture();

        ActorRef engineer = system.actorOf(Props.create(EngineerActor.class), ENGINEER);
        engineer.tell(currentPC, chiefEngineer);
        engineer.tell(HDD_MODEl, huckster);
        engineer.tell(RAM_MODEl, huckster);
        engineer.tell(CPU_MODEl, huckster);
        engineer.tell(MOTHERBOARD_MODEl, huckster);

        Timeout slowEngineer = new Timeout(Duration.create(2, TimeUnit.SECONDS));
        CompletableFuture<Object> engineerFuture = ask(huckster, DETAIL_CONFIRM_MESSAGE, slowEngineer)
                .toCompletableFuture();

        CompletableFuture<String> chiefEngineerFuture =
                CompletableFuture.allOf(hucksterFuture, engineerFuture)
                        .thenApply(v -> {
                            hucksterFuture.join();
                            engineerFuture.join();
                            return COMPLETE_MESSAGE;
                        });

        pipe(chiefEngineerFuture, system.dispatcher()).to(chiefEngineer);
        Thread.sleep(TIMEOUT_MILLIS);
        if (chiefEngineerFuture.isDone())
            System.out.println(chiefEngineerFuture.get());
    }
}
